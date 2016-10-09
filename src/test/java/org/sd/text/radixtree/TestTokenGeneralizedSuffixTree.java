package org.sd.text.radixtree;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

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

  private final void doCommonSubstrsTest(String[] strings, int minLen,
    int minParticipants, Map<String, BitSet> expectedResults) {
    List<List<String>> tokensList = new ArrayList<List<String>>(strings.length);

    for (String s : strings) {
      tokensList.add(makeTokenList(s));
    }

    final TokenGeneralizedSuffixTree<String> gst = new TokenGeneralizedSuffixTree<String>(
     tokensList, StringEosStrategy.INSTANCE);

    final Map<List<String>, BitSet> result = gst.commonSubsequences(minLen,
      minParticipants);

    assertEquals(expectedResults.size(), result.size());

    for (Map.Entry<String, BitSet> expectedResult : expectedResults.entrySet()) {
      List<String> expectedTokens = makeTokenList(expectedResult.getKey());

      BitSet bs = result.get(expectedTokens);

      assertNotNull("Missing bitset for " + expectedResult.getKey());
      assertEquals(bs, expectedResult.getValue());

      System.out.println("Success: found '" + expectedResult.getKey() + "'");
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

  public void testCommonSubsequences() {
    Map<String, BitSet> expected = new HashMap<String, BitSet>();
    BitSet bitSet = new BitSet();
    bitSet.set(0);
    bitSet.set(1);

    expected.put("This thing won all the awards", bitSet);

    String[] input = new String[] {
      "Good stuff This thing won all the awards Really just kidding",
      "Other junk This thing won all the awards just kidding",
      "jk stands for just kidding actually"
    };

    doCommonSubstrsTest(input, 6, 2, expected);

    BitSet updated = (BitSet) bitSet.clone();
    updated.set(2);
    expected.put("just kidding", updated);
    doCommonSubstrsTest(input, 2, 2, expected);
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
