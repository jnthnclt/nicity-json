/*
 * AStringWriter.java
 *
 * Created on Apr 5, 2010, 10:28:51 PM
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
abstract public class AStringWriter {

    /**
     *
     * @param wrap
     * @throws IOException
     */
    abstract public void key(CharSequence wrap) throws IOException;
    /**
     *
     * @param wrap
     * @throws IOException
     */
    abstract public void value(CharSequence wrap) throws IOException;

    /**
     *
     * @throws IOException
     */
    abstract public void openCurly() throws IOException;
    /**
     *
     * @throws IOException
     */
    abstract public void closeCurly() throws IOException;
    /**
     *
     * @throws IOException
     */
    abstract public void openBrace() throws IOException;
    /**
     *
     * @throws IOException
     */
    abstract public void closeBrace() throws IOException;
    /**
     *
     * @throws IOException
     */
    abstract public void newline() throws IOException;
    /**
     *
     * @param c
     * @throws IOException
     */
    abstract public void append(char c) throws IOException;

}
