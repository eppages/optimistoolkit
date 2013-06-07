/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package es.bsc.servicess.ide.dialogs;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import es.bsc.servicess.ide.Activator;
public class URLPatternDialog extends Dialog{
	
	private File webXML;
	private String appContext;
	private String urlPattern;
	private Combo urlList;

	public URLPatternDialog(Shell parent, File webXML, String appContext) {
		super(parent);
		this.webXML = webXML;
		this.appContext = appContext;
		this.urlPattern = "";
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		Label urlLabel = new Label(composite,SWT.BEGINNING);
		urlLabel.setText("Possible URL patterns for war"); 
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING); 
		rd.grabExcessHorizontalSpace = true; 
		urlLabel.setLayoutData(rd);
		urlList = new Combo (composite, SWT.READ_ONLY | SWT.BORDER |SWT.DEFAULT); 
		urlList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){ 
				urlPattern = appContext+urlList.getItem(urlList.getSelectionIndex()); 
			} 
		});
		rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		rd.grabExcessHorizontalSpace = true;
		urlList.setLayoutData(rd);
		//get possible URL patterns from from web.xml
		try{
			HashMap<String,List<String>> pattern = getServletURLPatterns(webXML);
			if(pattern != null && pattern.size()>0){ 
				urlList.removeAll();
				for (Object s :pattern.values()){
					for(String pat:(List<String>)s){
						urlList.add(pat);
					}
				}
				urlList.select(0); 
				urlPattern = this.appContext+urlList.getItem(0); 
			}
		}catch( Exception e){
			e.printStackTrace();
			ErrorDialog.openError(getShell(), "Error", "Reading parameters from the web.xml ", 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Exception: "+e.getLocalizedMessage()));
		}
		return composite;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	public void okPressed() {
		if (urlPattern != null && urlPattern.length()>0) {
			super.okPressed();
		} else {
			ErrorDialog.openError(getShell(), "Error", "Parameters missing", 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Type or pacakge name is null"));
		}

	}
	
	
	private HashMap getServletURLPatterns(File webXML)throws IOException, SAXException,
    ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false); 
		factory.setNamespaceAware(false); 
		SAXParser parser = factory.newSAXParser();
		ListServlets servlets = new ListServlets();
		parser.parse(webXML, servlets);
		return servlets.nameToPatterns;
	}
	
	public String getURLPattern(){
		return this.urlPattern;
	}
	
	
}

/**
 * Parse a web.xml file using the SAX2 API. This class extends DefaultHandler so
 * that instances can serve as SAX2 event handlers, and can be notified by the
 * parser of parsing events. We simply override the methods that receive events
 * we're interested in
 */
class ListServlets extends org.xml.sax.helpers.DefaultHandler {
  /** The main method sets things up for parsing */
  public static void main(String[] args) throws IOException, SAXException,
      ParserConfigurationException {

    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false); // We don't want validation
    factory.setNamespaceAware(false); // No namespaces please
    // Create a SAXParser object from the factory
    SAXParser parser = factory.newSAXParser();
    // Now parse the file specified on the command line using
    // an instance of this class to handle the parser callbacks
    parser.parse(new File(args[0]), new ListServlets());
  }

  HashMap nameToClass; // Map from servlet name to servlet class name

  HashMap nameToID; // Map from servlet name to id attribute

  HashMap nameToPatterns; // Map from servlet name to url patterns

  StringBuffer accumulator; // Accumulate text

  String servletName, servletClass, servletPattern; // Remember text

  String servletID; // Value of id attribute of <servlet> tag

  // Called at the beginning of parsing. We use it as an init() method
  public void startDocument() {
    accumulator = new StringBuffer();
    nameToClass = new HashMap();
    nameToID = new HashMap();
    nameToPatterns = new HashMap();
  }

  // When the parser encounters plain text (not XML elements), it calls
  // this method, which accumulates them in a string buffer.
  // Note that this method may be called multiple times, even with no
  // intervening elements.
  public void characters(char[] buffer, int start, int length) {
    accumulator.append(buffer, start, length);
  }

  // At the beginning of each new element, erase any accumulated text.
  public void startElement(String namespaceURL, String localName, String qname,
      Attributes attributes) {
    accumulator.setLength(0);
    // If its a servlet tag, look for id attribute
    if (qname.equals("servlet"))
      servletID = attributes.getValue("id");
  }

  // Take special action when we reach the end of selected elements.
  // Although we don't use a validating parser, this method does assume
  // that the web.xml file we're parsing is valid.
  public void endElement(String namespaceURL, String localName, String qname) {
    // Since we've indicated that we don't want name-space aware
    // parsing, the element name is in qname. If we were doing
    // namespaces, then qname would include the name, colon and prefix,
    // and localName would be the name without the the prefix or colon.
    if (qname.equals("servlet-name")) { // Store servlet name
      servletName = accumulator.toString().trim();
    } else if (qname.equals("servlet-class")) { // Store servlet class
      servletClass = accumulator.toString().trim();
    } else if (qname.equals("url-pattern")) { // Store servlet pattern
      servletPattern = accumulator.toString().trim();
    } else if (qname.equals("servlet")) { // Map name to class
      nameToClass.put(servletName, servletClass);
      nameToID.put(servletName, servletID);
    } else if (qname.equals("servlet-mapping")) {// Map name to pattern
      List patterns = (List) nameToPatterns.get(servletName);
      if (patterns == null) {
        patterns = new ArrayList();
        nameToPatterns.put(servletName, patterns);
      }
      patterns.add(servletPattern);
    }
  }

  // Called at the end of parsing. Used here to print our results.
  public void endDocument() {
    // Note the powerful uses of the Collections framework. In two lines
    // we get the key objects of a Map as a Set, convert them to a List,
    // and sort that List alphabetically.
    List servletNames = new ArrayList(nameToClass.keySet());
    Collections.sort(servletNames);
    // Loop through servlet names
    for (Iterator iterator = servletNames.iterator(); iterator.hasNext();) {
      String name = (String) iterator.next();
      // For each name get class and URL patterns and print them.
      String classname = (String) nameToClass.get(name);
      String id = (String) nameToID.get(name);
      List patterns = (List) nameToPatterns.get(name);
      System.out.println("Servlet: " + name);
      System.out.println("Class: " + classname);
      if (id != null)
        System.out.println("ID: " + id);
      if (patterns != null) {
        System.out.println("Patterns:");
        for (Iterator i = patterns.iterator(); i.hasNext();) {
          System.out.println("\t" + i.next());
        }
      }
      System.out.println();
    }
  }

  // Issue a warning
  public void warning(SAXParseException exception) {
    System.err
        .println("WARNING: line " + exception.getLineNumber() + ": " + exception.getMessage());
  }

  // Report a parsing error
  public void error(SAXParseException exception) {
    System.err.println("ERROR: line " + exception.getLineNumber() + ": " + exception.getMessage());
  }

  // Report a non-recoverable error and exit
  public void fatalError(SAXParseException exception) throws SAXException {
    System.err.println("FATAL: line " + exception.getLineNumber() + ": " + exception.getMessage());
    throw (exception);
  }
}
