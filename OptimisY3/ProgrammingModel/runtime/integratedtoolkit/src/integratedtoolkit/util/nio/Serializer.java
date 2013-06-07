/*
 *  Copyright 2002-2012 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.util.nio;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static byte[] serialize(Object data) throws IOException {

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(data);
            byte[] serialized = bos.toByteArray();
            os.close();
            bos.close();
            return serialized;
        } catch (Exception e) {
        }


        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLEncoder xml = new XMLEncoder(bos);
            xml.writeObject(data);
            xml.flush();
            xml.close();
            byte[] serialized = bos.toByteArray();
            bos.close();
            return serialized;
        } catch (Exception e) {
        }
        return null;
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        Object returnValue = null;
        try {
            InputStream is = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(is);
            returnValue = ois.readObject();
            ois.close();
            is.close();
            return returnValue;
        } catch (Exception e) {
        }

        try {
            InputStream is = new ByteArrayInputStream(data);
            BufferedInputStream bis = new BufferedInputStream(is);
            XMLDecoder d = new XMLDecoder(bis);
            d.close();
            returnValue = d.readObject();
            is.close();
            return returnValue;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }
}
