package org.sd.text.radixtree;

public class StringEosStrategy implements EosStrategy<String> {
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

  public static final StringEosStrategy INSTANCE = new StringEosStrategy();
}
