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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.log4j.Logger;
import java.net.*;
import java.io.*;

/*
  class RestService

  example:
   RestService service = new RestService();

   service.addParam("myvar1", "2323");
   service.addParam("myvar2", "2323");
   String url = "http://wwww.test.org/api/service";

   String result = service.get(url);
   String result = service.post(url);
*/
class RestService
{
    MultivaluedMap<String, String> queryParams  = new MultivaluedMapImpl();
    String errorMsg = new String();

    public RestService()
    {}

    public void addParam(String key, String value)
    {
      queryParams.add(key, value);
    }

    public void clearParams()
    {
       queryParams.clear();
    }

    public String get(String url)
    {
      String result = "";

      try
      {
         errorMsg              = "";
         Client         client = Client.create();
         WebResource  resource = client.resource(url);
                      result = resource.queryParams(queryParams).get(String.class);
      }
      catch(Exception ex)
      {
         errorMsg = ex.toString();
         result = "";
      }

      return result;
    }

    public String post(String url)
    {
       String result = "";

       try
       {
            errorMsg              = "";
            Client         client = Client.create();
            WebResource  resource = client.resource(url);
                           result = resource.type("application/x-www-form-urlencoded").post(String.class, queryParams);
       }
       catch(Exception ex)
       {
          errorMsg = ex.toString();
          result = "";
       }

       return result;
    }

   public String getHTML(String urlToRead)
    {
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      String result = "";
      try {
         url = new URL(urlToRead);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         while ((line = rd.readLine()) != null) {
            result += line + "\n";
         }
         rd.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }
}
