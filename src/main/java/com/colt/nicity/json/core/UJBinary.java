/*
 * UJBinary.java
 *
 * Created on Apr 4, 2010, 10:30:26 AM
 *
 * Copyright Apr 4, 2010 Jonathan Colt 
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
import com.colt.nicity.core.io.IWriteable;
import com.colt.nicity.core.io.UIO;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
*
* @author Jonathan Colt
*/
public class UJBinary {
    static final Class<?>[] cPrimativeTypes = {
      String.class,Double.class,Float.class,Long.class,Integer.class,
      Short.class,Character.class, Byte.class, Boolean.class,
    };

    static final byte cNullValue = 0;
    static final byte cBinaryValue = 1;
    static final byte cObjectValue = 2;
    static final byte cArrayValue = 3;//

    static final Map<Class<?>,Byte> cPrimativeTypeToClassByte = new HashMap<Class<?>,Byte>();
    static final Map<Byte,Class<?>> cClassByteToPrimativeType = new HashMap<Byte,Class<?>>();
    static {
        byte type = 4;// next value after cArrayValue...
        for(Class<?> c:cPrimativeTypes) {
            cPrimativeTypeToClassByte.put(c, type);
            cClassByteToPrimativeType.put(type, c);
            type++;
        }
    }
    interface IWriteJv {
        public void write(IWriteable _w,Object _v) throws IOException;
    }
    interface IReadJv {
       public Object read(IReadable _r) throws Exception;
    }
    static final Map<Class<?>,IWriteJv> cPrimativeTypeToWriter = new HashMap<Class<?>,IWriteJv>();
    static final IWriteJv valueWriter(Class<?> _class) {
        return cPrimativeTypeToWriter.get(_class);
    }
    static final Map<Class<?>,IReadJv> cPrimativeTypeToReader = new HashMap<Class<?>,IReadJv>();
    static final IReadJv valueReader(Class<?> _class) {
        return cPrimativeTypeToReader.get(_class);
    }
    static {
        cPrimativeTypeToReader.put(String.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return new String(UIO.readByteArray(_r,"v"),Charset.forName("UTF-8"));
            }
        });
        cPrimativeTypeToReader.put(Boolean.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readBoolean(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Byte.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readByte(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Character.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readChar(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Short.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readShort(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Integer.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readInt(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Long.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readLong(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Float.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readFloat(_r,"v");
            }
        });
        cPrimativeTypeToReader.put(Double.class,new IReadJv() {
            public Object read(IReadable _r) throws Exception {
                return UIO.readDouble(_r,"v");
            }
        });


        ///
        cPrimativeTypeToWriter.put(String.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeByteArray(_w,((String)_v).getBytes(Charset.forName("UTF-8")), "v");
            }
        });
        cPrimativeTypeToWriter.put(Boolean.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeBoolean(_w,(Boolean)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Byte.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeByte(_w,(Byte)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Character.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeChar(_w,(Character)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Short.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeShort(_w,(Short)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Integer.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeInt(_w,(Integer)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Long.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeLong(_w,(Long)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Float.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeFloat(_w,(Float)_v, "v");
            }
        });
        cPrimativeTypeToWriter.put(Double.class,new IWriteJv() {
            public void write(IWriteable _w, Object _v) throws IOException {
                UIO.writeDouble(_w,(Double)_v, "v");
            }
        });

    }
}
