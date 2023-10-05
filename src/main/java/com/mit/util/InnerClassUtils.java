package com.mit.util;

import java.util.Arrays;
import java.util.List;

public class InnerClassUtils {

  public static List<Object> convertFromSetToList(Object[] objects) {
    return Arrays.asList(Arrays.stream(objects).toArray());
  }
}
