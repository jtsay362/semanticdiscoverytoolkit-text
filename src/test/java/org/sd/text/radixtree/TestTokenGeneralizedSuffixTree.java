package org.sd.text.radixtree;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TestTokenGeneralizedSuffixTree extends TestCase {

  public TestTokenGeneralizedSuffixTree(String name) {
    super(name);
  }

  private final void doLongestSubstrsTest(String[] strings, int minLen, String[] expectedResults) {
    List<List<String>> tokensList = new ArrayList<List<String>>(strings.length);

    for (String s : strings) {
      tokensList.add(makeTokenList(s));
    }

    final TokenGeneralizedSuffixTree<String> gst = new TokenGeneralizedSuffixTree<String>(
     tokensList, StringEosStrategy.INSTANCE);
    final Set<List<String>> result = gst.longestSubsequence(minLen);

    assertEquals("got=" + result, expectedResults.length, result.size());
    for (String expectedResult : expectedResults) {
      List<String> expectedTokens = makeTokenList(expectedResult);
      assertTrue("expected=" + expectedResult, result.contains(expectedTokens));
    }
  }

  public void testLongestSubsequences() {
    doLongestSubstrsTest(new String[] {
      "Good stuff This thing won all the awards Really",
      "Other junk This thing won all the awards Just kidding",
    }, 6, new String[] {
      "This thing won all the awards"
    });
  }

  private static List<String> makeTokenList(String sentence) {
    LinkedList<String> result = new LinkedList<String>();

    for (String token : sentence.split("\\s+")) {
      result.add(token);
    }

    return result;
  }

  private static String joinTokens(List<String> tokens) {
    final StringBuilder sb = new StringBuilder();
    boolean isFirstToken = true;

    for (String token : tokens) {
      if (isFirstToken) {
        isFirstToken = false;
      } else {
        sb.append(' ');
      }
      sb.append(token);
    }

    return sb.toString();
  }


  public static Test suite() {
    TestSuite suite = new TestSuite(TestTokenGeneralizedSuffixTree.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
