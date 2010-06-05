/*
 * JWriter.java.java
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

import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class JStringWriter extends AStringWriter {
    static char space = ' ';
    static JStringWriter defaultWriter() {
        return new JStringWriter(new StringBuffer(),1);
    }
    private Appendable sb;
    private int indent = 0;
    private int indention = 0;
    private boolean addDent = false;
    /**
     *
     * @param _sb
     * @param _indention
     */
    public JStringWriter(Appendable _sb,int _indention) {
        sb = _sb;
        indention = _indention;
    }
    private void addDent() throws IOException {
        if (!addDent) return;
        for(int i=0;i<(indent*indention);i++) sb.append(space);
        addDent = false;
    }

    /**
     *
     * @throws IOException
     */
    public void openCurly() throws IOException {
        addDent = true;
        addDent();
        sb.append('{');
        indent++;
        addDent = true;
    }
    /**
     *
     * @throws IOException
     */
    public void closeCurly() throws IOException {
        addDent = true;
        indent--;
        addDent();
        sb.append('}');
    }
    /**
     *
     * @throws IOException
     */
    public void openBrace() throws IOException {
        addDent();
        sb.append('[');
        indent++;
    }
    /**
     *
     * @throws IOException
     */
    public void closeBrace() throws IOException {
        indent--;
        addDent();
        sb.append(']');
    }
    /**
     *
     * @throws IOException
     */
    public void newline() throws IOException {
        addDent = true;
        if (indention > 0) sb.append('\n');
    }
    /**
     *
     * @param csq
     * @throws IOException
     */
    public void key(CharSequence csq) throws IOException {
        addDent();
        sb.append(csq);
    }
    /**
     *
     * @param csq
     * @throws IOException
     */
    public void value(CharSequence csq) throws IOException {
        addDent();
        sb.append(csq);
    }
   
    /**
     *
     * @param c
     * @throws IOException
     */
    public void append(char c) throws IOException {
        sb.append(c);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
