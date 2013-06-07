/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.clients;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import eu.optimis.tf.clients.utils.ServiceManifestXMLProcessor;

public class TrecDemoClient {

	static Logger log = Logger.getLogger(TrecDemoClient.class.getName());

	public static void main(String[] args) {

		// for (int i = 0; i < args.length; i++){
		// System.out.println("arg num "+ i +" value: "+args[i]);
		// }

//		String option = "";
//		String ip = "";
//		int port = 0;
//		String argument = "";
//
//		if (args.length != 4) {
//			usage();
//		} else {
//			option = args[0];
//			ip = args[1];
//			port = Integer.valueOf(args[2]);
//			argument = args[3];
//			System.out.println("option: "+option+", ip:port "+ip+":"+port+", argument: "+argument);
//		}
//
//		TrustFrameworkIPClient tfcc = new TrustFrameworkIPClient(ip, port);
//		if (option.equalsIgnoreCase("-s")) {
//			try {
//				String sm = ServiceManifestXMLProcessor
//						.readFileAsString(argument);
//				tfcc.serviceDeployed(manifest)(sm);
//			} catch (IOException e) {
//				log.info("IO Error: " + e.getMessage());
//				usage();
//			}
//		} else if (option.equalsIgnoreCase("-g")) {
//			tfcc.getTrustLevel(argument);
//		} else if (option.equalsIgnoreCase("-h")) {
//			System.out.println(tfcc.getServiceHistoric(argument));
////			writefile(tfcc.getServiceHistoric(argument));
//		} else {
//			usage();
//		}
	}

	private static void usage() {
		System.out
				.println("USAGE: \n"
						+ "Start Monitoring: TrecDemoClient -s IP PORT ServiceManifest.xml \n"
						+ "Get last trust value: TrecDemoClient -g IP PORT serviceId \n"
						+ "Get Historic value: TrecDemoClient -h IP PORT serviceId");
		System.exit(-1);
	}

	private static void writefile(String historic) {
		Writer output = null;
		File file = new File("/home/uleeds/trecgui/trust.xml");
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(historic);
			output.close();
		} catch (IOException e) {
			log.info("IO Error: " + e.getMessage());
		}
	}
}
