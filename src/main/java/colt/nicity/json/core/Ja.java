/*
 * Ja.java.java
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
package colt.nicity.json.core;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public final class Ja extends AJ implements Iterable<AJ> {
  private final List<AJ> elements;
  /**
   *
   */
  public Ja() {
    elements = new LinkedList<AJ>();
  }
  /**
   *
   * @param element
   */
  public void add(AJ element) {
    elements.add(element);
  }
  /**
   *
   * @param array
   */
  public void addAll(Ja array) {
    elements.addAll(array.elements);
  }
  void reverse() {
    Collections.reverse(elements);
  }
  /**
   *
   * @return
   */
  public int size() {
    return elements.size();
  }
    @Override
  public Iterator<AJ> iterator() {
    return elements.iterator();
  }
  /**
   *
   * @param i
   * @return
   */
  public AJ get(int i) {
    return elements.get(i);
  }

  /**
   *
   * @param w
   * @throws IOException
   */
  @Override
  protected void toString(AStringWriter w) throws IOException {
    w.openBrace();
    w.newline();
    boolean first = true;
    for (AJ element : elements) {
      if (first) {
        first = false;
      } else {
        w.append(',');
        w.newline();
      }
      element.toString(w);
    }
    w.newline();
    w.closeBrace();
  }
  /**
   *
   * @param bw
   * @throws IOException
   */
    @Override
  protected void toBinary(ABinaryWriter bw) throws IOException {
      Object[] copy = elements.toArray();
      bw.appendArray(copy.length);
      for( Object c:copy) ((AJ)c).toBinary(bw);
  }
}
