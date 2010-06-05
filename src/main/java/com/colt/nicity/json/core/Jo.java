/*
 * Jo.java.java
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public final class Jo extends AJ {
  private final Map<String, AJ> fields;
  /**
   *
   */
  public Jo() {
    fields = new LinkedHashMap<String, AJ>();
  }

  void add(String key, AJ value) {
    fields.put(key, value);
  }
  AJ remove(String key) {
    return fields.remove(key);
  }
  boolean has(String key) {
    return fields.containsKey(key);
  }
  AJ get(String key) {
    if (fields.containsKey(key)) {
      AJ member = fields.get(key);
      return member == null ? Jnull.cNull() : member;
    } else {
      return null;
    }
  }

  /**
   *
   * @return
   */
  public Set<String> keys() {
      return fields.keySet();
  }
  /**
   *
   * @return
   */
  public Set<Entry<String, AJ>> entrySet() {
    return fields.entrySet();
  }
 
  /**
   *
   * @param sb
   * @throws IOException
   */
  @Override
  public void toString(AStringWriter sb) throws IOException {
    sb.openCurly();
    sb.newline();
    boolean first = true;
    for (Map.Entry<String, AJ> entry : fields.entrySet()) {
      if (first) {
        first = false;
      } else {
        sb.append(',');
        sb.newline();
      }
      sb.key("\""+entry.getKey()+"\"");
      sb.append(':');
      entry.getValue().toString(sb);
    }
    sb.newline();
    sb.closeCurly();
  }

  /**
   *
   * @param bw
   * @throws IOException
   */
    @Override
  public void toBinary(ABinaryWriter bw) throws IOException {
      Set<Entry<String, AJ>> set = fields.entrySet();
      bw.appendObject(set.size());
      for (Map.Entry<String, AJ> entry : set) {
          bw.appendKey(entry.getKey());
          entry.getValue().toBinary(bw);
      }
  }
}
