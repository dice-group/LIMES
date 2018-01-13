package org.aksw.limes.core.util;

public class Timer {

  private long originalStartTime;
  private long lastCheckTime;

  public Timer() {
    originalStartTime = System.nanoTime();
    lastCheckTime = originalStartTime;
  }

  public double checkElapsedSecondsSinceLastCheck() {
    long tmp = lastCheckTime;
    lastCheckTime = System.nanoTime();
    return (lastCheckTime - tmp) * 1.0e-9;
  }

  public double totalElapsedSecondsSinceBeginning() {
    lastCheckTime = System.nanoTime();
    return (lastCheckTime - originalStartTime) * 1.0e-9;
  }

}
