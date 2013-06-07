/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package net.emotivecloud.scheduler.drp4ost;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author smendoza
 */
public class ImageMerge {

    //Mounting Paths
    private static final String mountPointISO = "/tmp/mnt.ost.A";
    private static final String mountPointQCOW2 = "/tmp/mnt.ost.B";
    //Script file names
    private static final String scriptMerge = "merge_imgs.sh";
    private static final String scriptMountQCOW2 = "mount_qcow2.sh";
    private static final String scriptUmountQCOW2 = "umount_qcow2.sh";
    private static final String scriptMountISO = "mount_iso.sh";
    private static final String scriptUmountISO = "umount_iso.sh";

    public ImageMerge() {
    }

    public static void merge(String isoImage, String osImage) {

        System.out.println("DRP4OST-ImageMerge.merge() > isoImage=" + isoImage + ", osImage=" + osImage);

        //TODO: create mounting points if not exist

        //Set all scripts executable
        String[] allscripts = {scriptMerge, scriptMountQCOW2, scriptUmountQCOW2, scriptMountISO, scriptUmountISO};
        setExecutableAll(allscripts);

        //Create the command and its parameters
        String scriptPath = ImageMerge.class.getClassLoader().getResource(scriptMerge).getPath();
        String home = scriptPath.substring(0, scriptPath.indexOf(scriptMerge));
        String cmd = scriptPath + " " + osImage + " " + mountPointQCOW2 + " " + isoImage + " " + mountPointISO + " " + home;
        System.out.println("DRP4OST-ImageMerge.merge() > cmd = " + cmd);

        try {
            System.out.println("DRP4OST-ImageMerge.merge() >");
            //Execute the merge command
            Runtime rt = Runtime.getRuntime();
//            Process p = rt.exec(cmd);
            Process p = rt.exec(cmd);

            InputStream pis = p.getInputStream();
            InputStream pise = p.getErrorStream();
            String execOutput = ISToString(pis);
//            System.out.println("DRP4OST-ImageMerge.execute() > OUTPUT - std output: \n" + execOutput);
            System.out.println("DRP4OST-ImageMerge.execute() > OUTPUT - error output: \n" + ISToString(pise));
            pis.close();
        } catch (Exception e) {
            System.out.println("DRP4OST-ImageMerge.merge() > Exception while executing script " + scriptPath);
        }

    }

    public static String copy(String source, String dest) {

        System.out.println("DRP4OST-ImageMerge.copy() > source=" + source + ", dest=" + dest);
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add("cp -R");
        cmd.add(source);
        cmd.add(dest);

        String cmdS = "/sbin/cp -Rf " + source + "/* " + dest;
        System.out.println("DRP4OST-ImageMerge.copy() > cmd=" + cmdS);

        try {

            Runtime rt = Runtime.getRuntime();
//            Process p = rt.exec(cmdS);

//            Process p2 = rt.exec("sudo sync");


        ProcessBuilder pb = new ProcessBuilder(cmdS);
//        pb.redirectErrorStream(true);
            pb.command(cmdS);
            Process p = pb.start();
            System.out.println("DRP4OST-ImageMerge.copy() > Process START");
            InputStream pis = p.getInputStream();
            p.waitFor();
            System.out.println("DRP4OST-ImageMerge.execute() > OUTPUT: " + ISToString(pis));
            pis.close();

            System.out.println("DRP4OST-ImageMerge.copy() > Process START");
            InputStream pis2 = p.getInputStream();
            p.waitFor();
            System.out.println("DRP4OST-ImageMerge.execute() > OUTPUT: " + ISToString(pis2));
            pis.close();

            ls(dest);


        } catch (Exception e) {
            System.out.println("DRP4OST-ImageMerge.copy() > exception copying");
            e.printStackTrace();
        }

        return null;
    }

    public static String execute(ArrayList<String> cmd) {
        String execOutput = "DRP4OST-ImageMerge.execute() output:";
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.command(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            System.out.println("DRP4OST-ImageMerge.execute() > Process START");
            p.waitFor();
            System.out.println("DRP4OST-ImageMerge.execute() > Process FINISHED (been p.waitFor())");
            InputStream pis = p.getInputStream();
            execOutput = ISToString(pis);
            System.out.println("DRP4OST-ImageMerge.execute() > OUTPUT: " + execOutput);
            pis.close();
        } catch (Exception e) {
            System.out.println("DRP4OST-ImageMerge.execute() > Exception: ");
            e.printStackTrace();
        }
        return execOutput;
    }

    public static String execute(String cmd) {
        String execOutput = "DRP4OST-ImageMerge.execute() output:";
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.command(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            System.out.println("DRP4OST-ImageMerge.execute() > Process START");
            p.waitFor();
            System.out.println("DRP4OST-ImageMerge.execute() > Process FINISHED (been p.waitFor())");
            InputStream pis = p.getInputStream();
            execOutput = ISToString(pis);
            System.out.println("DRP4OST-ImageMerge.execute() > OUTPUT: " + execOutput);
            pis.close();
        } catch (Exception e) {
            System.out.println("DRP4OST-ImageMerge.execute() > Exception: ");
            e.printStackTrace();
        }
        return execOutput;
    }

    public static String ISToString(InputStream is) throws Exception {

        byte[] buffer = new byte[1024];
        int read = 0;
        String str = new String();
        while ((read = is.read(buffer)) != -1) {
            str += new String(buffer);
        }
        return str;
    }

    public static String ls(String path) {

        System.out.println("DRP4OST-ImageMerge.ls() > enter");
//        ArrayList<String> cmd = new ArrayList<String>();
//        cmd.add("ls");
//        cmd.add("-lah");

        String cmd = "ls -lah " + path;

        String output = execute(cmd);
        System.out.println("DRP4OST-ImageMerge.ls() > execoutput: " + output);

        return null;
    }

    public static String pwd() {

        System.out.println("DRP4OST-ImageMerge.pwd() > enter");
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add("pwd");

        String output = execute(cmd);
        System.out.println("DRP4OST-ImageMerge.pwd() > execoutput: " + output);

        return null;
    }

    public static void setExecutableAll(String[] files) {

        for (String file : files) {
            String scriptPath = ImageMerge.class.getClassLoader().getResource(file).getPath();
            setExecutable(scriptPath);
        }
    }

    public static void setExecutable(String path) {
        File file = new File(path);

        if (file.exists()) {
            boolean bval = file.setExecutable(true);
            System.out.println("DRP4OST-ImageMerge.setExecutable: " + bval);
        } else {
            System.out.println("DRP4OST-ImageMerge.chmod - File not exists, path=" + path);
        }

        if (file.exists()) {
            boolean bval = file.setExecutable(true, false);
            System.out.println("DRP4OST-ImageMerge.setExecutable: " + bval);
        } else {
            System.out.println("DRP4OST-ImageMerge.chmod - File not exists, path=" + path);
        }
    }

    public static String mountISO(String imgPath, String mountPoint) {

        String scriptPath = ImageMerge.class.getClassLoader().getResource(scriptMountISO).getPath();
        System.out.println("DRP4OST-ImageMerge.mountISO() > imgPath=" + imgPath + ", mountPoint=" + mountPoint + ", scriptPath=" + scriptPath);

        setExecutable(scriptPath);

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(scriptPath);
        cmd.add(imgPath);
        cmd.add(mountPoint);

        execute(cmd);

        return null;
    }

    public static String umountISO(String mountPoint) {

        String scriptPath = ImageMerge.class.getClassLoader().getResource(scriptUmountISO).getPath();
        System.out.println("DRP4OST-ImageMerge.umountISO() > mountPoint=" + mountPoint + ", scriptPath=" + scriptPath);

        setExecutable(scriptPath);

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(scriptPath);
        cmd.add(mountPoint);

        execute(cmd);

        return null;
    }

    public static String mountQCOW2(String imgPath, String mountPoint) {

        String scriptPath = ImageMerge.class.getClassLoader().getResource(scriptMountQCOW2).getPath();
        System.out.println("DRP4OST-ImageMerge.mountQCOW2() > imgPath=" + imgPath + ", mountPoint=" + mountPoint + ", scriptPath=" + scriptPath);

        setExecutable(scriptPath);

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(scriptPath);
        cmd.add(imgPath);
        cmd.add(mountPoint);

        execute(cmd);

        return null;
    }

    public static String umountQCOW2(String mountPoint) {

        String scriptPath = ImageMerge.class.getClassLoader().getResource(scriptUmountQCOW2).getPath();
        System.out.println("DRP4OST-ImageMerge.mountQCOW2() > mountPoint=" + mountPoint + ", scriptPath=" + scriptPath);

        setExecutable(scriptPath);

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(scriptPath);
        cmd.add(mountPoint);

        execute(cmd);

        return null;
    }
}
