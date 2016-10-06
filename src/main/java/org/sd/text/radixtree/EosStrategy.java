package org.sd.text.radixtree;

public interface EosStrategy<S> {
  public S createEos(int index);
  public boolean isEos(S token);
}
