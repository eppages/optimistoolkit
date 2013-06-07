/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
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

package eu.optimis.treccommon;
//import eu.optimis.tf.clients.TrustFrameworkSPClient;
//import eu.optimis.economicframework.rest.client.SPPredict;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
            System.out.println( "Hello World!" );
        String host = "optimis-spvm.atosorigin.es";
        Integer port = 8080;
        TrecApiSP tsp= new TrecApiSP(host, port);
        String aserviceId = "72a2768d-7f6f-4fdb-9388-5c5dfda1d2f9";
	
        try{
        tsp.TREC_SP_startmonitoring(null, aserviceId, Long.MIN_VALUE, host);
        }
        catch(Exception e)
        {
            System.out.println("here " + e);
        }
      //  TrecObject trec = tsp.getTRECs(host, port, entityID, serviceManifest, 1.0);

    }
}
