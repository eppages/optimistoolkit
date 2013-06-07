package eu.optimis.broker.client;

import com.sun.jersey.api.client.ClientResponse;


public class DMDataSynchClientMain {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
		DMDataSynchClient brokerclient = new DMDataSynchClient("200.0.0.1","8080");
	    
		ClientResponse resp = brokerclient.dataUploadComplete("DemoApp");
		System.out.println("DMDataSynchClientMain : dataUploadComplete :" + resp.getStatus());
		
		String status = brokerclient.isReady2CreateAgreeement("DemoApp");
		System.out.println("DMDataSynchClientMain : isReady2CreateAgreeement:" + status);

		//ClientResponse resp = brokerclient.dataUploadComplete("DemoApp");
		//System.out.println("DMDataSynch client :" + resp.getStatus());

		resp = brokerclient.Ready2CreateAgreeement("DemoApp", "80");
		System.out.println("DMDataSynchClientMain :Ready2CreateAgreeement :" + resp.getStatus());

		status = brokerclient.isReady2CreateAgreeement("DemoApp");
		System.out.println("DMDataSynchMain : isReady2CreateAgreeement:" + status);

 		
	   }

}
