/*
 * JioTests.java
 *
 * Created on Aug 22, 2010, 9:19:45 PM
 *
 * Copyright Aug 22, 2010 Jonathan Colt 
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
package com.colt.nicity.json;

import colt.nicity.json.core.JBinarySchemaDataReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import colt.nicity.json.core.JBinarySchemaDataWriter;
import colt.nicity.json.core.JStringSchemaDataReader;
import colt.nicity.json.core.JStringSchemaDataWriter;
import colt.nicity.json.core.Ja;
import colt.nicity.json.core.Jo;
import colt.nicity.json.core.UJson;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jonathan Colt
 */
public class JioTests {

    @Test
    public void stringSchemaDataWriter() throws Exception {
        Jo wjo = new Jo();
        UJson.add(wjo, "firstName","John");
        UJson.add(wjo,"lastName","Doe");
        UJson.add(wjo,"age",30);
        UJson.add(wjo,"happy",true);

        Ja ja = new Ja();
        UJson.add(ja, new long[]{1,2,3,4,5,6,7,8,9});
        UJson.add(wjo,"count",ja);


        StringBuffer schema = new StringBuffer();
        StringBuffer data = new StringBuffer();
        JStringSchemaDataWriter w = new JStringSchemaDataWriter(schema,data);

        wjo.toString(w);
        System.out.println("schema=\n"+schema+"\n");
        System.out.println("data=\n"+data+"\n");
        System.out.println((schema.length()+data.length())+"bytes");

        JStringSchemaDataReader r = new JStringSchemaDataReader(new StringReader(schema.toString()), new StringReader(data.toString()));
        Jo rjo = r.readJo(new Jo());

        System.out.println(rjo.toString());
        System.out.println((rjo.toString().length())+"bytes");

        assertEquals(wjo.toString(), rjo.toString());
    }

     @Test
    public void binarySchemaDataWriter() throws Exception {
        Jo wjo = new Jo();
        UJson.add(wjo, "firstName","John");
        UJson.add(wjo,"lastName","Doe");
        UJson.add(wjo,"age",30);
        UJson.add(wjo,"happy",true);

        Ja ja = new Ja();
        UJson.add(ja, new long[]{1,2,3,4,5,6,7,8,9});
        UJson.add(wjo,"count",ja);


        ByteArrayOutputStream wSchema = new ByteArrayOutputStream();
        ByteArrayOutputStream wData = new ByteArrayOutputStream();
        JBinarySchemaDataWriter w = new JBinarySchemaDataWriter(wSchema,wData);
        wjo.toBinary(w);
        System.out.println("binarysize="+(wSchema.size()+wData.size())+"bytes");

        ByteArrayInputStream rSchema = new ByteArrayInputStream(wSchema.toByteArray());
        ByteArrayInputStream rData = new ByteArrayInputStream(wData.toByteArray());
        JBinarySchemaDataReader r = new JBinarySchemaDataReader(rSchema, rData);
        Jo rjo = r.readJo(new Jo());

        System.out.println(rjo.toString());
        System.out.println((rjo.toString().length())+"bytes");

        assertEquals(wjo.toString(), rjo.toString());
    }

    
}
