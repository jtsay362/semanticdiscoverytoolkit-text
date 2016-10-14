package org.sd.text.radixtree;

import java.util.List;

public class StringTokenStrategy implements TokenStrategy<String> {
  protected StringTokenStrategy() {}

  @Override
  public boolean isValidSuffix(List<String> sequence) {
    return !sequence.isEmpty();
  }

  @Override
  public List<String> longestValidSubsequence(List<String> sequence) {
    return sequence;
  }

  @Override
  public String createEos(int index) {
    if (index < 0) {
      return EOS_MARKER;
    } else {
      return EOS_MARKER + index;
    }

  }

  @Override
  public boolean isEos(String token) {
    // TODO: could check number afterwards
    return token.startsWith(EOS_MARKER);
  }

  private static final String EOS_MARKER = "[EOS]";

  public static final StringTokenStrategy INSTANCE = new StringTokenStrategy();
}
