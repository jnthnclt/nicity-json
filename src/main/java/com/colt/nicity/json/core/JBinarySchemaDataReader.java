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

package com.colt.nicity.json.core;

import com.colt.nicity.core.io.IReadable;
import com.colt.nicity.core.io.UIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 *
 * @author Administrator
 */
public class JBinarySchemaDataReader {
    
    private IReadable schema;
    private IReadable data;
    /**
     *
     * @param _schema
     * @param _data
     */
    public JBinarySchemaDataReader(final InputStream _schema,final InputStream _data) {
        schema = new IReadable() {
            @Override
            public int read() throws IOException {
                return _schema.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return _schema.read(b);
            }

            @Override
            public int read(byte[] b, int _offset, int _len) throws IOException {
                return _schema.read(b, _offset, _len);
            }

            @Override
            public void close() throws IOException {
                _schema.close();
            }
        };
        data = new IReadable() {
            @Override
            public int read() throws IOException {
                return _data.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return _data.read(b);
            }

            @Override
            public int read(byte[] b, int _offset, int _len) throws IOException {
                return _data.read(b, _offset, _len);
            }

            @Override
            public void close() throws IOException {
                _data.close();
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
        byte type = UIO.readByte(schema, "class");
        if (type != UJBinary.cObjectValue) throw new RuntimeException("Expected Object "+type);
        _readJo(j);
        return j;
    }
    private Jo _readJo(Jo j) throws Exception {
        int fieldCount = UIO.readInt(data, "fieldCount");
        for(int i=0;i<fieldCount;i++) {
            String key = new String(UIO.readByteArray(schema, "key"),Charset.forName("UTF-8"));
            byte type = UIO.readByte(schema, "class");
            if (type > 3) {
                UJBinary.IReadJv reader = UJBinary.valueReader(UJBinary.cClassByteToPrimativeType.get(type));
                j.add(key, new Jv(reader.read(data)));
            }
            else if (type == UJBinary.cBinaryValue) {
                j.add(key, new Jv(UIO.readByteArray(data, "bytes")));
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
        byte type = UIO.readByte(schema, "class");
        if (type != UJBinary.cArrayValue) throw new RuntimeException("Expected Array");
        return _readJa(j);
    }

    private Ja _readJa(Ja j) throws Exception {
        int fieldCount = UIO.readInt(data, "length");
        for(int i=0;i<fieldCount;i++) {
            byte type = UIO.readByte(schema, "class");
            if (type > 2) {
                UJBinary.IReadJv reader = UJBinary.valueReader(UJBinary.cClassByteToPrimativeType.get(type));
                j.add(new Jv(reader.read(data)));
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
