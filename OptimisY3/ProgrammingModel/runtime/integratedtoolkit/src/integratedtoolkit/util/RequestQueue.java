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

package integratedtoolkit.util;

import java.util.LinkedList;



public class RequestQueue<T> {

	LinkedList<T> queue;
	int waiting;

	public RequestQueue() {
		queue = new LinkedList<T>();

		waiting = 0;
	}
	
	public synchronized void enqueue(T request) {
		queue.add(request);
		notify();
	}
	
	public synchronized T dequeue() {
            


		while (queue.size() == 0) {
			waiting++;
			try {
				wait();
			}
			catch (InterruptedException e) {
				return null;
			}
			waiting--;
		}
                 
		return queue.poll();

	}

        public synchronized void remove(T request) {
            queue.remove(request);
	}

	public synchronized void addToFront(T request) {
            queue.addFirst(request);
            notify();
	}
	
	public synchronized int getNumRequests() {
		return queue.size();
	}
	
	public synchronized boolean isEmpty() {
		return queue.size() == 0;
	}
	
	public synchronized int getWaiting() {
		return waiting;
	}
	
	public synchronized void clear() {
		queue.clear();
	}
	
	public void wakeUpAll() {
		notifyAll();
	}
	
}
