/*
 * JStringParse.java
 *
 * Created on Apr 7, 2010, 10:50:10 PM
 *
 * Copyright Apr 7, 2010 Jonathan Colt 
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
import java.io.Reader;

/**
 *
 * @author Jonathan Colt
 */
public class JStringParse {

    private Reader reader;
    private int index;
    private char lastChar;
    private boolean useLastChar;

    /**
     *
     * @param reader
     */
    public JStringParse(Reader reader) {
        this.reader = reader;
        this.useLastChar = false;
        this.index = 0;
    }

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     * @throws Exception
     */
    public void back() throws Exception {
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
     * @throws Exception
     */
    public char next() throws Exception {
        if (this.useLastChar) {
            this.useLastChar = false;
            if (this.lastChar != 0) {
                this.index += 1;
            }
            return this.lastChar;
        }
        int c;
        try {
            c = reader.read();
        }
        catch (IOException exc) {
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
    public String next(int n) throws Exception {
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
        }
        catch (IOException exc) {
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
    public char nextClean() throws Exception {
        for (;;) {
            char c = next();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    /**
     *
     * @param message
     * @return
     */
    public Exception syntaxError(String message) {
        return new Exception(message + toString());
    }

    @Override
    public String toString() {
        return " at character " + index;
    }
}
