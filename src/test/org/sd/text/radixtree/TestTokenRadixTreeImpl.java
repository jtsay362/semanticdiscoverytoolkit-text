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


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

/**
 * JUnit Tests for the RadixTreeImpl class.
 * <p>
 * @author Spence Koehler
 */
public class TestTokenRadixTreeImpl extends TestCase {

  public TestTokenRadixTreeImpl(String name) {
    super(name);
  }


  public void testInsert() {
    TokenRadixTreeImpl<String, String> trie = new TokenRadixTreeImpl<String, String>();

    insert(trie, "cats are cool");
    insert(trie, "cats are furry");
    insert(trie, "dogs are smelly");

    boolean result = false;
    try {
      trie.insert(makeTokenList("cats are furry"), "cats are soft");
    }
    catch (IllegalStateException e) {
      result = true;
    }

    assertTrue(result);

    findAndVerify(trie, "cats are cool");
    findAndVerify(trie, "cats are furry");
    findAndVerify(trie, "dogs are smelly");
  }

  public void testDelete() {
    TokenRadixTreeImpl<String, String> trie = new TokenRadixTreeImpl<String, String>();

    insert(trie, "big apple");
    insert(trie, "big apple shack");
    insert(trie, "big apple shack cream");
    insert(trie, "big apple pie");
    insert(trie, "big apple attack");

    assertTrue(contains(trie, "big apple"));
    assertTrue(remove(trie, "big apple"));
    assertFalse(contains(trie, "big apple"));

    assertTrue(contains(trie, "big apple pie"));
    assertTrue(remove(trie, "big apple pie"));
    assertFalse(contains(trie, "big apple pie"));

    assertTrue(contains(trie, "big apple shack"));
    assertTrue(remove(trie, "big apple shack"));
    assertFalse(contains(trie, "big apple shack"));

    // try to delete "big apple" again this should fail
    assertFalse(remove(trie, "big apple"));

    assertTrue(contains(trie, "big apple shack cream"));
    assertTrue(contains(trie, "big apple attack"));

    // try to delete "big" this should fail.
    assertFalse(remove(trie, "big"));
  }

  /*
  public void testFind() {
    RadixTreeImpl<String> trie = new RadixTreeImpl<String>();

    insert(trie, "apple", "apple");
    insert(trie, "appleshack", "appleshack");
    insert(trie, "appleshackcream", "appleshackcream");
    insert(trie, "applepie", "applepie");
    insert(trie, "ape", "ape");

    // we shou7ld be able to find all of these
    assertNotNull(trie.find("apple"));
    assertNotNull(trie.find("appleshack"));
    assertNotNull(trie.find("appleshackcream"));
    assertNotNull(trie.find("applepie"));
    assertNotNull(trie.find("ape"));

    // try to delete "apple" again this should fail
    assertNull(trie.find("ap"));
    assertNull(trie.find("apple2"));
    assertNull(trie.find("appl"));
    assertNull(trie.find("app"));
    assertNull(trie.find("appples"));
  }

  public void testContains() {
    RadixTreeImpl<String> trie = new RadixTreeImpl<String>();

    insert(trie, "apple", "apple");
    insert(trie, "appleshack", "appleshack");
    insert(trie, "appleshackcream", "appleshackcream");
    insert(trie, "applepie", "applepie");
    insert(trie, "ape", "ape");

    // we shou7ld be able to find all of these
    assertTrue(contains(trie, "apple"));
    assertTrue(contains(trie, "appleshack"));
    assertTrue(contains(trie, "appleshackcream"));
    assertTrue(contains(trie, "applepie"));
    assertTrue(contains(trie, "ape"));

    // try to delete "apple" again this should fail
    assertFalse(contains(trie, "ap"));
    assertFalse(contains(trie, "apple2"));
    assertFalse(contains(trie, "appl"));
    assertFalse(contains(trie, "app"));
    assertFalse(contains(trie, "appples"));
  }

  public void testContains2() {
    RadixTreeImpl<String> trie = null;

    trie = new RadixTreeImpl<String>();
    insert(trie, "abba", "abba");
    insert(trie, "abab", "abab");
    insert(trie, "baba", "baba");
    assertTrue(contains(trie, "abba"));
    assertTrue(contains(trie, "abab"));
    assertTrue(contains(trie, "baba"));


    trie = new RadixTreeImpl<String>();
    insert(trie, "abba", "abba");
    insert(trie, "baba", "baba");
    insert(trie, "abab", "abab");
    assertTrue(contains(trie, "abba"));
    assertTrue(contains(trie, "abab"));
    assertTrue(contains(trie, "baba"));


    trie = new RadixTreeImpl<String>();
    insert(trie, "baba", "baba");
    insert(trie, "abab", "abab");
    insert(trie, "abba", "abba");
    assertTrue(contains(trie, "abba"));
    assertTrue(contains(trie, "abab"));
    assertTrue(contains(trie, "baba"));


    trie = new RadixTreeImpl<String>();
    insert(trie, "baba", "baba");
    insert(trie, "abba", "abba");
    insert(trie, "abab", "abab");
    assertTrue(contains(trie, "abba"));
    assertTrue(contains(trie, "abab"));
    assertTrue(contains(trie, "baba"));


    trie = new RadixTreeImpl<String>();
    insert(trie, "abab", "abab");
    insert(trie, "baba", "baba");
    insert(trie, "abba", "abba");
    assertTrue(contains(trie, "abba"));
    assertTrue(contains(trie, "abab"));
    assertTrue(contains(trie, "baba"));


    trie = new TokenRadixTreeImpl<String, String>();
    insert(trie, "abab", "abab");
    insert(trie, "abba", "abba");
    insert(trie, "baba", "baba");
    assertTrue(contains(trie, "abba"));
    assertTrue(contains(trie, "abab"));
    assertTrue(contains(trie, "baba"));
  }        */

  public void testSearchPrefix() {
    TokenRadixTreeImpl<String, String> trie = new TokenRadixTreeImpl<String, String>();

    insert(trie, "big apple");
    insert(trie, "big apple shack");
    insert(trie, "big apple shack cream");
    insert(trie, "big apple pie");
    insert(trie, "big core");

    List<String> expected = new ArrayList<String>();
    expected.add("big apple shack");
    expected.add("big apple shack cream");

    List<String> result = searchPrefix(trie, "big apple shack", 3);
    assertTrue(expected.size() == result.size());
    Set<String> resultSet = new HashSet<String>(result);
    for (String key : expected) {
      assertTrue(resultSet.contains(key));
    }

    expected.add("big apple");
    expected.add("big apple pie");

    result = searchPrefix(trie, "big apple", 10);
    assertTrue(expected.size() == result.size());
    resultSet = new HashSet<String>(result);
    for (String key : expected) {
      assertTrue(resultSet.contains(key));
    }
  }

  public void testGetSize() {
    TokenRadixTreeImpl<String, String> trie = new TokenRadixTreeImpl<String, String>();

    insert(trie, "apple");
    insert(trie, "apple shack");
    insert(trie, "apple shack cream");
    insert(trie, "apple pie");
    insert(trie, "ape");

    assertTrue(trie.getSize() == 5);

    remove(trie, "apple shack");

    assertTrue(trie.getSize() == 4);
  }

  private static void insert(TokenRadixTreeImpl<String, String> trie, String sentence) {
    trie.insert(makeTokenList(sentence), sentence);
  }

  private static void findAndVerify(TokenRadixTreeImpl<String, String> trie, String sentence) {
    assertEquals(trie.find(makeTokenList(sentence)), sentence);
  }

  private static boolean contains(TokenRadixTreeImpl<String, String> trie, String sentence) {
    return trie.contains(makeTokenList(sentence));
  }

  private static List<String> searchPrefix(TokenRadixTreeImpl<String, String> trie, String sentence, int recordLimit) {
    return trie.searchPrefix(makeTokenList(sentence), recordLimit);
  }

  private static boolean remove(TokenRadixTreeImpl<String, String> trie, String sentence) {
    return trie.delete(makeTokenList(sentence));
  }

  private static List<String> makeTokenList(String sentence) {
    LinkedList<String> result = new LinkedList<String>();

    for (String token : sentence.split("\\s+")) {
      result.add(token);
    }

    return result;
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestTokenRadixTreeImpl.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
