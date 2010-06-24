/*
 * JBinaryWriter.java.java
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


import colt.nicity.core.io.IWriteable;
import colt.nicity.core.io.UIO;
import colt.nicity.json.core.UJBinary.IWriteJv;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 *
 * @author Administrator
 */
public class JBinarySchemaDataWriter extends ABinaryWriter {

    private long schemaHash = 0;
    private IWriteable schema;
    private IWriteable data;
    /**
     *
     * @param _schema
     * @param _data
     */
    public JBinarySchemaDataWriter(final OutputStream _schema,final OutputStream _data) {
        schema = new IWriteable() {
            @Override
            public void write(int b) throws IOException {
                if (_schema == null) return;
                _schema.write(b);
                schemaHash = hash(schemaHash,UIO.intBytes(b), 0, 4);
            }
            @Override
            public void write(byte[] b) throws IOException {
                if (_schema == null) return;
                _schema.write(b);
                schemaHash = hash(schemaHash,b, 0, b.length);
            }
            @Override
            public void write(byte[] b, int _offset, int _len) throws IOException {
                if (_schema == null) return;
                _schema.write(b, _offset, _len);
                schemaHash = hash(schemaHash,b, _offset, _len);
            }
            @Override
            public void close() throws IOException {
                if (_schema == null) return;
                _schema.flush();//??
                _schema.close();
            }
        };
        data = new IWriteable() {
            @Override
            public void write(int b) throws IOException {
                _data.write(b);
            }
            @Override
            public void write(byte[] b) throws IOException {
                _data.write(b);
            }
            @Override
            public void write(byte[] b, int _offset, int _len) throws IOException {
                _data.write(b, _offset, _len);
            }
            @Override
            public void close() throws IOException {
                _data.flush();//??
                _data.close();
            }
        };
    }
    /**
     *
     * @return
     */
    public long getSchemaHash() {
        return schemaHash;
    }
    static final private long hash(long _schemaHash,byte[] _key, int _start, int _length) {
        long randMult = 0x5DEECE66DL;
        long randAdd = 0xBL;
        long randMask = (1L << 48) - 1;
        long seed = _length;
        long result = _schemaHash;
        for (int i = _start; i < _start + _length; i++) {
            long x = (seed * randMult + randAdd) & randMask;
            seed = x;
            x %= _length;
            result += (_key[i] + 128) * x;
        }
        if (result < 0) {
            result = -result;
        }
        return result;
    }



    @Override
    void appendArray(int length) throws IOException {
        UIO.writeByte(schema, UJBinary.cArrayValue, "class");
        UIO.writeInt(data, length, "length");
    }

    @Override
    void appendObject(int fieldCount) throws IOException {
        UIO.writeByte(schema, UJBinary.cObjectValue, "class");
        UIO.writeInt(data, fieldCount, "count");
    }

    @Override
    void appendKey(String key) throws IOException {
        UIO.writeByteArray(schema, key.getBytes(Charset.forName("UTF-8")), "key");
    }

    @Override
    void appendValue(Object value) throws IOException {
        if (value == null) {
            UIO.writeByte(schema, UJBinary.cNullValue, "class");
        } else if (value instanceof byte[]) {
            UIO.writeByte(schema, UJBinary.cBinaryValue, "class");
            UIO.writeByteArray(data, (byte[])value, null);
        } else {
            Byte type = UJBinary.cPrimativeTypeToClassByte.get(value.getClass());
            if (type == null) throw new RuntimeException(value.getClass()+" Unsupported primative value");
            UIO.writeByte(schema, type, "class");
            IWriteJv writeJv = UJBinary.cPrimativeTypeToWriter.get(value.getClass());
            if (writeJv == null) throw new RuntimeException(value.getClass()+" Unsupported primative writer");
            writeJv.write(data, value);
        }
    }
}
