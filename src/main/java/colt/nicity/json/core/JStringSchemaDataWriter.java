/*
 * JSchemaDataStringWriter.java
 *
 * Created on Apr 5, 2010, 10:26:03 PM
 *
 * Copyright Apr 5, 2010 Jonathan Colt 
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

import java.io.IOException;

/**
*
* @author Jonathan Colt
*/
public class JStringSchemaDataWriter extends AStringWriter {


    static JStringSchemaDataWriter defaultWriter() {
        return new JStringSchemaDataWriter(new StringBuffer(),new StringBuffer());
    }
    private long schemaHash = 0;
    private Appendable schema;
    private Appendable data;
    /**
     *
     * @param schema
     * @param data
     */
    public JStringSchemaDataWriter(Appendable schema,Appendable data) {
        this.schema = schema;
        this.data = data;
    }

    /**
     *
     * @return
     */
    public String schema() {
        return schema.toString();
    }
    /**
     *
     * @return
     */
    public String data() {
        return data.toString();
    }
    
    /**
     *
     * @throws IOException
     */
    @Override
    public void openCurly() throws IOException {
        schema.append('{');
        schemaHash = hash(schemaHash,Character.toString('{'));
    }
    /**
     *
     * @throws IOException
     */
    @Override
    public void closeCurly() throws IOException {
        schema.append('}');
        schemaHash = hash(schemaHash,Character.toString('}'));
    }
    /**
     *
     * @throws IOException
     */
    @Override
    public void openBrace() throws IOException {
        schema.append('[');
        schemaHash = hash(schemaHash,Character.toString('['));
    }
    /**
     *
     * @throws IOException
     */
    @Override
    public void closeBrace() throws IOException {
        schema.append(']');
        schemaHash = hash(schemaHash,Character.toString(']'));
    }
    /**
     *
     * @throws IOException
     */
    @Override
    public void newline() throws IOException {
    }
    /**
     *
     * @param csq
     * @throws IOException
     */
    @Override
    public void key(CharSequence csq) throws IOException {
        schema.append(csq);
        schemaHash = hash(schemaHash,csq);
    }
    /**
     *
     * @param csq
     * @throws IOException
     */
    @Override
    public void value(CharSequence csq) throws IOException {
        schema.append("v");//?? is this abs needed
        data.append(csq);
        data.append(',');
    }
    /**
     *
     * @param c
     * @throws IOException
     */
    @Override
    public void append(char c) throws IOException {
        schema.append(c);
        schemaHash = hash(schemaHash,Character.toString(c));
    }


    /**
     *
     * @return
     */
    public long getSchemaHash() {
        return schemaHash;
    }
    static final private long hash(long _schemaHash,CharSequence _key) {
        long randMult = 0x5DEECE66DL;
        long randAdd = 0xBL;
        long randMask = (1L << 48) - 1;
        int _start = 0;
        int _length = _key.length();
        long seed = _length;
        long result = _schemaHash;
        for (int i = _start; i < _start + _length; i++) {
            long x = (seed * randMult + randAdd) & randMask;
            seed = x;
            x %= _length;
            result += (_key.charAt(i) + 128) * x;
        }
        if (result < 0) {
            result = -result;
        }
        return result;
    }
}
