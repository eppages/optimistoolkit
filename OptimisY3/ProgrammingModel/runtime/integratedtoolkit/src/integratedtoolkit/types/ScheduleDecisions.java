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

import java.util.HashMap;
import java.util.LinkedList;

public class ScheduleDecisions {

    public LinkedList<HashMap<String, Object>> mandatory;
    public LinkedList<HashMap<String, Object>> temporary;
    public LinkedList<String> terminate;
    private LinkedList<Movement> movements;

    public ScheduleDecisions() {
        movements = new LinkedList();
        temporary = new LinkedList();
        mandatory = new LinkedList();
        terminate = new LinkedList<String>();
    }

    public void addMovement(String sourceName, int sourceSlot, String targetName, int targetSlot, int core, int amount) {
        Movement mov = new Movement();
        mov.sourceName = sourceName;
        mov.sourceSlot = sourceSlot;
        mov.targetName = targetName;
        mov.targetSlot = targetSlot;
        mov.core = core;
        mov.amount = amount;
        movements.add(mov);
    }

    public LinkedList<Object[]> getMovements() {
        LinkedList movs = new LinkedList();
        for (Movement mov : movements) {
            movs.add(new Object[]{mov.sourceName, mov.targetName, mov.sourceSlot, mov.targetSlot, mov.core, mov.amount});
        }
        return movs;
    }

    public void addMandatory(String resourceName, Object[] properties) {
        
        System.out.println("Adding Mandatory Resource");
        HashMap<String, Object> machine = new HashMap();
        machine.put("resourceName", resourceName);
        System.out.println("\tresourceName:"+resourceName);
        machine.put("slots", properties[0]);
        System.out.println("\tSlots:"+properties[0]);
        machine.put("cores", properties[1]);
        System.out.println("\tCores:"+properties[1]);
        machine.put("user", properties[2]);
        System.out.println("\tUser:"+properties[2]);
        machine.put("iDir", properties[3]);
        System.out.println("\tiDir:"+properties[3]);
        machine.put("wDir", properties[4]);
        System.out.println("\twDir"+properties[4]);
        if (properties[5] != null) {
            machine.put("sharedDisks", properties[5]);
        }
        System.out.println("\tshared"+properties[5]);
        mandatory.add(machine);
    }
    
    

    public void addTemporary(String resourceName, Object[] properties) {
        HashMap<String, Object> machine = new HashMap();
        machine.put("resourceName", resourceName);
        machine.put("slots", properties[0]);
        machine.put("cores", properties[1]);
        machine.put("user", properties[2]);
        machine.put("iDir", properties[3]);
        machine.put("wDir", properties[4]);
        if (properties[5] != null) {
            machine.put("sharedDisks", properties[5]);
        }
        temporary.add(machine);
    }
}

class Movement {

    String sourceName;
    int sourceSlot;
    String targetName;
    int targetSlot;
    int core;
    int amount;
}
