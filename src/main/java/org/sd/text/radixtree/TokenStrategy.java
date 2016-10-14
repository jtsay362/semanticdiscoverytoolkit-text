package org.sd.text.radixtree;

import java.util.List;

public interface TokenStrategy<S> {
  public boolean isValidSuffix(List<S> sequence);
  public List<S> longestValidSubsequence(List<S> sequence);
  public S createEos(int index);
  public boolean isEos(S token);
}
