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

package eu.optimis.infrastructureproviderriskassessmenttool.rest.service;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class was obtained from:
 * http://stackoverflow.com/questions/8413608/sending-list-map-as-post-parameter-jersey
 *
 */
@XmlRootElement
public class HashMapStrings<T, U> {

    private Map<T, U> mapProperty;

    public HashMapStrings() {
        mapProperty = new HashMap<T, U>();
    }

    @XmlJavaTypeAdapter(MapAdapter.class)
    public Map<T, U> getMapProperty() {
        return mapProperty;
    }

    public void setMapProperty(Map<T, U> map) {
        this.mapProperty = map;
    }

    class MapElement {

        @XmlElement
        public String key;
        @XmlElement
        public String value;

        public MapElement(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    class MapAdapter extends XmlAdapter<MapElement[], Map<String, String>> {

        @SuppressWarnings("unchecked") //FIXME
		public MapElement[] marshal(Map<String, String> arg0) throws Exception {
            MapElement[] mapElements = (MapElement[]) new Object[arg0.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : arg0.entrySet()) {
                mapElements[i++] = new MapElement(entry.getKey(), entry.getValue());
            }

            return mapElements;
        }

        public Map<String, String> unmarshal(MapElement[] arg0) throws Exception {
            Map<String, String> r = new HashMap<String, String>();
            for (MapElement mapelement : arg0) {
                r.put(mapelement.key, mapelement.value);
            }
            return r;
        }
    }
}
