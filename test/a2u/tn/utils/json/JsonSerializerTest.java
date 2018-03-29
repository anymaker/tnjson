package a2u.tn.utils.json;

import a2u.tn.utils.json.JsonSerializer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Created by KB on 20.03.2018.
 */
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

    etalon = "{\n" +
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
    json = JsonSerializer.toJson(map, JsonSerializer.Mode.JSON5);
    assertEquals(etalon, json);


    test = "{\n" +
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
    map = JsonParser.parse(test);
    etalon = "{\n" +
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
    json = JsonSerializer.toJson(map, JsonSerializer.Mode.JSON5);
    assertEquals(etalon, json);
  }
}