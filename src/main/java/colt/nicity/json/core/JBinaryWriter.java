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
public class JBinaryWriter extends ABinaryWriter {

    private IWriteable os;
    /**
     *
     * @param _os
     */
    public JBinaryWriter(final OutputStream _os) {
        os = new IWriteable() {
            @Override
            public void write(int b) throws IOException {
                _os.write(b);
            }
            @Override
            public void write(byte[] b) throws IOException {
                _os.write(b);
            }
            @Override
            public void write(byte[] b, int _offset, int _len) throws IOException {
                _os.write(b, _offset, _len);
            }
            @Override
            public void close() throws IOException {
                _os.flush();//??
                _os.close();
            }
        };
    }
    @Override
    void appendArray(int length) throws IOException {
        UIO.writeByte(os, UJBinary.cArrayValue, "class");
        UIO.writeInt(os, length, "length");
    }

    @Override
    void appendObject(int fieldCount) throws IOException {
        UIO.writeByte(os, UJBinary.cObjectValue, "class");
        UIO.writeInt(os, fieldCount, "count");
    }

    @Override
    void appendKey(String key) throws IOException {
        UIO.writeByteArray(os, key.getBytes(Charset.forName("UTF-8")), "key");
    }

    @Override
    void appendValue(Object value) throws IOException {
        if (value == null) {
            UIO.writeByte(os, UJBinary.cNullValue, "class");
        } else if (value instanceof byte[]) {
            UIO.writeByte(os, UJBinary.cBinaryValue, "class");
            UIO.writeByteArray(os, (byte[])value, null);
        } else {
            Byte type = UJBinary.cPrimativeTypeToClassByte.get(value.getClass());
            if (type == null) throw new RuntimeException(value.getClass()+" Unsupported primative value");
            UIO.writeByte(os, type, "class");
            IWriteJv writeJv = UJBinary.cPrimativeTypeToWriter.get(value.getClass());
            if (writeJv == null) throw new RuntimeException(value.getClass()+" Unsupported primative writer");
            writeJv.write(os, value);
        }
    }
}
