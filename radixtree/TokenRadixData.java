/*
    Copyright 2009 Semantic Discovery, Inc. (www.semanticdiscovery.com)

    This file is part of the Semantic Discovery Toolkit.

    The Semantic Discovery Toolkit is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Semantic Discovery Toolkit is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with The Semantic Discovery Toolkit.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.sd.text.radixtree;

import java.util.Collections;
import java.util.List;

/**
 * Data to hold in a token radix tree node.
 * <p>
 * @author Spence Koehler
 * @author Jeff Tsay
 */
public class TokenRadixData<T, V> {

  private List<T> tokens;
  private boolean real;
  private V value;

  public TokenRadixData() {
    this(Collections.<T>emptyList(), false, null);
  }

  /** Takes ownership of tokens. **/
  public TokenRadixData(List<T> tokens, boolean real, V value) {
    this.tokens = tokens;
    this.real = real;
    this.value = value;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V data) {
    this.value = data;
  }

  public List<T> getTokens() {
    return tokens;
  }

  /** Takes ownership of tokens. **/
  public void setTokens(List<T> tokens) {
    this.tokens = tokens;
  }

  public boolean isReal() {
    return real;
  }

  public void setReal(boolean datanode) {
    this.real = datanode;
  }

  public String toString() {
    final StringBuilder result = new StringBuilder();

    result.append('[');
    for (T token : tokens) {
      result.append(token.toString());
      result.append(',');
    }

    if (!tokens.isEmpty()) {
      result.setLength(result.length() - 1);
    }

    result.append(']');

    if (real) result.append('*');
    if (value != null) {
      result.append('{').append(value.toString()).append('}');
    }

    return result.toString();
  }
}
