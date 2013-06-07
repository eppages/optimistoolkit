package eu.optimis.dm.dmclient_s3wrapper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author oriol.collell
 *
 */
public class App  {
    
    private static final String DEFAULT_PROPERTIES_PATH = "wrapper.properties";
    private static final String PATH_OPT = "c";
    private static final String AWS_ACCESS_PROP = "AWSAccessKey";
    private static final String AWS_SECRET_PROP = "AWSSecretKey";
    
    private static String SERVICE_ID = "d3f64f3f-c57b-460c-97d4-9b84c576c397"; //Random service id for testing
    
    private static String propertiesPath = DEFAULT_PROPERTIES_PATH;
    private static String accessKey;
    private static String secretKey;
    
    private JButton button;
    private JButton buttonDownload;
    private JButton buttonDelete;
    private JFrame frame;
    private JTextField text;
    private JTextField serviceIdText;
    private JProgressBar pb;
    private JList list;
    private JScrollPane listScroller;
    private DefaultListModel listModel;
    
    public static void main( String[] args ) {
        try {
            Options options = new Options();
            Option conf = OptionBuilder.withArgName("configuration path").hasArg().withDescription("Configuration path").create(PATH_OPT);
            options.addOption(conf);

            CommandLineParser parser = new GnuParser();
            CommandLine line = null;
            try {
                line = parser.parse(options, args);
            }
            catch (ParseException exp) {
                System.err.println("Incorrect parameters: " + exp.getMessage());
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("collector", options);
                System.exit(2); //Incorrect usage
            }

            if (line.hasOption(PATH_OPT)) {
                propertiesPath = line.getOptionValue(PATH_OPT);
                System.out.println("Using Properties file: " + propertiesPath);
            } else {
                System.out.println("Using default Configuration file: " + DEFAULT_PROPERTIES_PATH
                        + " (To use custom path use -c argument)");
                propertiesPath = DEFAULT_PROPERTIES_PATH;
            }

            getProperties();
            App app = new App();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void getProperties() {
        accessKey = getStringProperty(AWS_ACCESS_PROP);
        if (accessKey.isEmpty()) {
            throw new RuntimeException("No Access Key specified in the properties file");
        }
        
        secretKey = getStringProperty(AWS_SECRET_PROP);
        if (secretKey.isEmpty()) {
            throw new RuntimeException("No Secret Key specified in the properties file");
        }
    }
    
    public static String getStringProperty(String key) {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(propertiesPath);
            props.load(in);

            String defaultValue = props.getProperty(key);
            return System.getProperty(key, defaultValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public App() throws Exception {
        frame = new JFrame("Amazon S3 Image Upload");
        button = new JButton("Choose File...");
        button.addActionListener(new ButtonListener());
        buttonDownload = new JButton("Download Selected Image");
        buttonDownload.addActionListener(new DownloadButtonListener());
        buttonDelete = new JButton("Delete Selected Image");
        buttonDelete.addActionListener(new DeleteButtonListener());
        
        text = new JTextField();  
        text.setColumns(25);
        text.setEditable(false);
        
        serviceIdText = new JTextField();  
        serviceIdText.setColumns(40);
        serviceIdText.setText(SERVICE_ID);
        
        list = new JList();
        listModel = new DefaultListModel();
        updateListModel();
        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(-1);
        listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(200, 150));
        
        pb = new JProgressBar(0, 100);
        pb.setStringPainted(true);

        frame.setContentPane(createContentPane());
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    class ProgressUpdater extends Thread {
        
        S3Wrapper s3;
        App app;
        String name;
        
        public ProgressUpdater(S3Wrapper s3, App app) {
            this.s3 = s3;
            this.app = app;
            this.name = "";
        }
        
        public ProgressUpdater(S3Wrapper s3, App app, String name) {
            this.s3 = s3;
            this.app = app;
            this.name = name;
        }
        
        @Override
        public void run() {
            while (s3.getProgress() < 100) {
                pb.setValue(s3.getProgress());
            }
            pb.setValue(s3.getProgress());
            app.finishUpload(name);
        }
    }
    
    final App me = this;
    
    class ButtonListener implements ActionListener {
        
        public void actionPerformed(ActionEvent ae) {
            JFileChooser fileChooser = new JFileChooser();
            int showOpenDialog = fileChooser.showOpenDialog(frame);
            if (showOpenDialog != JFileChooser.APPROVE_OPTION) return;
            
            S3Wrapper s3 = new S3Wrapper(accessKey, secretKey);
            try {
                File selFile = fileChooser.getSelectedFile();
                URL url = s3.uploadImage(serviceIdText.getText(), selFile.getPath());
                ProgressUpdater updater = new ProgressUpdater(s3, me, selFile.getName());
                updater.start();
                text.setText(url.toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, 
                    "Unable to upload file to Amazon S3: " + e.getMessage(), 
                    "Error Uploading File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class DownloadButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {   
            if (!list.isSelectionEmpty()) {
                String file = (String) list.getSelectedValue();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(file));
                int showSaveDialog = fileChooser.showSaveDialog(frame);
                if (showSaveDialog != JFileChooser.APPROVE_OPTION) return;
                
                S3Wrapper s3 = new S3Wrapper(accessKey, secretKey);
                try {
                    File selDir = fileChooser.getSelectedFile();
                    s3.downloadImage(serviceIdText.getText(), file, selDir.getPath());
                    ProgressUpdater updater = new ProgressUpdater(s3, me);
                    updater.start();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Unable to upload file to Amazon S3: " + e.getMessage(), 
                        "Error Uploading File", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    class DeleteButtonListener implements ActionListener {
        
        public void actionPerformed(ActionEvent ae) {   
            if (!list.isSelectionEmpty()) {
                String file = (String) list.getSelectedValue();
                
                S3Wrapper s3 = new S3Wrapper(accessKey, secretKey);
                try {
                    s3.deleteImage(serviceIdText.getText(), file);
                    listModel.removeElement(file);
                    JOptionPane.showMessageDialog(frame, "File deleted successfully", 
                        "Delete Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Unable to upload file to Amazon S3: " + e.getMessage(), 
                        "Error Uploading File", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public void finishUpload(String name) {
        String msg = "";
        if (name.isEmpty()) {
            msg = "Download complete!";
        } else {
            msg = "Upload complete!";
        }
        JOptionPane.showMessageDialog(frame, msg, 
            "Transfer Complete", JOptionPane.INFORMATION_MESSAGE);
        if (!name.isEmpty()) listModel.addElement(name);
    }
    
    private void updateListModel() {
        listModel.removeAllElements();
        S3Wrapper s3 = new S3Wrapper(accessKey, secretKey);
        for (String image : s3.listImages(serviceIdText.getText())) {
            listModel.addElement(image);
        }
    }
    
    private JPanel createContentPane() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c0 = createGBC(0,0);
        c0.gridwidth = 3;
        panel.add(serviceIdText, c0);
        panel.add(button, createGBC(0,1));
        panel.add(pb, createGBC(1,1));
        panel.add(text, createGBC(2,1));
        
        GridBagConstraints c1 = createGBC(0,2);
        c1.gridheight = 2;
        panel.add(listScroller,c1);
        panel.add(buttonDownload,createGBC(1,2));
        panel.add(buttonDelete,createGBC(1,3));
        
        return panel;
    }
    
    private GridBagConstraints createGBC(int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        
        return gbc;
    }
}
