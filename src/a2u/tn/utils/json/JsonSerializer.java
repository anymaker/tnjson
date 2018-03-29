package a2u.tn.utils.json;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * MAP to JSON converter
 * Every value in the Map must be
 *   either a simple value (string or number or boolean),
 *   either by a Map - a nested object (Map<CharSequence, Object>),
 *   either an array of values (Collection<Object>)
 *
 */
public class JsonSerializer {

  /**
   * JSON generation mode
   * @see #toJson(Map, Mode)
   */
  public enum Mode {
    /**
     * Will be generated compact json-string, where any non-digital and non-letter character will be replaced with sequence \\uXXXX.<br>
     * This mode is default, because it has max compatibility with other clients.
     */
    HARD,

    /**
     * Will be generated compact json-string, where non-digital and non-letter character will be stay in readable format, if it possible.<bR>
     * This format is more compact, but is not all client can parse it.
     */
    LIGHT,

    /**
     * Will be generated json-string in pretty read format, where non-digital and non-letter character will be stay in readable format, if it possible.<bR>
     *
     */
    FORMATTED,

    /**
     * Will be generated json-string in max human readable format json5.<br>
     * See detail about json5 on https://json5.org/
     */
    JSON5
  }


  /**
   * Convert Map to JSON.<br>
   * Will be generated compact json-string, where any non-digital and non-letter character will be replaced with sequence \\uXXXX.
   * @param data Map vith data to convert
   * @return JSON-string
   */
  public static String toJson(Map<? extends CharSequence, ?> data) {
    StringBuilder b = new StringBuilder();
    addMap(data, b, Mode.HARD, 0);
    String json = b.toString();
    return json;
  }

  /**
   * Convert Map to JSON with specify output string format
   * @param data data Map vith data to convert
   * @param mode affects the format of resulting string
   * @return JSON-string
   * @see Mode
   */
  public static String toJson(Map<? extends CharSequence, ?> data, Mode mode) {
    StringBuilder b = new StringBuilder();
    addMap(data, b, mode, 0);
    String json = b.toString();
    return json;
  }



  private static void addMap(Map<? extends CharSequence, ?> map, StringBuilder b, Mode mode, int level) {
    int valuelevel = level + 1;

    b.append("{");
    endLine(b, mode);

    boolean hasEntry = false;
    for(Map.Entry<? extends CharSequence, ?> entry : map.entrySet()) {
      CharSequence key = entry.getKey();
      key = codeKey(key, mode);
      Object value = entry.getValue();

      if (hasEntry) {
        b.append(",");
        endLine(b, mode);
      }
      else {
        hasEntry = true;
      }

      startLine(b, mode, valuelevel);
      b.append(key);

      b.append(":");
      if (mode == Mode.FORMATTED || mode == Mode.JSON5) {
        b.append(" ");
      }

      addValue(value, b, mode, valuelevel);
    }

    endLine(b, mode);
    startLine(b, mode, level);
    b.append("}");

  }

  private static String codeKey(CharSequence key, Mode mode) {
    StringBuilder b = new StringBuilder();
    int len = key.length();
    boolean validIdenty = true;
    for (int i=0; i < len; i++) {
      char c = key.charAt(i);
      if (Character.isLetterOrDigit(c)) {
        b.append(c);
      }
      else {
        b.append(unicodeEscaped(c));
        validIdenty = false;
      }


    }

    if (mode == Mode.JSON5 && validIdenty && Character.isLetter(key.charAt(0))) {
      return b.toString();
    }
    else {
      return "\"" + b.toString() + "\"";
    }
  }

  private static void addList(Collection list, StringBuilder b, Mode mode, int level) {
    int itemlevel = level + 1;

    b.append("[");

    endLine(b, mode);
    startLine(b, mode, itemlevel);

    boolean hasEntry = false;
    for (Object value : list) {

      if (hasEntry) {
        b.append(",");
        endLine(b, mode);
        startLine(b, mode, itemlevel);
      }
      else {
        hasEntry = true;
      }

      addValue(value, b, mode, itemlevel);
    }
    endLine(b, mode);
    startLine(b, mode, level);
    b.append("]");

  }

  private static void addValue(Object value, StringBuilder b, Mode mode, int level) {

    if (value == null) {
      b.append("null");
    }
    else if (value instanceof String) {
      addString((String) value, b, mode);
    }
    else if (value instanceof Byte) {
      addNum(value, b);
    }
    else if (value instanceof Integer) {
      addNum(value, b);
    }
    else if (value instanceof BigInteger) {
      addNum(value, b);
    }
    else if (value instanceof Long) {
      addNum(value, b);
    }
    else if (value instanceof Float) {
      addNum(value, b);
    }
    else if (value instanceof Double) {
      addNum(value, b);
    }
    else if (value instanceof Boolean) {
      boolean v = (Boolean) value;
      b.append(v ? "true" : "false");
    }
    else if (value instanceof Date) {
      long v = ((Date)value).getTime();
      b.append(v);
    }
    else if (value instanceof Map) {
      addMap((Map<? extends CharSequence, ?>) value, b, mode, level);
    }
    else if (value instanceof Collection) {
      addList((Collection) value, b, mode, level);
    }

    else {
      String v = String.valueOf(value);
      addString(v, b, mode);
    }

  }

  private static void addString(String str, StringBuilder b, Mode mode) {
    b.append('"');
    int len = str.length();
    for(int p=0; p<len; p++) {
      char c = str.charAt(p);
      if (Character.isLetterOrDigit(c)) {
        b.append(c);
      }
      else if (mode == Mode.HARD) {
        b.append(unicodeEscaped(c));
      }
      else if (mode == Mode.LIGHT || mode == Mode.FORMATTED) {
        b.append(charToReadable(c));
      }
      else if (mode == Mode.JSON5) {
        switch (c) {
          case '\'': b.append("'"); break;
          case '\n': b.append("/\n"); break;
          default: b.append(charToReadable(c)); break;
        }
      }
      else {
        b.append(unicodeEscaped(c));
      }
    }
    b.append('"');
  }

  private static void addNum(Object num, StringBuilder b) {
    String v  = String.valueOf(num);
    b.append(v);
  }


  private static String charToReadable(char c) {
    switch (c) {
      case ' ': return String.valueOf(c);
      case '`': return String.valueOf(c);
      case '~': return String.valueOf(c);
      case '!': return String.valueOf(c);
      case '@': return String.valueOf(c);
      case '#': return String.valueOf(c);
      case '$': return String.valueOf(c);
      case '%': return String.valueOf(c);
      case '^': return String.valueOf(c);
      case '&': return String.valueOf(c);
      case '*': return String.valueOf(c);
      case '(': return String.valueOf(c);
      case ')': return String.valueOf(c);
      case '-': return String.valueOf(c);
      case '_': return String.valueOf(c);
      case '=': return String.valueOf(c);
      case '+': return String.valueOf(c);
      case '[': return String.valueOf(c);
      case ']': return String.valueOf(c);
      case '{': return String.valueOf(c);
      case '}': return String.valueOf(c);
      case ';': return String.valueOf(c);
      case ':': return String.valueOf(c);
      case '"': return "\\\"";
      case '\'': return "\\'";
      case '\\': return "\\\\";
      case '|': return String.valueOf(c);
      case '/': return "\\/";
      case ',': return String.valueOf(c);
      case '.': return String.valueOf(c);
      case '?': return String.valueOf(c);
      case '<': return String.valueOf(c);
      case '>': return String.valueOf(c);
      case '\b': return "\\b";
      case '\f': return "\\f";
      case '\n': return "\\n";
      case '\r': return "\\r";
      case '\t': return "\\t";
      default: return unicodeEscaped(c);
    }
  }
  private static String unicodeEscaped(char ch) {
    if (ch < 0x10) {
      return "\\u000" + Integer.toHexString(ch);
    }
    else if (ch < 0x100) {
      return "\\u00" + Integer.toHexString(ch);
    }
    else if (ch < 0x1000) {
      return "\\u0" + Integer.toHexString(ch);
    }
    return "\\u" + Integer.toHexString(ch);
  }

  private static void endLine(StringBuilder b, Mode mode) {
    if (mode == Mode.FORMATTED || mode == Mode.JSON5) {
      b.append("\n");
    }
  }
  private static void startLine(StringBuilder b, Mode mode, int level) {
    if (mode != Mode.FORMATTED && mode != Mode.JSON5) {
      return;
    }

    for(int i=0; i<level; i++) {
      b.append("  ");
    }
  }



}
