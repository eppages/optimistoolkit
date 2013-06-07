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
package integratedtoolkit.components;

import integratedtoolkit.types.Task;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/* To request the scheduling of a set of tasks
 * To request the rescheduling of a job in another host (for fault tolerance purposes)
 */
public interface Schedule {

    void scheduleTasks(List<Task> tasks, boolean waiting, int[] waitingCount, LinkedList<String> obsoletes) throws IOException;

    void newWaitingTask(int methodId, LinkedList<String> obsoletes)throws IOException;

    void rescheduleJob(Task task)throws IOException;

    void shutdown(HashMap<Integer, Integer> currentTaskCount, LinkedList<String> obsoletes)throws IOException;
}
