/*
 * UJson.java.java
 *
 * Created on 03-12-2010 07:21:11 AM
 *
 * Copyright 2010 Jonathan Colt
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

import com.colt.nicity.core.memory.struct.Tuple2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class UJson {
    /**
     *
     * @param _a
     */
    public static void main(String[] _a) {
        try {
            Ja ja = new Ja();
            Jo jo = new Jo();
            String set = "}\\{[+-/\"]}\u0048";
            UJson.addEscaped(jo, "escaped", set);
            UJson.add(jo,"boolean", true);
            UJson.add(jo,"integer", 1);
            UJson.add(jo,"long", 2L);
            UJson.add(jo,"string", "hello");


            System.out.println(jo);
            System.out.println(set);
            System.out.println(UJson.getStringEscaped(jo, "escaped"));

            ja.add(jo);
            System.out.println(ja);

            Ja r = new JStringReader(ja.toString()).readJa(null);
            System.out.println(r);
        } catch(Exception x) {
            x.printStackTrace();
        }

    }

    /**
     *
     * @param jo
     * @param indent
     * @return
     * @throws Exception
     */
    static public String toString(Jo jo,int indent) throws Exception {
        JStringWriter sw = new JStringWriter(new StringBuffer(),1);
        jo.toString(sw);
        return sw.toString();
    }
    /**
     *
     * @param string
     * @return
     * @throws Exception
     */
    static public Jo fromStringJo(String string) throws Exception {
        return fromStringJo(string, new Jo());
    }
    /**
     *
     * @param string
     * @param jo
     * @return
     * @throws Exception
     */
    static public Jo fromStringJo(String string,Jo jo) throws Exception {
        return new JStringReader(string).readJo(jo);
    }

    /**
     *
     * @param jo
     * @return
     * @throws Exception
     */
    static public ByteBuffer toBinaryJo(Jo jo) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jo.toBinary(new JBinaryWriter(baos));
        return ByteBuffer.wrap(baos.toByteArray());
    }

    /**
     *
     * @param bb
     * @return
     * @throws Exception
     */
    static public Jo fromBinaryJo(ByteBuffer bb) throws Exception {
        return new JBinaryReader(new ByteArrayInputStream(bb.array())).readJo(new Jo());
    }

    /**
     *
     * @param schemas
     * @param jo
     * @return
     * @throws Exception
     */
    static public Tuple2<Long,String> toSchemaStringJo(HashMap<Long,String> schemas,Jo jo) throws Exception {
        StringBuffer schema = new StringBuffer();
        StringBuffer data = new StringBuffer();
        JStringSchemaDataWriter w = new JStringSchemaDataWriter(schema,data);

        jo.toString(w);
        long schemaHash = w.getSchemaHash();
        schemas.put(schemaHash, schema.toString());
        return new Tuple2<Long,String>(schemaHash,data.toString());

    }

    /**
     *
     * @param schemas
     * @param schemaHash
     * @param values
     * @return
     * @throws Exception
     */
    static public Jo fromSchemaStringJo(HashMap<Long,String> schemas,long schemaHash,String values) throws Exception {
        String schema = schemas.get(schemaHash);
        JStringSchemaDataReader r = new JStringSchemaDataReader(new StringReader(schema), new StringReader(values));
        Jo jo = r.readJo(new Jo());
        return jo;
    }
    
    /**
     *
     * @param schemas
     * @param jo
     * @return
     * @throws Exception
     */
    static public ByteBuffer toSchemaBinaryJo(HashMap<Long,ByteBuffer> schemas,Jo jo) throws Exception {
        if (jo == null) {
            ByteBuffer bb = ByteBuffer.allocate(1);
            bb.put((byte)0);
            bb.flip();
            return bb;
        }
        ByteArrayOutputStream wSchema = new ByteArrayOutputStream();
        ByteArrayOutputStream wData = new ByteArrayOutputStream();
        JBinarySchemaDataWriter writer = new JBinarySchemaDataWriter(wSchema, wData);
        jo.toBinary(writer);
        long schemaHash = writer.getSchemaHash();
        ByteBuffer bbSchema = ByteBuffer.wrap(wSchema.toByteArray());
        ByteBuffer bbData = ByteBuffer.wrap(wData.toByteArray());

        
        int messageLength = 1; // byte mode
        messageLength += 8; // long schemaHash
        byte mode = (byte)2;
        if (!schemas.containsKey(schemaHash)) {
            mode = (byte)1;
            messageLength += 4;// int schema length
            messageLength += bbSchema.remaining();// schema size
        }
        messageLength += 4;// int data length
        messageLength += bbData.remaining();// data size

        ByteBuffer bb = ByteBuffer.allocate(messageLength);
        if (mode == 1) {
            bb.put(mode);
            bb.putLong(schemaHash);
            bb.putInt(bbSchema.capacity());
            bb.put(bbSchema);
        }
        else {
            bb.put(mode);
            bb.putLong(schemaHash);
        }
        bb.putInt(bbData.capacity());
        bb.put(bbData);
        bb.flip();
        return bb;
    }

    /**
     *
     * @param schemas
     * @param bb
     * @return
     * @throws Exception
     */
    static public Jo fromSchemaBinaryJo(HashMap<Long,ByteBuffer> schemas,ByteBuffer bb) throws Exception {
        byte mode = bb.get();
        if (mode == 0) {
            return null;
        }
        ByteBuffer schema = null;
        ByteBuffer data = null;
        long schemaHash = -1;
        if (mode == 1) {
            schemaHash = bb.getLong();
            int schemaLength = bb.getInt();//!! test for overflow allocation
            byte[] schemaBytes = new byte[schemaLength];
            bb.get(schemaBytes);
            schema = ByteBuffer.wrap(schemaBytes);
            schemas.put(schemaHash, schema);
        }
        else if (mode == 2) {
            schemaHash = bb.getLong();
            schema = schemas.get(schemaHash);
            if (schema == null) throw new RuntimeException("Missing schema for "+schemaHash);
        }
        else {
            throw new RuntimeException("Unexpected Mode "+mode);
        }
        int dataLength = bb.getInt();//!! test for overflow allocation
        byte[] dataBytes = new byte[dataLength];
        bb.get(dataBytes);
        data = ByteBuffer.wrap(dataBytes);

        ByteArrayInputStream rSchema = new ByteArrayInputStream(schema.array());
        ByteArrayInputStream rData = new ByteArrayInputStream(data.array());
        JBinarySchemaDataReader reader = new JBinarySchemaDataReader(rSchema, rData);
        return reader.readJo(new Jo());
    }

    /**
     *
     * @param _o
     * @return
     */
    static public String[] keys(Jo _o) {
        return _o.keys().toArray(new String[0]);
    }

    /**
     *
     * @param _o
     * @param _key
     */
    static public void remove(Jo _o,String _key) {
        if (_o != null && _key != null) _o.remove(_key);
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     */
    static public boolean has(Jo _o,String _key) {
        return _o.has(_key);
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _bytes
     * @return
     */
    static public Jo add(Jo _o, String _key, byte[] _bytes) {
        _o.add(_key, new Jv(_bytes));
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _int
     * @return
     */
    static public Jo add(Jo _o, String _key, int _int) {
        _o.add(_key, new Jv(_int));
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _long
     * @return
     */
    static public Jo add(Jo _o, String _key, long _long) {
        _o.add(_key, new Jv(_long));
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _double
     * @return
     */
    static public Jo add(Jo _o, String _key, double _double) {
        _o.add(_key, new Jv(_double));
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _boolean
     * @return
     */
    static public Jo add(Jo _o, String _key, boolean _boolean) {
        _o.add(_key, new Jv(_boolean));
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _string
     * @return
     */
    static public Jo add(Jo _o, String _key, String _string) {
        if (_string != null) {
            _o.add(_key, new Jv(_string));
        } else {
            _o.add(_key,Jnull.cNull());
        }
        return _o;
    }

    /**
     *
     * @param <V>
     * @param _o
     * @param _key
     * @param _value
     * @return
     */
    static public <V> Jo add(Jo _o,String _key,V _value) {
        if (_value != null) {
            _o.add(_key, new Jv(_value));
        } else {
            _o.add(_key,Jnull.cNull());
        }
        return _o;
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _aj
     * @return
     */
    static public Jo add(Jo _o, String _key, AJ _aj) {
        _o.add(_key,_aj);
        return _o;
    }


    /**
     *
     * @param _o
     * @param _key
     * @param _string
     * @return
     */
    static public Jo addEscaped(Jo _o, String _key, String _string) {
        if (_string != null) {
            _o.add(_key, new Jv(escape(_string)));
        } else {
            _o.add(_key, Jnull.cNull());
        }
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _url
     * @return
     */
    static public Jo add(Jo _o, String _key, URL _url) {
        if (_url != null) {
            _o.add(_key, new Jv(_url.toString()));
        } else {
            _o.add(_key, Jnull.cNull());
        }
        return _o;
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _date
     * @return
     */
    static public Jo add(Jo _o, String _key, Date _date) {
        if (_date != null) {
            _o.add(_key, new Jv(_date.getTime()));
        } else {
            _o.add(_key, Jnull.cNull());
        }
        return _o;
    }

    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,boolean[] _add) {
        for(boolean a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,Boolean[] _add) {
        for(boolean a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,float[] _add) {
        for(float a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,Float[] _add) {
        for(Float a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,int[] _add) {
        for(int a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,Integer[] _add) {
        for(Integer a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }

    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,double[] _add) {
        for(double a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,Double[] _add) {
        for(double a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,long[] _add) {
        for(long a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,Long[] _add) {
        for(long a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,String[] _add) {
        for(String a:_add) {
            _a.add(new Jv(a));
        }
        return _a;
    }
    /**
     *
     * @param _a
     * @param _add
     * @return
     */
    static public Ja add(Ja _a,Jo[] _add) {
        for(Jo a:_add) {
            _a.add(a);
        }
        return _a;
    }
    
    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public byte[] getBytesRaw(Jo _o, String _key, byte[] _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsBytes();
        } else {
            return _default;
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public int getInt(Jo _o, String _key, int _default) {
        if (_o.has(_key)) {
            return (int)_o.get(_key).getAsLong();
        } else {
            return _default;
        }
    }
    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public int[] getints(Ja _a,int[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        int[] v = new int[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsInt();
        return v;
    }
    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public Integer[] getInts(Ja _a,Integer[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        Integer[] v = new Integer[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsInt();
        return v;
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public long getLong(Jo _o, String _key, long _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsLong();
        } else {
            return _default;
        }
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public long[] getlongs(Ja _a,long[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        long[] v = new long[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsLong();
        return v;
    }
    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public Long[] getLongs(Ja _a,Long[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        Long[] v = new Long[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsLong();
        return v;
    }


    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public double getDouble(Jo _o, String _key, double _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsDouble();
        } else {
            return _default;
        }
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public double[] getdoubles(Ja _a,double[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        double[] v = new double[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsDouble();
        return v;
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public Double[] getDoubles(Ja _a,Double[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        Double[] v = new Double[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsDouble();
        return v;
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public float[] getfloats(Ja _a,float[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        float[] v = new float[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsFloat();
        return v;
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public Float[] getFloats(Ja _a,Float[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        Float[] v = new Float[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsFloat();
        return v;
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public boolean getBoolean(Jo _o, String _key, boolean _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsBoolean();
        } else {
            return _default;
        }
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public boolean[] getbooleans(Ja _a,boolean[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        boolean[] v = new boolean[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsBoolean();
        return v;
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public Boolean[] getBooleans(Ja _a,Boolean[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        Boolean[] v = new Boolean[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsBoolean();
        return v;
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public String getString(Jo _o, String _key, String _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsString();
        } else {
            return _default;
        }
    }

    /**
     *
     * @param _a
     * @param _default
     * @return
     */
    static public String[] getStrings(Ja _a,String[] _default) {
        if (_a == null) return _default;
        int l = _a.size();
        String[] v = new String[_a.size()];
        for(int i=0;i<l;i++) v[i] = _a.get(i).getAsString();
        return v;
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public Jo getObject(Jo _o, String _key, Jo _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsJsonObject();
        } else {
            return _default;
        }
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _default
     * @return
     */
    static public Ja getArray(Jo _o, String _key, Ja _default) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsJsonArray();
        } else {
            return _default;
        }
    }
    /**
     *
     * @param _strings
     * @return
     */
    static public Ja toJsonArray(String[] _strings) {
        Ja array = new Ja();
        for (int i = 0; i < _strings.length; i++) {
            array.add(new Jv(_strings[i]));
        }
        return array;
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public Date getDate(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            return new Date(_o.get(_key).getAsLong());
        } else {
            throw new RuntimeException("missing value for " + _key);
        }
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public Date ensureDate(Jo _o, String _key,Date _v) {
        if (_o.has(_key)) {
            return new Date(_o.get(_key).getAsLong());
        } else {
            _o.add(_key, new Jv(_v.getTime()));
            return _v;
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public byte[] getBytesRaw(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            return _o.get(_key).getAsBytes();
        } else {
            throw new RuntimeException("missing value for " + _key);
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public byte[] ensureBytesRaw(Jo _o, String _key,byte[] _v) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsBytes();
        } else {
             _o.add(_key, new Jv(_v));
            return _v;
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public int getInt(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            return _o.get(_key).getAsInt();
        } else {
            throw new RuntimeException("missing value for " + _key);
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public int ensureInt(Jo _o, String _key,int _v) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsInt();
        } else {
             _o.add(_key, new Jv(_v));
            return _v;
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public long getLong(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            return _o.get(_key).getAsLong();
        } else {
            throw new RuntimeException("missing value for " + _key);
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public long ensureLong(Jo _o, String _key,long _v) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsLong();
        } else {
             _o.add(_key, new Jv(_v));
            return _v;
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public double getDouble(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            return _o.get(_key).getAsDouble();
        } else {
            throw new RuntimeException("missing value for " + _key);
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public double ensureDouble(Jo _o, String _key,double _v) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsDouble();
        } else {
             _o.add(_key, new Jv(_v));
            return _v;
        }
    }

    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public boolean getBoolean(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            return _o.get(_key).getAsBoolean();
        } else {
            throw new RuntimeException("missing value for " + _key);
        }
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public boolean ensureBoolean(Jo _o, String _key,boolean _v) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsBoolean();
        } else {
             _o.add(_key, new Jv(_v));
            return _v;
        }
    }
    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public String getString(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonPrimitive() && p.getAsJsonPrimitive().isString()) {
                return p.getAsString();
            }
        }
        throw new RuntimeException("missing value for " + _key);
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public String ensureString(Jo _o, String _key,String _v) {
        if (_o.has(_key)) {
            return _o.get(_key).getAsString();
        } else {
             _o.add(_key, new Jv(_v));
            return _v;
        }
    }
    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public String getStringEscaped(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonPrimitive() && p.getAsJsonPrimitive().isString()) {
                return unescape(p.getAsString());
            }
        }
        throw new RuntimeException("missing value for " + _key);
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public String ensureStringEscaped(Jo _o, String _key,String _v) {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonPrimitive() && p.getAsJsonPrimitive().isString()) {
                return unescape(p.getAsString());
            }
        }
        _o.add(_key, new Jv(escape(_v)));
        return _v;
    }
    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public Jo getObject(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonObject()) {
                return p.getAsJsonObject();
            }
        }
        throw new RuntimeException("missing value for " + _key);
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public Jo ensureObject(Jo _o, String _key,Jo _v) {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonObject()) {
                return p.getAsJsonObject();
            }
        }
        _o.add(_key, _v);
        return _v;
    }
    /**
     *
     * @param _o
     * @param _key
     * @return
     * @throws Exception
     */
    static public Ja getArray(Jo _o, String _key) throws Exception {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonArray()) {
                return p.getAsJsonArray();
            }
        }
        throw new RuntimeException("missing value for " + _key);
    }
    /**
     *
     * @param _o
     * @param _key
     * @param _v
     * @return
     */
    static public Ja ensureArray(Jo _o, String _key,Ja _v) {
        if (_o.has(_key)) {
            AJ p = _o.get(_key);
            if (p.isJsonNull()) {
                return null;
            }
            if (p.isJsonObject()) {
                return p.getAsJsonArray();
            }
        }
        _o.add(_key, _v);
        return _v;
    }
    /**
     *
     * @param s
     * @return
     */
    public static String unescape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            OUTER: switch (ch) {
                case '\\':
                    char nch = s.charAt(i+1);
                    switch (nch) {
                        case '/':
                            sb.append('/');
                            i++;
                            break OUTER;
                        case '\\':
                            sb.append('\\');
                            i++;
                            break OUTER;
                        case '"':
                            sb.append('"');
                            i++;
                            break OUTER;
                        case 'u':
                            char[] unicode = new char[4];
                            for(int a=i+2,u=0;u<4;u++,a++) unicode[u] =  s.charAt(a);
                            int c = Integer.parseInt(new String(unicode), 16);
                            sb.append(c);
                            i+=6;
                            break OUTER;
                    }
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
    /**
     *
     * @param s
     * @return
     */
    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
