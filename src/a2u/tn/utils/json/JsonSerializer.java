package a2u.tn.utils.json;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MAP to JSON converter
 * Every value in the Map must be
 *   either a simple value (string or number or boolean),
 *   either by a Map - a nested object (Map<String, Object>),
 *   either an array of values (List<Object>)
 *
 */
public class JsonSerializer {

  /**
   * Convert Map to JSON
   * @param data Map vith data to convert
   * @return JSON-string
   */
  public static String toJson(Map<String, ?> data) {
    StringBuilder b = new StringBuilder();
    addMap(data, b);
    String json = b.toString();
    return json;
  }



  private static void addMap(Map<String, ?> map, StringBuilder b) {
    b.append("{");
    boolean hasEntry = false;
    for(Map.Entry<String, ?> entry : map.entrySet())
    {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (hasEntry) {
        b.append(",");
      } else {
        hasEntry = true;
      }

      b.append("\"").append(key).append("\":");
      addValue(value, b);
    }
    b.append("}");
  }

  private static void addList(List<Object> list, StringBuilder b) {
    b.append("[");
    boolean hasEntry = false;
    for (Object value : list)
    {
      if (hasEntry) {
        b.append(",");
      } else {
        hasEntry = true;
      }

      addValue(value, b);
    }
    b.append("]");
  }

  private static void addValue(Object value, StringBuilder b) {

    if (value == null) {
      b.append("null");
    }
    else if (value instanceof String) {
      addString((String) value, b);
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
      addNum(value, b);
    }
    else if (value instanceof Map) {
      addMap((Map<String, ?>) value, b);
    }
    else if (value instanceof List) {
      addList((List<Object>) value, b);
    }

    else {
      String v = String.valueOf(value);
      addString(v, b);
    }
  }

  private static void addString(String str, StringBuilder b) {
    b.append('"');
    int len = str.length();
    for(int p=0; p<len; p++) {
      char c = str.charAt(p);
      if (Character.isLetterOrDigit(c)) {
        b.append(c);
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


}
