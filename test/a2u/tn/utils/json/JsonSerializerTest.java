package a2u.tn.utils.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class JsonSerializerTest {

  @SuppressWarnings("unused")
  private static class Test01 {
    public    String field01 = "public";
              String field02 = "default";
    private   String field03 = "private";
    static    String field04 = "static";
    transient String field05 = "transient";
  }

  @SuppressWarnings("unused")
  private class Test02 {
    public    String field01 = "public";
    String field02 = "default";
    private   String field03 = "private";
    transient String field05 = "transient";

    private String toJson() {
      return "json";
    }
  }

  @Test
  public void testToJson_Types() throws Exception {
    String json;

    json = TnJson.toJson(null);
    assertEquals("null", json);



    json = TnJson.toJson(1);
    assertEquals("1", json);

    json = TnJson.toJson(1L);
    assertEquals("1", json);

    json = TnJson.toJson(Long.MAX_VALUE);
    assertEquals("9223372036854775807", json);

    json = TnJson.toJson(Long.MIN_VALUE);
    assertEquals("-9223372036854775808", json);



    json = TnJson.toJson(1.1);
    assertEquals("1.1", json);

    json = TnJson.toJson(Double.POSITIVE_INFINITY);
    assertEquals("Infinity", json);

    json = TnJson.toJson(Double.NEGATIVE_INFINITY);
    assertEquals("-Infinity", json);

    json = TnJson.toJson(Double.MAX_VALUE);
    assertEquals("1.7976931348623157E308", json);

    json = TnJson.toJson(Double.MIN_VALUE);
    assertEquals("4.9E-324", json);



    json = TnJson.toJson("str");
    assertEquals("\"str\"", json);



    int[] dimInt = new int[]{1,2,3};
    json = TnJson.toJson(dimInt);
    assertEquals("[1,2,3]", json);

    String[] dimStr = new String[]{"str1", "str2"};
    json = TnJson.toJson(dimStr);
    assertEquals("[\"str1\",\"str2\"]", json);

    int[] dimEmpty = new int[0];
    json = TnJson.toJson(dimEmpty);
    assertEquals("[]", json);

    int[][] dimMany = { {11, 12, 13}, {21} };
    json = TnJson.toJson(dimMany);
    assertEquals("[[11,12,13],[21]]", json);



    Collection<Object> collection;
    collection = new ArrayList<>();
    collection.add(1);
    collection.add("str");
    json = TnJson.toJson(collection);
    assertEquals("[1,\"str\"]", json);

    collection = new LinkedHashSet<>();
    collection.add(1);
    collection.add("str");
    json = TnJson.toJson(collection);
    assertEquals("[1,\"str\"]", json);



    Map<Object, Object> map = new LinkedHashMap<>();
    map.put("k1", 1);
    map.put("k2", "str");
    json = TnJson.toJson(map);
    assertEquals("{\"k1\":1,\"k2\":\"str\"}", json);

    map = new LinkedHashMap<>();
    map.put(1, 1);
    map.put(2, "str");
    json = TnJson.toJson(map);
    assertEquals("{\"1\":1,\"2\":\"str\"}", json);



    Test01 obj01 = new Test01();
    json = TnJson.toJson(obj01);
    assertEquals("{field01:\"public\",field02:\"default\",field04:\"static\"}", json);

    Test02 obj02 = new Test02();
    json = TnJson.toJson(obj02);
    assertEquals("json", json);
  }


  @Test
  public void testToJson() throws Exception {
    /*
        {
          "num": 123,
          "str": "str",
          "emptyobj": {},
          "emptylist": [],
          "innermap": {
            "innernum": 345
          },
          "list1": [789, 987],
          "list2": [{
            "innernum": 345
          },
          {
            "innernum": 345
          }]
        }
    */
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num", 123);
    map.put("str", "str");
    map.put("emptyobj", new LinkedHashMap<String, Object>());
    map.put("emptylist", new ArrayList<>());

    Map<StringBuilder, Object> innermap = new LinkedHashMap<>();
    StringBuilder key = new StringBuilder("inner").append("num");
    innermap.put(key, 345);
    map.put("innermap", innermap);

    Collection<Object> list1 = new TreeSet<>();
    list1.add(789);
    list1.add(987);
    map.put("list1", list1);

    List<Object> list2 = new ArrayList<>();
    list2.add(innermap);
    list2.add(innermap);
    map.put("list2", list2);

    String json = TnJson.toJson(map);
    String etalon = "{\"num\":123,\"str\":\"str\",\"emptyobj\":{},\"emptylist\":[],\"innermap\":{\"innernum\":345},\"list1\":[789,987],\"list2\":[{\"innernum\":345},{\"innernum\":345}]}";
    assertEquals(etalon, json);
  }


  @Test
  public void testToJson_Mode() throws Exception {
    String etalon;
    String json;
    String test = "{\n" +
                    "  num: 123,\n" +
                    "  str: \"str one twho\",\n" +
                    "  emptyobj: {\n" +
                    "\n" +
                    "  },\n" +
                    "  emptylist: [\n" +
                    "    \n" +
                    "  ],\n" +
                    "  innermap: {\n" +
                    "    innernum: 345\n" +
                    "  },\n" +
                    "  list1: [\n" +
                    "    789,\n" +
                    "    987\n" +
                    "  ],\n" +
                    "  list2: [\n" +
                    "    {\n" +
                    "      innernum: 345\n" +
                    "    },\n" +
                    "    \"aaaaa\",\n" +
                    "    {\n" +
                    "      innernum: 345\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n";


    Map<String, Object> map = TnJson.parse(test);
    etalon = "{\"num\":123,\"str\":\"str\\u0020one\\u0020twho\",\"emptyobj\":{},\"emptylist\":[],\"innermap\":{\"innernum\":345},\"list1\":[789,987],\"list2\":[{\"innernum\":345},\"aaaaa\",{\"innernum\":345}]}";
    json = TnJson.toJson(map, TnJson.Mode.HARD);
    assertEquals(etalon, json);

    etalon = "{\"num\":123,\"str\":\"str one twho\",\"emptyobj\":{},\"emptylist\":[],\"innermap\":{\"innernum\":345},\"list1\":[789,987],\"list2\":[{\"innernum\":345},\"aaaaa\",{\"innernum\":345}]}";
    json = TnJson.toJson(map, TnJson.Mode.LIGHT);
    assertEquals(etalon, json);

    etalon = "{\n" +
             "  \"num\": 123,\n" +
             "  \"str\": \"str one twho\",\n" +
             "  \"emptyobj\": {\n" +
             "\n" +
             "  },\n" +
             "  \"emptylist\": [\n" +
             "    \n" +
             "  ],\n" +
             "  \"innermap\": {\n" +
             "    \"innernum\": 345\n" +
             "  },\n" +
             "  \"list1\": [\n" +
             "    789,\n" +
             "    987\n" +
             "  ],\n" +
             "  \"list2\": [\n" +
             "    {\n" +
             "      \"innernum\": 345\n" +
             "    },\n" +
             "    \"aaaaa\",\n" +
             "    {\n" +
             "      \"innernum\": 345\n" +
             "    }\n" +
             "  ]\n" +
             "}";
    json = TnJson.toJson(map, TnJson.Mode.FORMATTED);
    assertEquals(etalon, json);
  }

  @Test
  public void testToJson_json5() throws Exception {
    String test = "{\n" +
                  "  num: 123,\n" +
                  "  str: \"str one twho\",\n" +
                  "  emptyobj: {\n" +
                  "\n" +
                  "  },\n" +
                  "  emptylist: [\n" +
                  "    \n" +
                  "  ],\n" +
                  "  innermap: {\n" +
                  "    innernum: 345\n" +
                  "  },\n" +
                  "  list1: [\n" +
                  "    789,\n" +
                  "    987\n" +
                  "  ],\n" +
                  "  list2: [\n" +
                  "    {\n" +
                  "      innernum: 345\n" +
                  "    },\n" +
                  "    \"aaaaa\",\n" +
                  "    {\n" +
                  "      innernum: 345\n" +
                  "    }\n" +
                  "  ]\n" +
                  "}\n";


    Map<String, Object> map = TnJson.parse(test);

    String etalon = "{\n" +
                    "  num: 123,\n" +
                    "  str: \"str one twho\",\n" +
                    "  emptyobj: {\n" +
                    "\n" +
                    "  },\n" +
                    "  emptylist: [\n" +
                    "    \n" +
                    "  ],\n" +
                    "  innermap: {\n" +
                    "    innernum: 345\n" +
                    "  },\n" +
                    "  list1: [\n" +
                    "    789,\n" +
                    "    987\n" +
                    "  ],\n" +
                    "  list2: [\n" +
                    "    {\n" +
                    "      innernum: 345\n" +
                    "    },\n" +
                    "    \"aaaaa\",\n" +
                    "    {\n" +
                    "      innernum: 345\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
    String json = TnJson.toJson(map, TnJson.Mode.JSON5);
    assertEquals(etalon, json);
  }

  @Test
  public void testToJson_json5_commonExample() throws Exception {
    String test = "{\n" +
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
    Map<String, Object> map = TnJson.parse(test);
    String etalon = "{\n" +
                    "  unquoted: \"and you can quote me on that\",\n" +
                    "  singleQuotes: \"I can use \\\"double quotes\\\" here\",\n" +
                    "  lineBreaks: \"Look, Mom! /\n" +
                    "No \\\\n's!\",\n" +
                    "  hexadecimal: 912559,\n" +
                    "  leadingDecimalPoint: 0.8675309,\n" +
                    "  andTrailing: 8675309.0,\n" +
                    "  positiveSign: 1,\n" +
                    "  trailingComma: \"in objects\",\n" +
                    "  andIn: [\n" +
                    "    \"arrays\"\n" +
                    "  ],\n" +
                    "  backwardsCompatible: \"with JSON\"\n" +
                    "}";
    String json = TnJson.toJson(map, TnJson.Mode.JSON5);
    assertEquals(etalon, json);
  }


  @Test
  public void testWithoutKeyQuote() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num1", 111);
    map.put("str", "str");
    map.put("num2", 222);
    String etalon = "{num1:111,str:\"str\",num2:222}";
    String json = TnJson.builder().withoutKeyQuote().buildJson(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testSingleQuote1() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num1", 111);
    map.put("str1", "sttr");
    map.put("str2", "st'tr");
    map.put("str3", "st\"tr");
    map.put("num2", 222);
    String etalon = "{'num1':111,'str1':'sttr','str2':'st\\u0027tr','str3':'st\\u0022tr','num2':222}";
    String json = TnJson.builder().singleQuote().buildJson(map);
    assertEquals(etalon, json);
  }
  @Test
  public void testSingleQuote2() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num1", 111);
    map.put("str1", "sttr");
    map.put("str2", "st'tr");
    map.put("str3", "st\"tr");
    map.put("num2", 222);
    String etalon = "{'num1':111,'str1':'sttr','str2':'st\\'tr','str3':'st\"tr','num2':222}";
    String json = TnJson.builder().singleQuote().readable().buildJson(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testFormated() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num1", 111);
    map.put("str1", "str");
    map.put("num2", 222);
    String etalon = "{\n" +
                    "  \"num1\": 111,\n" +
                    "  \"str1\": \"str\",\n" +
                    "  \"num2\": 222\n" +
                    "}";
    String json = TnJson.builder().formated().buildJson(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testReadable() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("str1", "12345`~!@#$%^&*()_+-={}[]:\"|;'\\<>?,./67890");
    String etalon = "{\"str1\":\"12345`~!@#$%^&*()_+-={}[]:\\\"|;'\\\\<>?,.\\/67890\"}";
    String json = TnJson.builder().readable().buildJson(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testAllowMultiRowString1() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("str1", "aaaaaa\n" +
                    "bbbbbb\n" +
                    "cccccc");
    String etalon = "{\"str1\":\"aaaaaa/\n" +
                    "bbbbbb/\n" +
                    "cccccc\"}";
    String json = TnJson.builder().allowMultiRowString().buildJson(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testAllowMultiRowString2() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("str1", "aaaaaa\n" +
                    "bbbbbb\n" +
                    "cccccc");
    String etalon = "{\n" +
                    "  \"str1\": \"aaaaaa/\n" +
                    "bbbbbb/\n" +
                    "cccccc\"\n" +
                    "}";
    String json = TnJson.builder().allowMultiRowString().formated().buildJson(map);
    assertEquals(etalon, json);
  }


  @Test
  public void testKeepNull() {
    String etalon;
    String json;
    //with keepNull
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num1", 111);
    map.put("null", null);
    map.put("num2", 222);
    etalon = "{\"num1\":111,\"null\":null,\"num2\":222}";
    json = TnJson.builder().keepNull().buildJson(map);
    assertEquals(etalon, json);

    //without keepNull (default)
    etalon = "{\"num1\":111,\"num2\":222}";
    json = TnJson.builder().buildJson(map);
    assertEquals(etalon, json);

    //in list with keepNull
    List<Object> list = new ArrayList<>();
    list.add(1);
    list.add(null);
    list.add(3);
    map.put("list", list);
    etalon = "{\"num1\":111,\"null\":null,\"num2\":222,\"list\":[1,null,3]}";
    json = TnJson.builder().keepNull().buildJson(map);
    assertEquals(etalon, json);

    //in list without keepNull (default)
    etalon = "{\"num1\":111,\"num2\":222,\"list\":[1,3]}";
    json = TnJson.builder().buildJson(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testNotKeepNull() {
    Map<CharSequence, Object> map = new LinkedHashMap<>();
    map.put("num1", 111);
    map.put("null", null);
    map.put("num2", 222);
    String etalon = "{\"num1\":111,\"num2\":222}";
    String json = TnJson.builder().buildJson(map);
    assertEquals(etalon, json);
  }


}