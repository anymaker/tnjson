package a2u.tn.utils.json;

import a2u.tn.utils.json.JsonSerializer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("num", 123);
    map.put("str", "str");
    map.put("emptyobj", new LinkedHashMap<String, Object>());
    map.put("emptylist", new ArrayList<Object>());

    Map<String, Object> innermap = new LinkedHashMap<String, Object>();
    innermap.put("innernum", 345);
    map.put("innermap", innermap);

    List<Object> list1 = new ArrayList<Object>();
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
}