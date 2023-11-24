package com.mit.util;

public class RandomUtils {

  public static double getRandomTime(double min, double max) {
    double perc = Math.random();
    double dif = max - min;
    double add = perc * dif;
    return min + add;
  }
}
