
/*
Copyright (C) 2012 National Technical University of Athens

  Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.optimis.DataManagerClient;

import java.io.*;
import java.net.*;

 class InfrastructureProviderProperties
  {
     public  String name;
     public  String masterLocalIP;
     public  String masterPublicIP;
     public  String vmImagesAccountName;
     public  String vmImagesAccountPassword;
     private String reachableIP = "";
     public  String vmpath;
     public String  repoInterface = "";
     public  boolean isOptimis;
     public  boolean checkExists;

     InfrastructureProviderProperties()
     {
       checkExists = false;
     }

     public String getIP()
     {
      if( reachableIP != "" )
        return reachableIP;

      try {
       InetAddress address = InetAddress.getByName(masterPublicIP);

       if( address.isReachable(3000) )
        {
          reachableIP = masterPublicIP;
          return masterPublicIP;
        }
       else
        {
          reachableIP = masterLocalIP;
          return masterLocalIP;
        }
     }
     catch (UnknownHostException e) {
         reachableIP = masterLocalIP;
         return masterLocalIP;
     }
     catch (IOException e) {
         reachableIP = masterLocalIP;
         return masterLocalIP;
     }
    }
  }


