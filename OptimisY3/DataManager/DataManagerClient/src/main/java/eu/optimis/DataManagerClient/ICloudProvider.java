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

import java.util.Vector;

interface ICloudProvider
{
  public String createUsersRepository(String serviceID, boolean isSensitive) throws Exception;
  public String checklegal(String sid, String manifestXML, String localProvider, String federatedIP);
  public String getCPD() throws Exception;
  public String uploadVMimageRequest(String sid, String url) throws Exception;
  public String checkUploadStatus(String sid, String url) throws Exception;
  public Vector getVMs(String sid) throws Exception;
  public String specifyObjective(String objfun, int trust, int eco, int cost);
  public boolean downloadVMimage(String sid, String vmimage, String localFolder) throws Exception;
  public boolean deleteAccount(String sid);
  public String  startFCSJob(String sid, String numPredictions);
  public String  finishedFCSJob(String token);
}
