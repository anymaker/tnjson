package a2u.tn.utils.json;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parsing JSON string to Map object with JSON5
 * JSON for Humans https://spec.json5.org
 *
 */
@SuppressWarnings("Convert2Diamond")
public class JsonParser {

  public static final String DEFAULT_LIST_KEY = "list";

  private static final String NULL = "null";
  private static final String BOOL_TRUE = "true";
  private static final String BOOL_FALSE = "false";
  private static final String NUM_INFINITY = "infinity";
  private static final String NUM_INFINITY_PSITIVE = "+infinity";
  private static final String NUM_INFINITY_NEGATIVE = "-infinity";
  private static final String NUM_NAN = "nan";

  private static final char LF = 0x0A;
  private static final char CR = 0x0D;
  private static final char LS = 0x2028;
  private static final char PS = 0x2029;


  private Map<String, Object> resultMap;
  private String content;
  private int maxLength;
  private int index;


  private JsonParser() {
    //hide this
  }



  /**
   * Parsing JSON-string to Map
   * Every value in the Map can be
   *   either a simple value (string or number or boolean),
   *   either by a Map - a nested object (Map<String, Object>),
   *   either an array of values (List<Object>)
   *
   * If JSON contain array, then will return Map object with element by key-name DEFAULT_LIST_KEY, which contain list
   *
   * @param data JSON-string
   * @return Map with data
   */
  public static Map<String, Object> parse(String data) {
    JsonParser p = new JsonParser();
    return p.doParse(data);
  }

  private Map<String, Object> doParse(String data) {
    content = data.trim();
    maxLength = content.length();
    index = 0;
    resultMap = new LinkedHashMap<String, Object>();

    parseEmpty();

    return resultMap;
  }



  private void parseEmpty() {
    while (index < maxLength) {
      char c = getTokenBegin();
      if (c == '{') {
        index++;
        resultMap = parseMap();
        return;
      }
      else if (c == '[') {
        index++;
        List<Object> list = parseList();
        resultMap.put(DEFAULT_LIST_KEY, list);
      }

      index++;
    }
  }

  private Map<String, Object> parseMap() {
    Map<String, Object> map = new LinkedHashMap<String, Object>();

    while (index < maxLength) {
      char c = getTokenBegin(); //to begin

      if (c == '}') {
        index++;
        return map;
      }

      String key = getIdenty();

      c = getTokenBegin();
      if (c != ':') {
        throw new ParseException("Invalid character '" + charToLog(c) + "' at position " + index + ", expected ':'.");
      }
      index++;

      Object val = getValue();
      map.put(key, val);

      c = getTokenBegin();
      if (c == '}') {
        index++;
        return map;
      }
      if (c == ',') {
        index++;
        continue;
      }

      throw new ParseException("Invalid character '" + charToLog(c) + "' at position " + index + ", expected ',' or '}'.");
    }

    return map;
  }

  private List<Object> parseList() {
    List<Object> list = new ArrayList<Object>();

    while (index < maxLength) {
      char c = getTokenBegin();

      if (c == ']') {
        index++;
        return list;
      }

      if (c == ',') {
        index++;
        continue;
      }

      Object val = getValue();
      list.add(val);

    }

    return list;
  }



  private char getTokenBegin() {
    while (index < maxLength) {
      char c = content.charAt(index);
      if (Character.isLetterOrDigit(c) || c == '"' || c == '\'' || c == '@' || c == '#' || c == '$' || c == '_' || c == '{' || c == '}' || c == ':' || c == '[' || c == ']' || c == ',' || c == '+' || c == '-' || c == '.') {
        return c;
      }

      if (c == '\\') {
        return c;
      }

      if (c == '/') {
        char next = content.charAt(index + 1);
        if (next == '/') {
          skipToEndLine();
        }
        else if (next == '*') {
          index += 2;
          skipToEndComent();
        }
      }

      index++;
    }
    return 0;
  }

  private void skipToEndLine() {
    while (index < maxLength) {
      char c = content.charAt(index);
      if (isLineTerminator(c)) {
        return;
      }
      index++;
    }
  }

  private void skipToEndComent() {
    while (index < maxLength) {
      char c = content.charAt(index);
      if (c == '*') {
        index++;
        c = content.charAt(index);
        if (c == '/') {
          return;
        }
      }
      index++;
    }
  }

  private boolean isLineTerminator(char c) {
    return c == LF || c == CR || c == LS || c == PS;
  }



  private String getIdenty() {
    char c = content.charAt(index);
    char terminator = (c == '"' || c == '\'') ? c : 0;

    StringBuilder b = new StringBuilder();
    while (index < maxLength) {
      c = content.charAt(index);
      if (c == terminator) {
        index++;
        if (b.length() == 0) {
          continue;
        }
        return b.toString().trim();
      }
      if (c == ':' || c == '/') {
        return b.toString().trim();
      }

      if (c == '\\') {
        char ce = getCharFromEscapedText();
        b.append(ce);
      }
      else {
        b.append(c);
      }
      index++;
    }

    return b.toString().trim();
  }



  private Object getValue() {
    char c = getTokenBegin();

    if (c == '{') {
      index++;
      Map<String, Object> map = parseMap();
      return map;
    }
    if (c == '[') {
      index++;
      List<Object> list = parseList();
      return list;
    }
    if (c == '"' || c == '\'') {
      String str = getString();
      return str;
    }
    else {
      Object num = getLiteral();
      return num;
    }


    //throw new ParseException("Invalid character '" + c + "' at position " + index + ".");
  }



  private Object getLiteral() {
    StringBuilder b = new StringBuilder();
    while (index < maxLength) {
      char c = content.charAt(index);
      if (Character.isLetterOrDigit(c) || c == '.' || c == '+' || c == '-') {
        b.append(c);
        index++;
      }
      else if ((c == '+' || c == '-') && b.length() == 0 ) {
        b.append(c);
        index++;
      }
      else {
        break;
      }
    }

    String literal = b.toString().trim().toLowerCase();

    if (literal.equals(NULL)) {
      return null;
    }

    else if (literal.equals(BOOL_TRUE)) {
      return true;
    }
    else if (literal.equals(BOOL_FALSE)) {
      return false;
    }

    else if (literal.equals(NUM_INFINITY)) {
      return Double.POSITIVE_INFINITY;
    }
    else if (literal.equals(NUM_INFINITY_PSITIVE)) {
      return Double.POSITIVE_INFINITY;
    }
    else if (literal.equals(NUM_INFINITY_NEGATIVE)) {
      return Double.NEGATIVE_INFINITY;
    }

    else if (literal.equals(NUM_NAN)) {
      return Double.NaN;
    }

    try {
      return detectNumber(literal);
    }
    catch (Exception ex) {
      throw new ParseException("Invalid literal '" + literal + "' at position " + index + ".");
    }

  }

  private Object detectNumber(String literal) {
    boolean hasDot = literal.indexOf('.') >= 0;
    boolean hasE = literal.indexOf('e') >= 0;
    boolean hasX = literal.indexOf('x') >= 0;

    if (hasDot || (hasE && ! hasX)) {
      return Double.parseDouble(literal);
    }

    // Integer.MAX_VALUE dec == 2147483647 - 10 characters
    // Integer.MAX_VALUE hex == 0x7fffffff - 8 characters + 2 (0x) = 10 characters
    // Long.MAX_VALUE dec == 9223372036854775807 - 19 characters
    // Long.MAX_VALUE hex == 0x7fffffffffffffff - 16 characters + 2 (0x) = 18 characters
    //
    // therefore
    // for maxintlen == 9 or 9 characters dec / hex
    // for maxlonglen == 18 or 17 characters dec / hex
    int maxintlen = hasX ? 9 : 9;
    int maxlonglen = hasX ? 17 : 18;
    if (literal.charAt(0) == '-') {
      maxintlen++;
      maxlonglen++;
    }
    if (literal.length() <= maxintlen) {
      return Integer.decode(literal);
    }
    if (literal.length() <= maxlonglen) {
      return Long.decode(literal);
    }
    else {
      return new BigInteger(literal);
    }
  }



  private String getString() {
    char terminator = content.charAt(index);
    index++;

    StringBuilder b = new StringBuilder();
    while (index < maxLength) {
      char c = content.charAt(index);
      if (c == '\\') {
        char ce = getCharFromEscapedText();
        b.append(ce);
        index++;
      }
      else if (c == terminator) {
        index++;
        break;
      }
      else {
        b.append(c);
        index++;
      }
    }

    return b.toString().trim();
  }


  /**
   * Extract escaped sequences, and convert ot char
   * https://spec.json5.org/#escapes
   *
   * As result index will refer to the last accepted character
   *
   * @return matching character
   */
  private char getCharFromEscapedText() {
    char resultChar;
    index++;
    char next = content.charAt(index);
    switch (next) {
      case 'b': resultChar = '\b'; break;
      case 'f': resultChar = '\f'; break;
      case 'n': resultChar = '\n'; break;
      case 'r': resultChar = '\r'; break;
      case 't': resultChar = '\t'; break;
      case '"': resultChar = '"';  break;
      case '\\': resultChar = '\\'; break;
      case 'u':
        StringBuilder bu = new StringBuilder();
        bu.append(content.charAt(++index));
        bu.append(content.charAt(++index));
        bu.append(content.charAt(++index));
        bu.append(content.charAt(++index));
        int hexValU = Integer.parseInt(bu.toString(), 16);
        resultChar = (char) hexValU;
        break;

      case 'x':
      case 'X':
        StringBuilder bx = new StringBuilder();
        char cx = content.charAt(index + 1); //index will refer to the last accepted character, therefore we need to use +1
        while (isHexadecimalChar(cx)) {
          bx.append(cx);
          index++;
          cx = content.charAt(index + 1);
        }
        int hexValX = Integer.parseInt(bx.toString(), 16);
        resultChar = (char) hexValX;
        break;

      default: return next;

    }

    return resultChar;
  }



  private boolean isHexadecimalChar(char c) {
    if (c >= '0' && c <= '9') {
      return true;
    }
    if (c >= 'A' && c <= 'F') {
      return true;
    }
    if (c >= 'a' && c <= 'f') {
      return true;
    }
    return false;
  }



  private String charToLog(char c) {
    switch (c) {
      case '\b': return "\\b";
      case '\f': return "\\f";
      case '\n': return "\\n";
      case '\r': return "\\r";
      case '\t': return "\\t";
      case '\'': return "'";
      case '"': return "\"";
      case LS: return "LS (0x2028)";
      case PS: return "PS (0x2029)";

      case 0: return "null (0x0000)";

      default: return String.valueOf(c);
    }

  }


}
