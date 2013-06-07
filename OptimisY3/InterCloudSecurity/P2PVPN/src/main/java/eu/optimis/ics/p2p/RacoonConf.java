/*
 * Copyright (c) 2010-2013 British Telecom and City University London
 *
 * This file is part of P2PVPN component of the WP 5.4
 * (Inter-Cloud Security) of the EU OPTIMIS project.
 *
 * P2PVPN can be used under the terms of the SHARED SOURCE LICENSE
 * FOR NONCOMMERCIAL USE. 
 *
 * You should have received a copy of the SHARED SOURCE LICENSE FOR
 * NONCOMMERCIAL USE in the project's root directory. If not, please contact the
 * author at ali.sajjad@bt.com
 *
 * Author: Ali Sajjad
 *
 */
package eu.optimis.ics.p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*
 * @author Ali Sajjad
 */
public class RacoonConf {
	
	private final static String racoonFile = "/etc/racoon/racoon.conf";
	private final static String pskFile = "/etc/racoon/psk.txt"; 			// Trailing / is necessary

	public RacoonConf() {
		
		File file = new File(racoonFile);
		if(file.exists())
		{
			boolean success = file.delete();
			if (!success) {
				System.out.println("Deletion failed.");
			}
			else 	System.out.println("File deleted.");
		}
		
		file = new File(pskFile);
		if(file.exists())
		{
			boolean success = file.delete();
			if (!success) {
				System.out.println("Deletion failed.");
			}
			else 	System.out.println("File deleted.");
		}
	}
	
	public void genPSKConfFile(String key) throws IOException
	{
		genPSKFile(key);
		
		Charset cs = Charset.forName("US-ASCII");
		CharsetEncoder cse = cs.newEncoder();

		FileOutputStream fout = new FileOutputStream(racoonFile);
		FileChannel fc = fout.getChannel();

		// See PeerSPDConf for the explanation of the following line
		ByteBuffer buff = cse.encode(CharBuffer.wrap(getTemplate("pre_shared_key")));
		
		while (buff.hasRemaining()) 
		{
			  System.out.print((char) buff.get());
		}
		buff.rewind();
		System.out.println("\nWriting secret key based configuration to racoon.conf file\n\t");
		System.out.println("------------"+new Date(System.currentTimeMillis())+"--------------");
		fc.write(buff);
		fc.close();
		fout.close();
	}
	
	public void genRSAConfFile() throws IOException
	{
		Charset cs = Charset.forName("US-ASCII");
		CharsetEncoder cse = cs.newEncoder();

		FileOutputStream fout = new FileOutputStream(racoonFile);
		FileChannel fc = fout.getChannel();

		// See PeerSPDConf for the explanation of the following line
		ByteBuffer buff = cse.encode(CharBuffer.wrap(getTemplate("rsasig")));
		
		while (buff.hasRemaining()) 
		{
			  System.out.print((char) buff.get());
		}
		buff.rewind();
		System.out.println("\nWriting Certificate-based configuration to racoon.conf file\n\t");
		System.out.println("------------"+new Date(System.currentTimeMillis())+"--------------");
		fc.write(buff);
		fc.close();
		fout.close();
	}
	
	private String getTemplate(String authMethod)
	{
		String presharedkeyPath = "\"/etc/racoon/psk.txt\"";
		String certPath = "\"/etc/racoon/certs\"";

		String paths = new StringBuilder()
        					.append("path pre_shared_key "+presharedkeyPath+";\n")
        					.append("path certificate "+certPath+";\n")
        					.toString();

		String proposal = new StringBuilder()
							.append("proposal {\n")
							.append("\t\tencryption_algorithm 3des;\n")
							.append("\t\thash_algorithm md5;\n")
					        .append("\t\tauthentication_method "+authMethod+";\n")
					        .append("\t\tdh_group modp1024;\n")
					        .append("\t}")
					        .toString();
	
		StringBuilder remoteConf = new StringBuilder()
										.append("remote anonymous {\n")
										.append("\texchange_mode main;\n");
		
		if(authMethod.equals("rsasig"))	// If the method is rsasig
		{
			remoteConf.append("\tverify_cert on;\n")
					  .append("\tmy_identifier asn1dn;\n")
					  .append("\tcertificate_type x509 \"peer.crt\" \"peer.key\";\n");
		}
		
		remoteConf.append("\t"+proposal+"\n")
				  .append("}\n");
		
		String remote = remoteConf.toString();
		
		String sainfo = new StringBuilder()
							.append("sainfo anonymous {\n")
							.append("\tpfs_group modp1024;\n")
							.append("\tencryption_algorithm 3des;\n")
							.append("\tauthentication_algorithm hmac_md5;\n")
        					.append("\tcompression_algorithm deflate;\n")
        					.append("}")
        					.toString();

		return paths+remote+sainfo;
	}
	
	private void genPSKFile(String key) throws IOException
	{
		Charset cs = Charset.forName("US-ASCII");
		CharsetEncoder cse = cs.newEncoder();

		FileOutputStream fout = new FileOutputStream(pskFile);
		FileChannel fc = fout.getChannel();
		
		// See PeerSPDConf for the explanation of the following line
		ByteBuffer buff = cse.encode(CharBuffer.wrap(key));
		
		while (buff.hasRemaining()) 
		{
			  System.out.print((char) buff.get());
		}
		buff.rewind();
		System.out.println("\nWriting PSK to psk.txt file\n\t");
		System.out.println("------------"+new Date(System.currentTimeMillis())+"--------------");
		fc.write(buff);
		fc.close();
		fout.close();
	}
	
	public static int reloadRacoon() throws IOException, InterruptedException {
		
		List<String> commands = new ArrayList<String>();
	    //commands.add("sudo");		// will run only if you modify the visudo conf
	    commands.add("/etc/init.d/racoon");
	    commands.add("reload");
	    
		ProcessBuilder pb = new ProcessBuilder(commands); 
		pb.redirectErrorStream(true);
		
		Process process = pb.start();
		
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
		        System.out.println(line);
		}
		int exitValue = process.waitFor();
		System.out.println("\nRacoon configuration reloaded : Exit Value: "+exitValue);
		return exitValue;
	}
}
