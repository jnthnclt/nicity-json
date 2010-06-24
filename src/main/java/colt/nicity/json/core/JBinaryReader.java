/*
 * JBinaryReader.java.java
 *
 * Created on 12-27-2009 03:21:00 PM
 *
 * Copyright 2009 Jonathan Colt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package colt.nicity.json.core;

import colt.nicity.core.io.IReadable;
import colt.nicity.core.io.UIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 *
 * @author Administrator
 */
public class JBinaryReader {
    

    private IReadable is;
    /**
     *
     * @param _is
     */
    public JBinaryReader(final InputStream _is) {
        is = new IReadable() {
            @Override
            public int read() throws IOException {
                return _is.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return _is.read(b);
            }

            @Override
            public int read(byte[] b, int _offset, int _len) throws IOException {
                return _is.read(b, _offset, _len);
            }

            @Override
            public void close() throws IOException {
                _is.close();
            }
        };
    }
    /**
     *
     * @param j
     * @return
     * @throws Exception
     */
    public Jo readJo(Jo j) throws Exception {
        if (j == null) j = new Jo();
        byte type = UIO.readByte(is, "class");
        if (type != UJBinary.cObjectValue) throw new RuntimeException("Expected Object");
        _readJo(j);
        return j;
    }
    private Jo _readJo(Jo j) throws Exception {
        int fieldCount = UIO.readInt(is, "fieldCount");
        for(int i=0;i<fieldCount;i++) {
            String key = new String(UIO.readByteArray(is, "key"),Charset.forName("UTF-8"));
            byte type = UIO.readByte(is, "class");
            if (type > 3) {
                UJBinary.IReadJv reader = UJBinary.valueReader(UJBinary.cClassByteToPrimativeType.get(type));
                j.add(key, new Jv(reader.read(is)));
            }
            else if (type == UJBinary.cBinaryValue) {
                j.add(key, new Jv(UIO.readByteArray(is, "bytes")));
            }
            else if (type == UJBinary.cObjectValue) {
                j.add(key,_readJo(new Jo()));// recursive
            }
            else if (type == UJBinary.cArrayValue) {
                j.add(key,_readJa(new Ja()));// recursive
            }
            else if (type == UJBinary.cNullValue) {
                j.add(key, Jnull.cNull());
            }
        }
        return j;
    }

    /**
     *
     * @param j
     * @return
     * @throws Exception
     */
    public Ja readJa(Ja j) throws Exception {
        if (j == null) j = new Ja();
        byte type = UIO.readByte(is, "class");
        if (type != UJBinary.cArrayValue) throw new RuntimeException("Expected Array");
        return _readJa(j);
    }

    private Ja _readJa(Ja j) throws Exception {
        int fieldCount = UIO.readInt(is, "length");
        for(int i=0;i<fieldCount;i++) {
            byte type = UIO.readByte(is, "class");
            if (type > 2) {
                UJBinary.IReadJv reader = UJBinary.valueReader(UJBinary.cClassByteToPrimativeType.get(type));
                j.add(new Jv(reader.read(is)));
            }
            else if (type == UJBinary.cObjectValue) {
                j.add(_readJo(new Jo()));// recursive
            }
            else if (type == UJBinary.cArrayValue) {
                j.add(_readJa(new Ja()));// recursive
            }
            else if (type == UJBinary.cNullValue) {
                j.add(Jnull.cNull());
            }
        }
        return j;
    }

    
}
