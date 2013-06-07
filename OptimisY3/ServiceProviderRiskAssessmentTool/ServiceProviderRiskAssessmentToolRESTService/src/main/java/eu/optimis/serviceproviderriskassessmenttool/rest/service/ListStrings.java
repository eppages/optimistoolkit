/*
 *  Copyright 2013 University of Leeds
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

package eu.optimis.serviceproviderriskassessmenttool.rest.service;

import java.util.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListStrings implements List<String> {

    public List<String> list;

    public ListStrings() {

        list = new Vector<String>();

    }

    public boolean add(String arg0) {

        return list.add(arg0);

    }

    public void add(int arg0, String arg1) {

        list.add(arg0, arg1);

    }

    public boolean addAll(Collection<? extends String> arg0) {

        return list.addAll(arg0);

    }

    public boolean addAll(int arg0, Collection<? extends String> arg1) {

        return list.addAll(arg0, arg1);

    }

    public void clear() {

        list.clear();

    }

    public boolean contains(Object arg0) {

        return list.contains(arg0);

    }

    public boolean containsAll(Collection<?> arg0) {

        return list.containsAll(arg0);

    }

    public String get(int arg0) {

        return list.get(arg0);

    }

    public int indexOf(Object arg0) {

        return list.indexOf(arg0);

    }

    public boolean isEmpty() {

        return list.isEmpty();

    }

    public Iterator<String> iterator() {

        return list.iterator();

    }

    public int lastIndexOf(Object arg0) {

        return list.lastIndexOf(arg0);

    }

    public ListIterator<String> listIterator() {

        return list.listIterator();

    }

    public ListIterator<String> listIterator(int arg0) {

        return list.listIterator(arg0);

    }

    public boolean remove(Object arg0) {

        return list.remove(arg0);

    }

    public String remove(int arg0) {

        return list.remove(arg0);

    }

    public boolean removeAll(Collection<?> arg0) {

        return list.retainAll(arg0);

    }

    public boolean retainAll(Collection<?> arg0) {

        return list.retainAll(arg0);

    }

    public String set(int arg0, String arg1) {

        return list.set(arg0, arg1);

    }

    public int size() {

        return list.size();

    }

    public List<String> subList(int arg0, int arg1) {

        return list.subList(arg0, arg1);

    }

    public String toString() {

        return list.toString();

    }

    public Object[] toArray() {

        return list.toArray();

    }

    public <T> T[] toArray(T[] arg0) {

        return list.toArray(arg0);

    }
}