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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.tomp2p.storage.Data;
/*
 * @author Ali Sajjad
 */
public class PeerSPDConf {

	private String spdFile = "/etc/racoon/spd.sh";
	private String localIP;
	private String externalIP;
	private List<String> remoteIPList;
	
	public PeerSPDConf(String localIP, String externalIP, Collection<Data> remoteIPColl)
	{
		File file = new File(spdFile);
		if(file.exists())
		{
			boolean success = file.delete();
			if (!success) {
				System.out.println("Deletion failed.");
			}
			else 	System.out.println("File deleted.");
		}
		
		this.localIP = localIP;
		this.externalIP = externalIP;
		
		this.remoteIPList = new ArrayList<String>();
		
		Iterator<Data> it = remoteIPColl.iterator();
		
		try 
		{
			while(it.hasNext())		// Populate a String array with IP addresses collected from the DHT
			{
				String remoteIP = it.next().getObject().toString();
				if (!remoteIP.equals(localIP) && !remoteIP.equals(externalIP))	remoteIPList.add(remoteIP);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("No. of Remote Peers = "+remoteIPList.size());	
	}
	
	public void genSPDFile() throws IOException, ClassNotFoundException, InterruptedException {
		
		Charset cs = Charset.forName("US-ASCII");
		CharsetEncoder cse = cs.newEncoder();
		
		//System.out.println(getSPDConf());

		FileOutputStream fout = new FileOutputStream(spdFile);
		FileChannel fc = fout.getChannel();
		
		ByteBuffer buff = cse.encode(CharBuffer.wrap(getSPDConf()));	// Loads a hard coded policy file using the next method
		
		// Buffer can be read only once (I think), so don't read or flip if before writing to file or
		// it will be empty, or remember to rewind it !!!
		while (buff.hasRemaining()) {
			  
		      System.out.print((char) buff.get());
		}
		//buff.flip();
		buff.rewind();
		System.out.println("\nUpdating the IPSec Security Policy Database\n\t");
		System.out.println("------------"+new Date(System.currentTimeMillis())+"--------------");
		fc.write(buff);
		fc.close();
		fout.close();
		
		// Enforce the SPD policy using the "setkey" utility for IPSec
		PolicyEnforcer.enforceSPDPolicy(spdFile);
	}
	
	private String getSPDConf() throws ClassNotFoundException, IOException
	{
		String header = new StringBuilder()
							.append("#!/usr/sbin/setkey -f\n")
							.append("flush;\n")
							.append("spdflush;\n")
							.toString();
		
		// This policy just enforces tunnelling of icmp traffic
		String icmp_policy = "";
		String http_policy = "";
		String tomcat_policy = "";
		//System.out.print("Length = "+remoteIPs.length);
        for (String remote : remoteIPList) 
        {
        	icmp_policy += new StringBuilder()
			.append("spdadd "+remote+" "+localIP+" icmp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+localIP+" "+remote+" icmp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.toString();
			
			if (!localIP.equals(externalIP))
				icmp_policy += new StringBuilder()
			.append("spdadd "+remote+" "+externalIP+" icmp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+externalIP+" "+remote+" icmp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.toString();
			
			http_policy += new StringBuilder()
			.append("spdadd "+remote+" "+localIP+"[80] tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+localIP+"[80] "+remote+" tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+remote+"[80] "+localIP+" tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+localIP+" "+remote+"[80] tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.toString();
			
			if (!localIP.equals(externalIP))
				http_policy += new StringBuilder()
			.append("spdadd "+remote+" "+externalIP+"[80] tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+externalIP+"[80] "+remote+" tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+remote+"[80] "+externalIP+" tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+externalIP+" "+remote+"[80] tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.toString();
			
			tomcat_policy += new StringBuilder()
			.append("spdadd "+remote+" "+localIP+"[8080] tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+localIP+"[8080] "+remote+" tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+remote+"[8080] "+localIP+" tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+localIP+" "+remote+"[8080] tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.toString();
			
			if (!localIP.equals(externalIP))
				tomcat_policy += new StringBuilder()
			.append("spdadd "+remote+" "+externalIP+"[8080] tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+externalIP+"[8080] "+remote+" tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+remote+"[8080] "+externalIP+" tcp -P in ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.append("spdadd "+externalIP+" "+remote+"[8080] tcp -P out ipsec\n")
			.append("\t\tesp/transport//require\n")
			.append("\t\tah/transport//require;\n")
			.toString();
		}
        //System.out.println(header+policy);
		return header+icmp_policy+http_policy+tomcat_policy;
	}
}
