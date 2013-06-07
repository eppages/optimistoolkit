/*
 *  Copyright 2002-2013 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.types;


public class ProjectWorker implements java.io.Serializable{
        private String name;
        private String user;
        private String installDir;
        private String workingDir;
        private int limitOfTasks;


        
        public ProjectWorker(String name){
            this.name=name;
            this.user="user";
            this.limitOfTasks = 1;
            this.installDir="/IT_worker/";
            this.workingDir="/home/user/";
        }

        public ProjectWorker(String name, String type){
            this.name=name;
            this.user="user";
            this.limitOfTasks = 1;
            this.installDir="/IT_worker/";
            this.workingDir="/home/user/";
        }

        public ProjectWorker(String name, String type, String user){
            this.name=name;
            this.user=user;
            this.limitOfTasks = 1;
            this.installDir="/IT_worker/";
            this.workingDir="/home/user/";
        }

        public ProjectWorker(String name, String type, String user, int limitOfTasks){
            this.name=name;
            this.user=user;
            this.limitOfTasks = limitOfTasks;
            this.installDir="/IT_worker/";
            this.workingDir="/home/user/";
        }       
  
        public ProjectWorker(String name, String user, int limitOfTasks, String iDir, String wDir){
            this.name=name;
            this.user=user;
            this.limitOfTasks = limitOfTasks;
            this.installDir=iDir;
            this.workingDir=wDir;
        }


        public void setName(String name){
            this.name=name;
        }
        
        public void setUser(String user){
            this.user=user;
        }
        public void setInstallDir(String installDir){
            this.installDir=installDir;
        }
        public void setWorkingDir(String workingDir){
            this.workingDir=workingDir;
        }
        public void setLimitOfTasks(int limitOfTasks){
            this.limitOfTasks=limitOfTasks;
        }


        public String getName(){
            return this.name;
        }
        public String getUser(){
            return this.user;
        }
        public String getInstallDir(){
            return this.installDir;
        }
        public String getWorkingDir(){
            return this.workingDir;
        }
        public int getLimitOfTasks(){
            return this.limitOfTasks;
        }        
}
