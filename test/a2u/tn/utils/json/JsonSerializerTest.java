package a2u.tn.utils.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class JsonSerializerTest {

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
          "list1": [789,
          987],
          "list2": [{
            "innernum": 345
          },
          {
            "innernum": 345
          }]
        }
    */
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num", 123);
    map.put("str", "str");
    map.put("emptyobj", new LinkedHashMap<String, Object>());
    map.put("emptylist", new ArrayList<Object>());

    Map<StringBuilder, Object> innermap = new LinkedHashMap<StringBuilder, Object>();
    StringBuilder key = new StringBuilder("inner").append("num");
    innermap.put(key, 345);
    map.put("innermap", innermap);

    Collection<Object> list1 = new TreeSet<Object>();
    list1.add(789);
    list1.add(987);
    map.put("list1", list1);

    List<Object> list2 = new ArrayList<Object>();
    list2.add(innermap);
    list2.add(innermap);
    map.put("list2", list2);

    String json = JsonSerializer.toJson(map);
    String etalon = "{\"num\":123,\"str\":\"str\",\"emptyobj\":{},\"emptylist\":[],\"innermap\":{\"innernum\":345},\"list1\":[789,987],\"list2\":[{\"innernum\":345},{\"innernum\":345}]}";
    assertEquals(etalon, json);
  }


  @Test
  public void testToJson2() throws Exception {
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


    Map<String, Object> map = JsonParser.parse(test);
    etalon = "{\"num\":123,\"str\":\"str\\u0020one\\u0020twho\",\"emptyobj\":{},\"emptylist\":[],\"innermap\":{\"innernum\":345},\"list1\":[789,987],\"list2\":[{\"innernum\":345},\"aaaaa\",{\"innernum\":345}]}";
    json = JsonSerializer.toJson(map, JsonSerializer.Mode.HARD);
    assertEquals(etalon, json);

    etalon = "{\"num\":123,\"str\":\"str one twho\",\"emptyobj\":{},\"emptylist\":[],\"innermap\":{\"innernum\":345},\"list1\":[789,987],\"list2\":[{\"innernum\":345},\"aaaaa\",{\"innernum\":345}]}";
    json = JsonSerializer.toJson(map, JsonSerializer.Mode.LIGHT);
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
    json = JsonSerializer.toJson(map, JsonSerializer.Mode.FORMATTED);
    assertEquals(etalon, json);
  }

  @Test
  public void testToJson51() throws Exception {
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


    Map<String, Object> map = JsonParser.parse(test);

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
    String json = JsonSerializer.toJson(map, JsonSerializer.Mode.JSON5);
    assertEquals(etalon, json);
  }

  @Test
  public void testToJson52() throws Exception {
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
    Map<String, Object> map = JsonParser.parse(test);
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
    String json = JsonSerializer.toJson(map, JsonSerializer.Mode.JSON5);
    assertEquals(etalon, json);
  }


  @Test
  public void testWithoutKeyQuote() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num1", 111);
    map.put("str", "str");
    map.put("num2", 222);
    String etalon = "{num1:111,str:\"str\",num2:222}";
    String json = JsonSerializer.Settings.init().withoutKeyQuote().serialize(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testSingleQuote1() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num1", 111);
    map.put("str1", "sttr");
    map.put("str2", "st'tr");
    map.put("str3", "st\"tr");
    map.put("num2", 222);
    String etalon = "{'num1':111,'str1':'sttr','str2':'st\\u0027tr','str3':'st\\u0022tr','num2':222}";
    String json = JsonSerializer.Settings.init().singleQuote().serialize(map);
    assertEquals(etalon, json);
  }
  @Test
  public void testSingleQuote2() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num1", 111);
    map.put("str1", "sttr");
    map.put("str2", "st'tr");
    map.put("str3", "st\"tr");
    map.put("num2", 222);
    String etalon = "{'num1':111,'str1':'sttr','str2':'st\\'tr','str3':'st\"tr','num2':222}";
    String json = JsonSerializer.Settings.init().singleQuote().readable().serialize(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testFormated() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num1", 111);
    map.put("str1", "str");
    map.put("num2", 222);
    String etalon = "{\n" +
                    "  \"num1\": 111,\n" +
                    "  \"str1\": \"str\",\n" +
                    "  \"num2\": 222\n" +
                    "}";
    String json = JsonSerializer.Settings.init().formated().serialize(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testReadable() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("str1", "12345`~!@#$%^&*()_+-={}[]:\"|;'\\<>?,./67890");
    String etalon = "{\"str1\":\"12345`~!@#$%^&*()_+-={}[]:\\\"|;'\\\\<>?,.\\/67890\"}";
    String json = JsonSerializer.Settings.init().readable().serialize(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testAllowMultiRowString1() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("str1", "aaaaaa\n" +
                    "bbbbbb\n" +
                    "cccccc");
    String etalon = "{\"str1\":\"aaaaaa/\n" +
                    "bbbbbb/\n" +
                    "cccccc\"}";
    String json = JsonSerializer.Settings.init().allowMultiRowString().serialize(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testAllowMultiRowString2() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("str1", "aaaaaa\n" +
                    "bbbbbb\n" +
                    "cccccc");
    String etalon = "{\n" +
                    "  \"str1\": \"aaaaaa/\n" +
                    "bbbbbb/\n" +
                    "cccccc\"\n" +
                    "}";
    String json = JsonSerializer.Settings.init().allowMultiRowString().formated().serialize(map);
    assertEquals(etalon, json);
  }


  @Test
  public void testKeepNull() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num1", 111);
    map.put("null", null);
    map.put("num2", 222);
    String etalon = "{\"num1\":111,\"null\":null,\"num2\":222}";
    String json = JsonSerializer.Settings.init().keepNull().serialize(map);
    assertEquals(etalon, json);
  }

  @Test
  public void testNotKeepNull() {
    Map<CharSequence, Object> map = new LinkedHashMap<CharSequence, Object>();
    map.put("num1", 111);
    map.put("null", null);
    map.put("num2", 222);
    String etalon = "{\"num1\":111,\"num2\":222}";
    String json = JsonSerializer.Settings.init().serialize(map);
    assertEquals(etalon, json);
  }


}