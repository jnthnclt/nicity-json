/*
 * JReader.java.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * @author Administrator
 */
public class JStringReader {

    private int index;
    private Reader reader;
    private char lastChar;
    private boolean useLastChar;

    /**
     *
     * @param s
     */
    public JStringReader(String s) {
        this(new StringReader(s));
    }

    /**
     *
     * @param reader
     */
    public JStringReader(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.useLastChar = false;
        this.index = 0;
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

        if (nextClean() != '{') {
            throw syntaxError("A JSONObject text must begin with '{'");
        }
        for (;;) {
            c = nextClean();
            switch (c) {
            case 0:
                throw syntaxError("A JSONObject text must end with '}'");
            case '}':
                return jo;
            default:
                back();
                key = nextValue().getAsString();
            }

            /*
             * The key is followed by ':'. We will also tolerate '=' or '=>'.
             */

            c = nextClean();
            if (c == '=') {
                if (next() != '>') {
                    back();
                }
            } else if (c != ':') {
                throw syntaxError("Expected a ':' after a key");
            }
            Object nv = nextValue();
            if (key != null && nv != null && jo.get(key) == null) {
                jo.add(key,(AJ)nv);
            }

            /*
             * Pairs are separated by ','. We will also tolerate ';'.
             */

            switch (nextClean()) {
            case ';':
            case ',':
                if (nextClean() == '}') {
                    return jo;
                }
                back();
                break;
            case '}':
                return jo;
            default:
                throw syntaxError("Expected a ',' or '}'");
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
        char c = nextClean();
        char q;
        if (c == '[') {
            q = ']';
        } else if (c == '(') {
            q = ')';
        } else {
            throw syntaxError("A JSONArray text must start with '['");
        }
        if (nextClean() == ']') {
            return ja;
        }
        back();
        for (;;) {
            if (nextClean() == ',') {
                back();
                ja.add((AJ)null);
            } else {
                back();
                ja.add((AJ)nextValue());
            }
            c = nextClean();
            switch (c) {
            case ';':
            case ',':
                if (nextClean() == ']') {
                    return ja;
                }
                back();
                break;
            case ']':
            case ')':
                if (q != c) {
                    throw syntaxError("Expected a '" + new Character(q) + "'");
                }
                return ja;
            default:
                throw syntaxError("Expected a ',' or ']'");
            }
        }

    }


    


    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     */
    private void back() throws Exception {
        if (useLastChar || index <= 0) {
            throw new Exception("Stepping back two steps is not supported");
        }
        index -= 1;
        useLastChar = true;
    }

    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     */
    private char next() throws Exception {
        if (this.useLastChar) {
        	this.useLastChar = false;
            if (this.lastChar != 0) {
            	this.index += 1;
            }
            return this.lastChar;
        } 
        int c;
        try {
            c = this.reader.read();
        } catch (IOException exc) {
            throw new Exception(exc);
        }

        if (c <= 0) { // End of stream
        	this.lastChar = 0;
            return 0;
        } 
    	this.index += 1;
    	this.lastChar = (char) c;
        return this.lastChar;
    }



    /**
     * Get the next n characters.
     *
     * @param n     The number of characters to take.
     * @return      A string of n characters.
     * @throws Exception
     *   Substring bounds error if there are not
     *   n characters remaining in the source string.
     */
     private String next(int n) throws Exception {
         if (n == 0) {
             return "";
         }

         char[] buffer = new char[n];
         int pos = 0;

         if (this.useLastChar) {
        	 this.useLastChar = false;
             buffer[0] = this.lastChar;
             pos = 1;
         }

         try {
             int len;
             while ((pos < n) && ((len = reader.read(buffer, pos, n - pos)) != -1)) {
                 pos += len;
             }
         } catch (IOException exc) {
             throw new Exception(exc);
         }
         this.index += pos;

         if (pos < n) {
             throw syntaxError("Substring bounds error");
         }

         this.lastChar = buffer[n - 1];
         return new String(buffer);
     }


    /**
     * Get the next char in the string, skipping whitespace.
     * @throws Exception
     * @return  A character, or 0 if there are no more characters.
     */
    private char nextClean() throws Exception {
        for (;;) {
            char c = next();
            if (c == 0 || c > ' ') {
                return c;
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
    private Jv nextString(char quote) throws Exception {
        char c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = next();
            switch (c) {
            case 0:
            case '\n':
            case '\r':
                throw syntaxError("Unterminated string");
            case '\\':
                c = next();
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
                    sb.append((char)Integer.parseInt(next(4), 16));
                    break;
                case '"':
                case '\'':
                case '\\':
                case '/':
                	sb.append(c);
                	break;
                default:
                    throw syntaxError("Illegal escape.");
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


    

    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws Exception If syntax error.
     *
     * @return An object.
     */
    private AJ nextValue() throws Exception {
        char c = nextClean();
        String s;

        switch (c) {
            case '"':
            case '\'':
                return nextString(c);
            case '{':
                back();
                Jo jo = new Jo();
                readJo(jo);
                return jo;
            case '[':
            case '(':
                back();
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
            c = next();
        }
        back();

        s = sb.toString().trim();
        if (s.equals("")) {
            throw syntaxError("Missing value");
        }
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

    private Exception syntaxError(String message) {
        return new Exception(message + toString());
    }

    @Override
    public String toString() {
        return " at character " + index;
    }
}
