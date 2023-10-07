package com.daw.pms.Utils;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Util for handle registration code.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 10/6/23
 */
@Component
public class RegistrationCodeUtil {
  /** Character base for registration code. */
  private final char[] charBase =
      new char[] {
        '8', 'W', '6', 'S', 'X', '7', 'K', 'M', '4', '5', 'J', '3', 'D', 'Y', 'G', '9', '2', 'V',
        'N', 'H', 'P', 'R', 'L', 'F', 'U', 'T', 'E', 'Z', 'C', 'Q', 'B'
      };

  /** Character for code complement. */
  private final char complementCharacter = 'A';

  /** The length of binary. */
  private final int binLength = charBase.length;

  /** The length of registration code. */
  private final int codeLength = 6;

  /**
   * Generate registration code by user id in pms.
   *
   * @param id User id in pms.
   * @return Registration code.
   */
  public String getRegistrationCodeByUserId(long id) {
    char[] buf = new char[32];
    int charPos = 32;

    while ((id / binLength) > 0) {
      int ind = (int) (id % binLength);
      buf[--charPos] = charBase[ind];
      id /= binLength;
    }
    buf[--charPos] = charBase[(int) (id % binLength)];
    String str = new String(buf, charPos, (32 - charPos));
    // Complete the code if necessary.
    if (str.length() < codeLength) {
      StringBuilder sb = new StringBuilder();
      sb.append(complementCharacter);
      Random rnd = new Random();
      for (int i = 1; i < codeLength - str.length(); i++) {
        sb.append(charBase[rnd.nextInt(binLength)]);
      }
      str += sb.toString();
    }
    return str;
  }

  /**
   * Get user id by registration code.
   *
   * @param code Registration code.
   * @return user id in pms.
   */
  public long getUserIdByRegistrationCode(String code) {
    char[] chs = code.toCharArray();
    long res = 0L;
    for (int i = 0; i < chs.length; i++) {
      int ind = 0;
      for (int j = 0; j < binLength; j++) {
        if (chs[i] == charBase[j]) {
          ind = j;
          break;
        }
      }
      if (chs[i] == complementCharacter) {
        break;
      }
      if (i > 0) {
        res = res * binLength + ind;
      } else {
        res = ind;
      }
    }
    return res;
  }
}
