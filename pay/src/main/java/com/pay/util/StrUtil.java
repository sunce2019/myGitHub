package com.pay.util;

public class StrUtil {

  public static String strnull(Object object) {
    if (object == null) {
      return "";
    }
    return object.toString();
  }

  public static boolean isEmpty(Object object) {
    if (object == null || "".equals(object)) {
      return true;
    }
    return false;
  }
}
