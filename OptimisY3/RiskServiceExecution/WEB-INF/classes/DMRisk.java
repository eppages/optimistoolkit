/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
//import org.glassfish.admin.amx.util.FileUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Risk Assessment Servlet.
 *
 * 
 */

public class DMRisk extends HttpServlet {

	private static final long serialVersionUID = 1L;

	//@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
					throws IOException, ServletException
					{

		System.out.println("in get 1");
		ResourceBundle rb =
				ResourceBundle.getBundle("LocalStrings",request.getLocale());
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<head>");

		String title = rb.getString("DMRisk.title");

		out.println("<title>" + title + "</title>");
		out.println("</head>");
		out.println("<body bgcolor=\"white\">");
		out.println("</body>");
		out.println("</html>");
					}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("in post"); 
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String ServiceID = request.getParameter("ServiceID");
		String TargetRiskMetrics = request.getParameter("TargetRiskMetrics");
		System.out.println("Target = " + TargetRiskMetrics);
		System.out.println(" ServiceID = " + ServiceID);
		String res = risk();
		System.out.println("risk ass result " + res);

		out.println("You invoked with serice ID " + ServiceID + " and risk metics " + TargetRiskMetrics);
		out.flush();
		out.close();
	}

	public String risk(){

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource("http://213.27.211.117:8080/DataManagerAPI/Notify");

		File file = new File("/usr/share/tomcat6/riskproposal.xml");
		String ProposalInput="0";
		//org.apache.tomcat.util.http.fileupload.FileUtils(file);
		//org.glassfish.admin.amx.util.FileUtils.fileToString(src)
		try {
			ProposalInput = org.glassfish.admin.amx.util.FileUtils.fileToString(file);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Object response = service.type("application/x-www-form-urlencoded").post(ClientResponse.class, ProposalInput);
		Object status = response;
		Object textEntity = response;
		String riskAss = ("Status is: "+status.toString()+" and text is: "+textEntity.toString());
		//  String riskAss = "0.8";                 
		return riskAss;
	}

}


