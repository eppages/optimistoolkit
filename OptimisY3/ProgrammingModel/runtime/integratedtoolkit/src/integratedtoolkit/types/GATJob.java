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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.gridlab.gat.monitoring.Metric;
import org.gridlab.gat.monitoring.MetricDefinition;
import org.gridlab.gat.monitoring.MetricEvent;
import org.gridlab.gat.monitoring.MetricListener;
import org.gridlab.gat.resources.HardwareResourceDescription;
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.Job.JobState;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.ResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;


import integratedtoolkit.ITConstants;
import integratedtoolkit.api.ITExecution.*;
import integratedtoolkit.components.JobStatus.JobEndStatus;
import integratedtoolkit.components.impl.JobManager;
import integratedtoolkit.types.Parameter.*;
import integratedtoolkit.types.data.DataAccessId;
import integratedtoolkit.types.data.DataAccessId.*;
import integratedtoolkit.util.ProjectManager;
import java.util.LinkedList;
import java.util.Map.Entry;

public class GATJob extends integratedtoolkit.types.Job implements MetricListener {

    private static final String JOB_DIR_CREATION_ERR = "Error creating job output/error directory";
    private static final String CALLBACK_PROCESSING_ERR = "Error processing callback for job";
    private static final String WORKER_SCRIPT = "worker.sh";
    private static final String ANY_PROT = "any://";
    private static final String JOB_STATUS = "job.status";
    private static final String RES_ATTR = "machine.node";
    private static final String JOB_PREPARATION_ERR = "Error preparing job";
    private static final String RB_CREATION_ERR = "Error creating resource broker";
    private static final String JOB_SUBMISSION_ERR = "Error submitting job";
    private static int jobCount;
    private Job GATjob;
    // GAT context
    private static GATContext context;
    // GAT broker adaptor information
    private static boolean usingGlobus;
    private static boolean userNeeded;
    // Brokers - TODO: Problem if many resources used
    private static Map<String, ResourceBroker> brokers;
    // Language
    private static final boolean isJava = System.getProperty(ITConstants.IT_LANG) != null
            && System.getProperty(ITConstants.IT_LANG).equals("java")
            ? true : false;
    // Worker classpath
    private static final String workerClasspath =
            (System.getProperty(ITConstants.IT_WORKER_CP) != null && System.getProperty(ITConstants.IT_WORKER_CP).compareTo("") != 0)
            ? System.getProperty(ITConstants.IT_WORKER_CP)
            : "\"\"";

    public static void init() {
        jobCount = 0;
        if (context == null) {
            context = new GATContext();
            String brokerAdaptor = System.getProperty(ITConstants.GAT_BROKER_ADAPTOR),
                    fileAdaptor = System.getProperty(ITConstants.GAT_FILE_ADAPTOR);
            logger.info("Adaptors JM : " + brokerAdaptor + " ------ " + fileAdaptor);
            context.addPreference("ResourceBroker.adaptor.name", brokerAdaptor);
            context.addPreference("File.adaptor.name", fileAdaptor + ", local");
            usingGlobus = brokerAdaptor.equalsIgnoreCase("globus");
            userNeeded = brokerAdaptor.regionMatches(true, 0, "ssh", 0, 3);
            for (Entry<String, String> e : ProjectManager.getJobAdaptorPreferences().entrySet()) {
                context.addPreference(e.getKey(), e.getValue());
            }
            if (debug) {
                try {
                    File jobsDir = GAT.createFile(context, "any:///jobs");
                    jobsDir.mkdir();
                } catch (Exception e) {
                    logger.fatal(JOB_DIR_CREATION_ERR, e);
                    System.exit(1);
                }
            }

        }
        brokers = new TreeMap<String, ResourceBroker>();
    }

    public GATJob(Task task) {
        jobCount++;
        jobId = nextJobId++;
        this.task = task;
        history = JobHistory.NEW;
    }

    public JobKind getKind() {
        return JobKind.METHOD;
    }

    public void submit() throws Exception {
        // Prepare the job
        JobDescription jobDescr = null;
        try {
            jobDescr = prepareJob();
        } catch (Exception e) {;
            logger.fatal(JOB_PREPARATION_ERR + ": " + this, e);
            System.exit(1);
        }

        // Get a broker for the host
        ResourceBroker broker = null;
        try {
            String dest = (String) jobDescr.getResourceDescription().getResourceAttribute(RES_ATTR);
            if ((broker = brokers.get(dest)) == null) {
                broker = GAT.createResourceBroker(context, new URI(dest));
                brokers.put(dest, broker);
            }
        } catch (Exception e) {
            logger.fatal(RB_CREATION_ERR, e);
            System.exit(1);
        }
        // Submit the job, registering for notifications of job state transitions (associatedJM is the metric listener)
        Job job = null;

        try {
            job = broker.submitJob(jobDescr, this, JOB_STATUS);
        } catch (Exception e) {
            logger.error(JOB_SUBMISSION_ERR + ": " + this, e);
            associatedJM.jobStatusNotification(this, JobEndStatus.SUBMISSION_FAILED);
            throw e;
        }

        // Update mapping
        GATjob = job;
    }

    public void stop() throws Exception {
        jobCount--;
        if (GATjob != null) {
            MetricDefinition md = GATjob.getMetricDefinitionByName(JOB_STATUS);
            Metric m = md.createMetric();
            GATjob.removeMetricListener(this, m);
            GATjob.stop();
        }
    }

    // MetricListener interface implementation
    public void processMetricEvent(MetricEvent value) {
        Job job = (Job) value.getSource();
        JobState newJobState = (JobState) value.getValue();
        SoftwareDescription sd = ((JobDescription) job.getJobDescription()).getSoftwareDescription();
        Integer jobId = (Integer) sd.getAttributes().get("jobId");
        /* Check if either the job has finished or there has been a submission error.
         * We don't care about other state transitions
         */
        if (newJobState == JobState.STOPPED) {
            /* We must check whether the chosen adaptor is globus
             * In that case, since globus doesn't provide the exit status of a job,
             * we must examine the standard error file
             */
            try {
                if (usingGlobus) {
                    File errFile = sd.getStderr();
                    // Error file should always be in the same host as the IT
                    File localFile = GAT.createFile(context, errFile.toGATURI());
                    if (localFile.length() > 0) {
                        GATjob = null;
                        associatedJM.jobStatusNotification(this, JobEndStatus.EXECUTION_FAILED);
                    } else {
                        if (!debug) {
                            localFile.delete();
                        }
                        associatedJM.jobStatusNotification(this, JobEndStatus.OK);
                    }
                } else {
                    if (job.getExitStatus() == 0) {
                        associatedJM.jobStatusNotification(this, JobEndStatus.OK);
                    } else {
                        GATjob = null;
                        associatedJM.jobStatusNotification(this, JobEndStatus.EXECUTION_FAILED);
                    }
                }
            } catch (Exception e) {
                logger.fatal(CALLBACK_PROCESSING_ERR + ": " + this, e);
                System.exit(1);
            }
            jobCount--;
        } else if (newJobState == JobState.SUBMISSION_ERROR) {
            try {
                if (debug) {
                    logger.debug("Job info for job " + jobId + ": " + job.getInfo() + "\n" + this);
                }

                if (usingGlobus && job.getInfo().get("resManError").equals("NO_ERROR")) {
                    associatedJM.jobStatusNotification(this, JobEndStatus.OK);
                } else {
                    GATjob = null;
                    associatedJM.jobStatusNotification(this, JobEndStatus.SUBMISSION_FAILED);
                }
            } catch (GATInvocationException e) {
                logger.fatal(CALLBACK_PROCESSING_ERR + ": " + this, e);
                System.exit(1);
            }
            jobCount--;
        }
    }

    public static void end() {
        while (jobCount > 0) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        GAT.end();
    }

    private JobDescription prepareJob() throws Exception {
        // Get the information related to the job

        ExecutionParams execParams = getExecutionParams();
        String targetPath = execParams.getInstallDir();
        String targetHost = execParams.getHost();
        String targetUser = execParams.getUser();
        if (userNeeded && targetUser != null) {
            targetUser += "@";
        } else {
            targetUser = "";
        }

        SoftwareDescription sd = new SoftwareDescription();
        sd.setExecutable(targetPath + WORKER_SCRIPT);
        ArrayList<String> lArgs = new ArrayList<String>();
        // Prepare arguments: classpath working_dir debug method_class method_name has_target num_params par_type_1 par_1 ... par_type_n par_n
        lArgs.add(workerClasspath);
        lArgs.add(execParams.getWorkingDir());

        LinkedList<String> obsoleteFiles = JobManager.hostToObsolete.get(targetHost);
        if (obsoleteFiles != null) {
            synchronized (obsoleteFiles) {
                lArgs.add("" + obsoleteFiles.size());
                for (String renaming : obsoleteFiles) {
                    if (debug) {
                        logger.debug("Ordering the removal of obsolete file " + renaming + " in host " + targetHost);
                    }
                    lArgs.add(renaming);
                }
                obsoleteFiles.clear();
            }
        } else {
            lArgs.add("0");
        }
        if (task.getTaskId() > -1) {
            Method method = (Method) getCore();
            String methodName = method.getName();
            lArgs.add(workerDebug);
            lArgs.add(method.getDeclaringClass());
            lArgs.add(methodName);
            lArgs.add(Boolean.toString(method.hasTargetObject()));
            int numParams = method.getParameters().length;
            if (method.hasReturnValue()) {
                numParams--;
            }
            lArgs.add(Integer.toString(numParams));
            for (Parameter param : method.getParameters()) {
                ParamType type = param.getType();
                lArgs.add(Integer.toString(type.ordinal()));
                if (type == ParamType.FILE_T || type == ParamType.OBJECT_T) {
                    DependencyParameter dPar = (DependencyParameter) param;
                    DataAccessId dAccId = dPar.getDataAccessId();
                    lArgs.add(dPar.getDataRemotePath());
                    if (type == ParamType.OBJECT_T) {
                        if (dAccId instanceof RAccessId) {
                            lArgs.add("R");

                        } else {
                            lArgs.add("W"); // for the worker to know it must write the object to disk

                        }
                    }

                } else if (type == ParamType.STRING_T) {
                    BasicTypeParameter btParS = (BasicTypeParameter) param;
                    // Check spaces
                    String value = btParS.getValue().toString();
                    int numSubStrings = value.split(" ").length;
                    lArgs.add(Integer.toString(numSubStrings));
                    lArgs.add(value);
                } else { // Basic types
                    BasicTypeParameter btParB = (BasicTypeParameter) param;
                    lArgs.add(btParB.getValue().toString());
                }
            }
        }
        // Conversion vector -> array
        String[] arguments = new String[lArgs.size()];
        arguments = lArgs.toArray(arguments);
        sd.setArguments(arguments);

        sd.addAttribute("jobId", jobId);

        if (debug) {
            // Set standard output file for job
            File outFile = GAT.createFile(context, "any:///jobs/job" + jobId + ".out");
            sd.setStdout(outFile);
        }

        if (debug || usingGlobus) {
            // Set standard error file for job
            File errFile = GAT.createFile(context, "any:///jobs/job" + jobId + ".err");
            sd.setStderr(errFile);
        }

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(RES_ATTR, ANY_PROT + targetUser + targetHost);
        ResourceDescription rd = new HardwareResourceDescription(attributes);

        if (debug) {
            logger.debug("Ready to submit job " + jobId + ":");
            logger.debug("  * Host: " + targetHost);
            logger.debug("  * Executable: " + sd.getExecutable());

            StringBuilder sb = new StringBuilder("  - Arguments:");
            for (String arg : sd.getArguments()) {
                sb.append(" ").append(arg);
            }
            logger.debug(sb.toString());
        }

        return new JobDescription(sd, rd);
    }
}
