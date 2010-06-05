/*
 * Jnull.java.java
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
public final class Jnull extends AJ {
    /**
     *
     * @return
     */
    public static Jnull cNull() {
    return INSTANCE;
  }
  private static final Jnull INSTANCE = new Jnull();
  private Jnull() {}
  
  /**
   *
   * @param sb
   * @throws IOException
   */
  @Override
  protected void toString(AStringWriter sb) throws IOException {
    sb.value("null");
  }

  /**
   *
   * @param bw
   * @throws IOException
   */
  protected void toBinary(ABinaryWriter bw) throws IOException {
      
  }

  @Override
  public int hashCode() {
    return Jnull.class.hashCode();
  }
  @Override
  public boolean equals(Object other) {
    return other instanceof Jnull;
  }
}
