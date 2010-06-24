/*
 * ABinaryWriter.java
 *
 * Created on Apr 4, 2010, 7:07:35 PM
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


package colt.nicity.json.core;

import java.io.IOException;



/**
*
* @author Jonathan Colt
*/
abstract public class ABinaryWriter {

    abstract void appendArray(int length) throws IOException;

    abstract void appendObject(int fieldCount) throws IOException;

    abstract void appendKey(String key) throws IOException;

    abstract void appendValue(Object value) throws IOException;
}