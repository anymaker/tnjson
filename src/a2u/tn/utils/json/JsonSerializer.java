package a2u.tn.utils.json;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * MAP to JSON converter.<br>
 * Every value in the Map must be
 * <ul>
 *   <li>either a simple value: string, number, boolean, or other;</li>
 *   <li>either by a Map such as Map&lt;String, Object&gt;;</li>
 *   <li>either an array of values such as Collection&lt;Object&gt;.</li>
 * </ul>
 * <br>
 * Of course, you can use for json-object any class inheritor of the java.util.Map interface.<br>
 * For json-array you can use any class inheritor of the java.util.Collection interface.
 */
public class JsonSerializer {

  /**
   * json generator tuning
   */
  public static class Settings {
    private boolean isKeyQuoted = true;
    private char quoteSymbol = '"';
    private boolean isFormated = false;
    private boolean isStayReadable = false;
    private boolean isAllowMultiRowString = false;
    private boolean isKeepNull = false;

    /**
     * Create new Settings
     * @return new object with default settings
     */
    public static Settings init() {
      return new Settings();
    }

    /**
     * Disable quotation generation for the key
     */
    public Settings withoutKeyQuote() {
      isKeyQuoted = false;
      return this;
    }

    /**
     * Use single quotes
     */
    public Settings singleQuote() {
      quoteSymbol = '\'';
      return this;
    }

    /**
     * Format the final json
     */
    public Settings formated() {
      isFormated = true;
      return this;
    }

    /**
     * Leave characters in a strings as readable as possible
     */
    public Settings readable() {
      isStayReadable = true;
      return this;
    }

    /**
     * Allow linefeed in a strings
     */
    public Settings allowMultiRowString() {
      isAllowMultiRowString = true;
      return this;
    }

    /**
     * Allow null values
     */
    public Settings keepNull() {
      isKeepNull = true;
      return this;
    }
    public String serialize(Map data) {
      return toJson(data, this);
    }
  }

  /**
   * JSON generation mode
   * @see #toJson(Map, Mode)
   */
  public enum Mode {
    /**
     * Will be generated compact json-string, where any non-digital and non-letter character in string will be replaced with sequence uXXXX.<br>
     * This mode is default, because it has max compatibility with other clients.
     */
    HARD,

    /**
     * Will be generated compact json-string, where non-digital and non-letter character in string will be stay in readable format, if it possible.<bR>
     * This format is more compact, but is not all client can parse it.
     */
    LIGHT,

    /**
     * Will be generated json-string in pretty read format, where non-digital and non-letter character in string will be stay in readable format, if it possible.<bR>
     *
     */
    FORMATTED,

    /**
     * Will be generated json-string in max human readable format json5.<br>
     * See detail about json5 on https://json5.org/
     */
    JSON5,

    /**
     * JSON5 like, but without linefeed
     */
    JSON5COMPACT
  }



  private static final Settings SET_HARD         = Settings.init();
  private static final Settings SET_LIGHT        = Settings.init().readable();
  private static final Settings SET_FORMATTED    = Settings.init().readable().formated();
  private static final Settings SET_JSON5        = Settings.init().readable().formated().withoutKeyQuote().allowMultiRowString();
  private static final Settings SET_JSON5COMPACT = Settings.init().readable().withoutKeyQuote().singleQuote();

  private JsonSerializer() {
    //hide this
  }

  /**
   * Convert Map to JSON.<br>
   * Will be generated compact json-string, where any non-digital and non-letter character in string will be replaced with sequence uXXXX.
   * @param data Map vith data to convert
   * @return JSON-string
   */
  public static String toJson(Map data) {
    return SET_HARD.serialize(data);
  }

  /**
   * Convert Map to JSON with specify output string format
   * @param data data Map vith data to convert
   * @param mode affects the format of resulting string
   * @return JSON-string
   * @see Mode
   */
  public static String toJson(Map data, Mode mode) {
    switch (mode) {
      case HARD:         return SET_HARD.serialize(data);
      case LIGHT:        return SET_LIGHT.serialize(data);
      case FORMATTED:    return SET_FORMATTED.serialize(data);
      case JSON5:        return SET_JSON5.serialize(data);
      case JSON5COMPACT: return SET_JSON5COMPACT.serialize(data);
      default:           return SET_HARD.serialize(data);
    }
  }

  /**
   * Convert Map to JSON with settings
   * @param data data Map vith data to convert
   * @param settings json generator tuning
   * @return JSON-string
   * @see Settings
   */
  public static String toJson(Map data, Settings settings) {
    StringBuilder b = new StringBuilder();
    addMap(data, b, settings, 0);
    String json = b.toString();
    return json;
  }


  private static void addMap(Map map, StringBuilder b, Settings settings, int level) {
    int valuelevel = level + 1;

    b.append("{");
    endLine(b, settings);

    boolean hasEntry = false;
    for(Object keyObj : map.keySet()) {
      String key = String.valueOf(keyObj);
      Object value = map.get(keyObj);

      if (value == null && !settings.isKeepNull) {
        continue;
      }

      key = codeKey(key, settings);

      if (hasEntry) {
        b.append(",");
        endLine(b, settings);
      }
      else {
        hasEntry = true;
      }

      startLine(b, settings, valuelevel);
      b.append(key);

      b.append(":");
      if (settings.isFormated) {
        b.append(" ");
      }

      addValue(value, b, settings, valuelevel);
    }

    endLine(b, settings);
    startLine(b, settings, level);
    b.append("}");

  }

  private static String codeKey(CharSequence key, Settings settings) {
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

    if (!settings.isKeyQuoted && validIdenty && Character.isLetter(key.charAt(0))) {
      return b.toString();
    }
    else {
      return settings.quoteSymbol + b.toString() + settings.quoteSymbol;
    }
  }

  private static void addList(Collection list, StringBuilder b, Settings settings, int level) {
    int itemlevel = level + 1;

    b.append("[");

    endLine(b, settings);
    startLine(b, settings, itemlevel);

    boolean hasEntry = false;
    for (Object value : list) {

      if (hasEntry) {
        b.append(",");
        endLine(b, settings);
        startLine(b, settings, itemlevel);
      }
      else {
        hasEntry = true;
      }

      addValue(value, b, settings, itemlevel);
    }
    endLine(b, settings);
    startLine(b, settings, level);
    b.append("]");

  }

  private static void addValue(Object value, StringBuilder b, Settings settings, int level) {

    if (value == null) {
      b.append("null");
    }
    else if (value instanceof String) {
      addString((String) value, b, settings);
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
      addMap((Map) value, b, settings, level);
    }
    else if (value instanceof Collection) {
      addList((Collection) value, b, settings, level);
    }

    else {
      String v = String.valueOf(value);
      addString(v, b, settings);
    }

  }

  private static void addString(String str, StringBuilder b, Settings settings) {
    if (str == null) {
      b.append("null");
    }
    else {
      b.append(settings.quoteSymbol);

      int len = str.length();
      for (int p = 0; p < len; p++) {
        char c = str.charAt(p);
        if (Character.isLetterOrDigit(c)) {
          b.append(c);
        }
        else if (c == '\n') {
          if (settings.isAllowMultiRowString) {
            b.append("/\n");
          }
          else if (settings.isStayReadable) {
            b.append(charToReadable(c));
          }
          else {
            b.append(unicodeEscaped(c));
          }
        }
        else if (c == settings.quoteSymbol) {
          if (settings.isStayReadable) {
            b.append(charToReadable(c));
          }
          else {
            b.append(unicodeEscaped(c));
          }
        }
        else {
          if (settings.isStayReadable) {
            if (c == '\'' || c == '"') {
              b.append(c);
            }
            else {
              b.append(charToReadable(c));
            }
          }
          else {
            b.append(unicodeEscaped(c));
          }
        }
      }

      b.append(settings.quoteSymbol);
    }
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

  private static void endLine(StringBuilder b, Settings settin) {
    if (settin.isFormated) {
      b.append("\n");
    }
  }
  private static void startLine(StringBuilder b, Settings settin, int level) {
    if (!settin.isFormated) {
      return;
    }

    for(int i=0; i<level; i++) {
      b.append("  ");
    }
  }



}
