/*
 * JStringSchemaDataReader.java
 *
 * Created on Apr 5, 2010, 10:54:19 PM
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


package com.colt.nicity.json.core;

import java.io.BufferedReader;
import java.io.Reader;

/**
*
* @author Jonathan Colt
*/
public class JStringSchemaDataReader {
    //!! incomplete impl

    private JStringParse pschema;
    private JStringParse pdata;

    /**
     *
     * @param schema
     * @param data
     */
    public JStringSchemaDataReader(Reader schema,Reader data) {
        pschema = new JStringParse((schema.markSupported() ? schema : new BufferedReader(schema)));
        pdata = new JStringParse(data);
    }

    /**
     *
     * @param jo
     * @return
     * @throws Exception
     */
    public Jo readJo(Jo jo) throws Exception {
        if (jo == null) jo = new Jo();
        char c;
        String key;

        if (pschema.nextClean() != '{') {
            throw pschema.syntaxError("A JSONObject text must begin with '{'");
        }
        for (;;) {
            c = pschema.nextClean();
            switch (c) {
            case 0:
                throw pschema.syntaxError("A JSONObject text must end with '}'");
            case '}':
                return jo;
            default:
                pschema.back();
                key = nextKey().getAsString();
            }

            /*
             * The key is followed by ':'. We will also tolerate '=' or '=>'.
             */

            c = pschema.nextClean();
            if (c == '=') {
                if (pschema.next() != '>') {
                    pschema.back();
                }
            } else if (c != ':') {
                throw pschema.syntaxError("Expected a ':' after a key");
            }
            Object nv = nextValue();
            if (key != null && nv != null && jo.get(key) == null) {
                jo.add(key,(AJ)nv);
            }

            /*
             * Pairs are separated by ','. We will also tolerate ';'.
             */

            switch (pschema.nextClean()) {
            case ';':
            case ',':
                if (pschema.nextClean() == '}') {
                    return jo;
                }
                pschema.back();
                break;
            case '}':
                return jo;
            default:
                throw pschema.syntaxError("Expected a ',' or '}'");
            }
        }
    }

    /**
     *
     * @param ja
     * @return
     * @throws Exception
     */
    public Ja readJa(Ja ja) throws Exception {
        if (ja == null) ja = new Ja();
        char c = pschema.nextClean();
        char q;
        if (c == '[') {
            q = ']';
        } else if (c == '(') {
            q = ')';
        } else {
            throw pschema.syntaxError("A JSONArray text must start with '['");
        }
        if (pschema.nextClean() == ']') {
            return ja;
        }
        pschema.back();
        for (;;) {
            if (pschema.nextClean() == ',') {
                pschema.back();
                ja.add((AJ)null);
            } else {
                pschema.back();
                ja.add((AJ)nextValue());
            }
            c = pschema.nextClean();
            switch (c) {
            case ';':
            case ',':
                if (pschema.nextClean() == ']') {
                    return ja;
                }
                pschema.back();
                break;
            case ']':
            case ')':
                if (q != c) {
                    throw pschema.syntaxError("Expected a '" + new Character(q) + "'");
                }
                return ja;
            default:
                throw pschema.syntaxError("Expected a ',' or ']'");
            }
        }

    }


    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     * @param quote The quoting character, either
     *      <code>"</code>&nbsp;<small>(double quote)</small> or
     *      <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return      A String.
     * @throws Exception Unterminated string.
     */
    private Jv nextString(JStringParse parse,char quote) throws Exception {
        char c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = parse.next();
            switch (c) {
            case 0:
            case '\n':
            case '\r':
                throw parse.syntaxError("Unterminated string");
            case '\\':
                c = parse.next();
                switch (c) {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    sb.append((char)Integer.parseInt(pschema.next(4), 16));
                    break;
                case '"':
                case '\'':
                case '\\':
                case '/':
                	sb.append(c);
                	break;
                default:
                    throw parse.syntaxError("Illegal escape.");
                }
                break;
            default:
                if (c == quote) {
                    return new Jv(sb.toString());
                }
                sb.append(c);
            }
        }
    }

    private AJ nextKey() throws Exception {
        char c = pschema.nextClean();
        String s;
        switch (c) {
            case '"':
            case '\'':
                return nextString(pschema,c);
            case '{':
                pschema.back();
                Jo jo = new Jo();
                readJo(jo);
                return jo;
            case '[':
            case '(':
                pschema.back();
                Ja ja = new Ja();
                readJa(ja);
                return ja;
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuffer sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = pschema.next();
        }
        pschema.back();
        s = sb.toString().trim();
        if (s.equals("")) {
            throw pschema.syntaxError("Missing value");
        }
        return stringToValue(s);
    }


    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws Exception If syntax error.
     *
     * @return An object.
     */
    private AJ nextValue() throws Exception {
        char c = pschema.nextClean();
        String s;

        switch (c) {
            case '"':
            case '\'':
                nextString(pschema,c);
                c = pdata.nextClean();
                return nextString(pdata,c);
            case '{':
                pschema.back();
                Jo jo = new Jo();
                readJo(jo);
                return jo;
            case '[':
            case '(':
                pschema.back();
                Ja ja = new Ja();
                readJa(ja);
                return ja;
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        // burn through schemas value
        StringBuffer sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = pschema.next();
        }
        pschema.back();
        s = sb.toString().trim();
        if (s.equals("")) {
            throw pschema.syntaxError("Missing value");
        }

        // read actual data value
        c = pdata.nextClean();
        
        switch (c) {
            case '"':
            case '\'':
                AJ ns = nextString(pdata,c);
                if (pdata.nextClean() != ',') pdata.syntaxError("expected ','");
                return ns;
        }
        sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = pdata.next();
        }
        pdata.back();
        s = sb.toString().trim();
        if (s.equals("")) {
            throw pdata.syntaxError("Missing value");
        }
        if (pdata.nextClean() != ',') pdata.syntaxError("expected ','");
        return stringToValue(s);
    }

    static private AJ stringToValue(String s) {
        if (s.equals("")) {
            return new Jv(s);
        }
        if (s.equalsIgnoreCase("true")) {
            return new Jv(Boolean.TRUE);
        }
        if (s.equalsIgnoreCase("false")) {
            return new Jv(Boolean.FALSE);
        }
        if (s.equalsIgnoreCase("null")) {
            return Jnull.cNull();
        }

        /*
         * If it might be a number, try converting it. We support the 0- and 0x-
         * conventions. If a number cannot be produced, then the value will just
         * be a string. Note that the 0-, 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser is free to accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = s.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0') {
                if (s.length() > 2 &&
                        (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                    try {
                        return new Jv(new Integer(Integer.parseInt(s.substring(2),
                                16)));
                    } catch (Exception e) {
                        /* Ignore the error */
                    }
                } else {
                    try {
                        return new Jv(new Integer(Integer.parseInt(s, 8)));
                    } catch (Exception e) {
                        /* Ignore the error */
                    }
                }
            }
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1) {
                    return new Jv(Double.valueOf(s));
                } else {
                    Long myLong = new Long(s);
                    if (myLong.longValue() == myLong.intValue()) {
                        return new Jv(new Integer(myLong.intValue()));
                    } else {
                        return new Jv(myLong);
                    }
                }
            }  catch (Exception f) {
                /* Ignore the error */
            }
        }
        return new Jv(s);
    }

    
}