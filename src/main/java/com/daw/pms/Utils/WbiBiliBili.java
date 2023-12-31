package com.daw.pms.Utils;

import cn.hutool.crypto.SecureUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Util for deal with wbi in bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class WbiBiliBili {
  private static final int[] mixinKeyEncTab =
      new int[] {
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19,
        29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4,
        22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52
      };

  private static String getMixinKey(String imgKey, String subKey) {
    String s = imgKey + subKey;
    StringBuilder key = new StringBuilder();
    for (int i = 0; i < 32; i++) {
      key.append(s.charAt(mixinKeyEncTab[i]));
    }
    return key.toString();
  }

  /**
   * Sign the request for wbi.
   *
   * @param params Original request params.
   * @param imgKey Img key.
   * @param subKey Sub key.
   * @return Signed request params with wbi.
   */
  public static String wbiSignRequestParam(
      Map<String, Object> params, String imgKey, String subKey) {
    String mixinKey = getMixinKey(imgKey, subKey);
    LinkedHashMap<String, Object> map = new LinkedHashMap<>(params);
    map.put("wts", System.currentTimeMillis() / 1000);
    StringJoiner param = new StringJoiner("&");
    // Sort and join the params.
    map.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(entry -> param.add(entry.getKey() + "=" + entry.getValue().toString()));
    String s = param + mixinKey;
    String wbiSign = SecureUtil.md5(s);
    return param + "&w_rid=" + wbiSign;
  }
}
