package a2u.tn.utils.json;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class JsonParserTest {

  @org.junit.Test
  public void testParseFirstMethod() throws Exception {

    testParseStandart();
    testParseStandartString();
    testParseStandartBoolean();

    //json5
    testParseTrtailingComa();
    testParseNumber();
    testParseComment();

    testParseOther();

    testIncorrectness();

  }

  private void testParseStandart() throws Exception {
    String json;
    Map<String, Object> result;

    json = "{\"num\":123}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num"));

    json = "{\"num1\":123, \"num2\": 456}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));
    assertEquals(456, result.get("num2"));

    json = "{\"num1\":123,\"num2\":456}";   //compact
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));
    assertEquals(456, result.get("num2"));

    json = "{ \"num1\" : 123, \"num2\" : 456 }";   //max
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));
    assertEquals(456, result.get("num2"));

    json = "{\n" +
           "\t\"num1\": 123,\n" +
           "\t\"num2\": 456\n" +
           "}";   //formated
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));
    assertEquals(456, result.get("num2"));


    json = "{\"str\": \"str\"}";
    result = TnJson.parse(json);
    assertEquals("str", result.get("str"));


    json = "{\"list\": [111,222]}";
    result = TnJson.parse(json);
    assertEquals(111, ((List)result.get(JsonParser.DEFAULT_LIST_KEY)).get(0));
    assertEquals(222, ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(1));

    json = "{\"list\": [\"111\",\"222\"]}";
    result = TnJson.parse(json);
    assertEquals("111", ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(0));
    assertEquals("222", ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(1));

    json = "{\"list\": [{\"num\":111, \"str\":\"str111\"}, {\"num\":222, \"str\":\"str222\"}]}";
    result = TnJson.parse(json);
    assertEquals(111, ((Map) ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(0)).get("num"));
    assertEquals("str222", ((Map) ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(1)).get("str"));
  }

  private void testParseStandartString() throws Exception {
    String json;
    Map<String, Object> result;

    json = "{\"str\": \"str\"}";
    result = TnJson.parse(json);
    assertEquals("str", result.get("str"));

    json = "{\"str\": \"str\\u0040a\"}";
    result = TnJson.parse(json);
    assertEquals("str@a", result.get("str"));

    json = "{\"str\": \"str\\x40\"}";
    result = TnJson.parse(json);
    assertEquals("str@", result.get("str"));

    json = "{\"str\": \"str\\x40gg\"}";
    result = TnJson.parse(json);
    assertEquals("str@gg", result.get("str"));

    json = "{\"str\": \"\\\"AAA\\\"\"}"; // str = "AAA"
    result = TnJson.parse(json);
    assertEquals("\"AAA\"", result.get("str"));
  }

  private void testParseStandartBoolean() throws Exception {
    String json;
    Map<String, Object> result;

    json = "{num:true}";
    result = TnJson.parse(json);
    assertEquals(true, result.get("num"));

    json = "{num:false}";
    result = TnJson.parse(json);
    assertEquals(false, result.get("num"));

    json = "{num:null}";
    result = TnJson.parse(json);
    assertEquals(null, result.get("num"));
  }

  private void testParseTrtailingComa() throws Exception {
    String json;
    Map<String, Object> result;


    json = "{num:123,}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num"));

    json = "{num:123, }";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num"));


    json = "{\"list\": [111,222,]}";
    result = TnJson.parse(json);
    assertEquals(111, ((List)result.get(JsonParser.DEFAULT_LIST_KEY)).get(0));
    assertEquals(222, ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(1));

    json = "{\"list\": [111,222, ]}";
    result = TnJson.parse(json);
    assertEquals(111, ((List)result.get(JsonParser.DEFAULT_LIST_KEY)).get(0));
    assertEquals(222, ((List) result.get(JsonParser.DEFAULT_LIST_KEY)).get(1));
  }

  private void testParseNumber() throws Exception {
    String json;
    Map<String, Object> result;

    json = "{num:123}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num"));

    json = "{num:+123}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num"));

    json = "{num:-123}";
    result = TnJson.parse(json);
    assertEquals(-123, result.get("num"));


    json = "{num:123.}";
    result = TnJson.parse(json);
    assertEquals(123.0, result.get("num"));

    json = "{num:.123}";
    result = TnJson.parse(json);
    assertEquals(0.123, result.get("num"));



    json = "{num:123e-456, }";
    result = TnJson.parse(json);
    assertEquals(Double.parseDouble("123e-456"), result.get("num"));



    json = "{num:0xdecaf, }";
    result = TnJson.parse(json);
    assertEquals(0xdecaf, result.get("num"));

    json = "{num:-0xC0FFEE, }";
    result = TnJson.parse(json);
    assertEquals(-0xC0FFEE, result.get("num"));



    json = "{num:Infinity, }";
    result = TnJson.parse(json);
    assertEquals(Double.POSITIVE_INFINITY, result.get("num"));

    json = "{num:+Infinity, }";
    result = TnJson.parse(json);
    assertEquals(Double.POSITIVE_INFINITY, result.get("num"));

    json = "{num:-Infinity, }";
    result = TnJson.parse(json);
    assertEquals(Double.NEGATIVE_INFINITY, result.get("num"));


    json = "{num:NaN, }";
    result = TnJson.parse(json);
    assertEquals(Double.NaN, result.get("num"));


    json = "{num:"+ Integer.MAX_VALUE +"}";
    result = TnJson.parse(json);
    assertEquals(2147483647L, result.get("num"));

    json = "{num:" + Integer.MAX_VALUE/10 + "}";
    result = TnJson.parse(json);
    assertEquals(214748364, result.get("num"));

    json = "{num:" + Long.MAX_VALUE + "}";
    result = TnJson.parse(json);
    assertEquals(new BigInteger("9223372036854775807"), result.get("num"));

    json = "{num:" + Long.MAX_VALUE/10 + "}";
    result = TnJson.parse(json);
    assertEquals(922337203685477580L, result.get("num"));

  }

  private void testParseComment() throws Exception {
    String json;
    Map<String, Object> result;

    //multirow comment /* */
    json = "{/*\"num1\"*/sss:123, \"num2\": 456}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("sss"));

    json = "{sss/*\"num1\"*/:123, \"num2\": 456}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("sss"));

    json = "{\"num1\":123/*, \"num2\": 456*/}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));

    json = "{\"num1\":123,/* \"num2\": 456*/}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));

    json = "{\"num1\":123, /*\"num2\": 456*/}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));

    json = "{/*\"num1\":123,*/ \"num2\": 456}";
    result = TnJson.parse(json);
    assertEquals(456, result.get("num2"));

    json = "{\"num1\":/*123*/321, \"num2\": 456}";
    result = TnJson.parse(json);
    assertEquals(321, result.get("num1"));
    assertEquals(456, result.get("num2"));

    json = "{\"num1\":/*123*/ 321, \"num2\": 456}";
    result = TnJson.parse(json);
    assertEquals(321, result.get("num1"));
    assertEquals(456, result.get("num2"));

    //single line comment //
    json = "{\n" +
           "\t//\"num1\": 123,\n" +
           "\t\"num2\": 456\n" +
           "}";
    result = TnJson.parse(json);
    assertEquals(456, result.get("num2"));

    json = "{\n" +
           "\t\"num1\": 123,\n" +
           "//\"num2\": 456\n" +
           "}";
    result = TnJson.parse(json);
    assertEquals(123, result.get("num1"));

  }

  private void testParseOther() throws Exception {
    String json;
    Map<String, Object> result;

    json = "{}";
    result = TnJson.parse(json);
    assertEquals(0, result.size());

    json = "{num\u0040:true}";
    result = TnJson.parse(json);
    assertEquals(true, result.get("num@"));

    json = "{\n" +
           "    image: {\n" +
           "        width: 1920,\n" +
           "        height: 1080,\n" +
           "        'aspect-ratio': '16:9',\n" +
           "    }\n" +
           "}";
    result = TnJson.parse(json);
    assertEquals("16:9", MapNavigator.fromPath(result, "image.aspect-ratio"));



    json = "{\n" +
           "  // comments\n" +
           "  unquoted: 'and you can quote me on that',\n" +
           "  singleQuotes: 'I can use \"double quotes\" here',\n" +
           "  lineBreaks: \"Look, Mom! \\\n" +
           "No \\\\n's!\",\n" +
           "  hexadecimal: 0xdecaf,\n" +
           "  leadingDecimalPoint: .8675309, andTrailing: 8675309.,\n" +
           "  positiveSign: +1,\n" +
           "  trailingComma: 'in objects', andIn: ['arrays',],\n" +
           "  \"backwardsCompatible\": \"with JSON\",\n" +
           "}";
    result = TnJson.parse(json);
    assertEquals(1, MapNavigator.fromPath(result, "positiveSign"));


    json = "[]";
    result = TnJson.parse(json);
    assertEquals(0, ((List)result.get(JsonParser.DEFAULT_LIST_KEY)).size());

    json = "[1]";
    result = TnJson.parse(json);
    assertEquals(1, MapNavigator.fromPath(result, "list.0"));

    json = "[1,2]";
    result = TnJson.parse(json);
    assertEquals(1, MapNavigator.fromPath(result, "list.0"));
    assertEquals(2, MapNavigator.fromPath(result, "list.1"));


    json = "// An array with three elements\n" +
           "// and a trailing comma\n" +
           "[\n" +
           "    1,\n" +
           "    true,\n" +
           "    'three',\n" +
           "]";
    result = TnJson.parse(json);
    assertEquals("three", ((List)result.get(JsonParser.DEFAULT_LIST_KEY)).get(2));


    json = "{\"str\": '\\A\\C\\/\\D\\C'}";
    result = TnJson.parse(json);
    assertEquals("AC/DC", result.get("str"));

  }

  private void testIncorrectness() throws Exception {
    String json;

    json = "{1}";
    testException(json);

    json = "{1:}";
    testException(json);

    json = "{1:AAA}";
    testException(json);

    json = "{[]}";
    testException(json);

    json = "{1:'hhhhhh\'2'}";
    testException(json);

    json = "{aaaa \"wwww\", ssss:\"dddd\"}";
    testException(json);

  }
  private void testException(String json) throws Exception {
    try {
      Map<String, Object> result = TnJson.parse(json);
      throw new Exception("No exception.\nResult: " + result);
    }
    catch (ParseException ex) {
      System.out.println("Expected Exception: " + ex.getMessage() + " - Ok.");
    }

  }



  @org.junit.Test
  public void testParseSecondMethod() throws Exception {
    String json;
    Map<String, Object> result;

    json = "{obj1:{num1:123, obj2:{list:[456, 789]}}}";
    result = TnJson.parse(json, null);
    assertEquals("java.util.LinkedHashMap", MapNavigator.fromPath(result, "obj1.obj2").getClass().getName());
    assertEquals("java.util.ArrayList", MapNavigator.fromPath(result, "obj1.obj2.list").getClass().getName());

    result = TnJson.parse(json,
                             new IGetCollection() {
                               @Override
                               public Map<String, Object> forObject(String path) {
                                 return null;
                               }

                               @Override
                               public Collection<Object> forList(String path) {
                                 return null;
                               }
                             });
    assertEquals("java.util.LinkedHashMap", MapNavigator.fromPath(result, "obj1.obj2").getClass().getName());
    assertEquals("java.util.ArrayList", MapNavigator.fromPath(result, "obj1.obj2.list").getClass().getName());

    result = TnJson.parse(json,
                             new IGetCollection() {
                               @Override
                               public Map<String, Object> forObject(String path) {
                                 if (path.equals("root.obj1.obj2")) {
                                   return new HashMap<>();
                                 }
                                 return null;
                               }

                               @Override
                               public Collection forList(String path) {
                                 if (path.equals("root.obj1.obj2.list")) {
                                   return new HashSet();
                                 }
                                 return null;
                               }
                             });
    assertEquals("java.util.HashMap", MapNavigator.fromPath(result, "obj1.obj2").getClass().getName());
    assertEquals("java.util.HashSet", MapNavigator.fromPath(result, "obj1.obj2.list").getClass().getName());

    HashSet list = (HashSet) MapNavigator.fromPath(result, "obj1.obj2.list");
    assertEquals("java.lang.Integer", list.toArray()[0].getClass().getName());


  }
}