/*
 * AJ.java.java
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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Administrator
 */
public abstract class AJ {

  /*package*/ boolean isJsonArray() {
    return this instanceof Ja;
  }
  /*package*/ boolean isJsonObject() {
    return this instanceof Jo;
  }
  /*package*/ boolean isJsonPrimitive() {
    return this instanceof Jv;
  }
  /*package*/ boolean isJsonNull() {
    return this instanceof Jnull;
  }
  /*package*/ Jo getAsJsonObject() {
    if (isJsonObject()) {
      return (Jo) this;
    } else {
      throw new IllegalStateException("This is not a JSON Object.");
    }
  }
  /*package*/ Ja getAsJsonArray() {
    if (isJsonArray()) {
      return (Ja) this;
    } else {
      throw new IllegalStateException("This is not a JSON Array.");
    }
  }
  /*package*/ Jv getAsJsonPrimitive() {
    if (isJsonPrimitive()) {
      return (Jv) this;
    } else {
      throw new IllegalStateException("This is not a JSON Primitive.");
    }
  }
  /*package*/ Jnull getAsJsonNull() {
    if (isJsonNull()) {
      return (Jnull) this;
    } else {
      throw new IllegalStateException("This is not a JSON Null.");
    }
  }

  /*package*/ byte[] getAsBytes() {
    throw new UnsupportedOperationException();
  }

  /*package*/ boolean getAsBoolean() {
    throw new UnsupportedOperationException();
  }


  /*package*/ Number getAsNumber() {
    throw new UnsupportedOperationException();
  }

  /*package*/ String getAsString() {
    throw new UnsupportedOperationException();
  }

  /*package*/ double getAsDouble() {
    throw new UnsupportedOperationException();
  }

  /*package*/ float getAsFloat() {
    throw new UnsupportedOperationException();
  }

  /*package*/ long getAsLong() {
    throw new UnsupportedOperationException();
  }

  /*package*/ int getAsInt() {
    throw new UnsupportedOperationException();
  }

  /*package*/ byte getAsByte() {
    throw new UnsupportedOperationException();
  }

  /*package*/ char getAsCharacter() {
    throw new UnsupportedOperationException();
  }

  /*package*/ BigDecimal getAsBigDecimal() {
    throw new UnsupportedOperationException();
  }

  /*package*/ BigInteger getAsBigInteger() {
    throw new UnsupportedOperationException();
  }

  /*package*/ short getAsShort() {
    throw new UnsupportedOperationException();
  }

  
  @Override
  public String toString() {
    try {
      JStringWriter sb = JStringWriter.defaultWriter();
      toString(sb);
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  /**
   *
   * @param w
   * @throws IOException
   */
  protected abstract void toString(AStringWriter w) throws IOException;
  /**
   *
   * @param bw
   * @throws IOException
   */
  protected abstract void toBinary(ABinaryWriter bw) throws IOException;
}
