package a2u.tn.utils.json;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TnJsonBuilderTest {



  private class TestCls01 {
    String str;
    int num;

    public TestCls01(String str, int num) {
      this.str = str;
      this.num = num;
    }

    public String getJsonValue() {
      return String.valueOf(str) + String.valueOf(num);
    }

    @Override
    public String toString() {
      return "TestCls01";
    }
  }

  private Map<String, Object> data;

  @Before
  public void fillMap() {
    data = new LinkedHashMap<>();
    data.put("num", 1);
    data.put("str", "str1");
    Map<String, Object> internal = new LinkedHashMap<>();
    internal.put("num", 100);
    internal.put("str", "str100");
    internal.put("cls", new TestCls01("sss", 500));
    data.put("internal", internal);
  }

  
  @Test
  public void testHandlePath() throws Exception {
    String json;
    String etalon;

    json = TnJson.builder()
      .handlePath((path, value) -> value)
      .readable().singleQuote().withoutKeyQuote().keepNull()
      .buildJson(data);
    etalon = "{num:1,str:'str1',internal:{num:100,str:'str100',cls:{str:'sss',num:500}}}";
    assertEquals(etalon, json);


    json = TnJson.builder()
      .handlePath((path, value) -> {
        if (path.equals(".num")) {
          return 555;
        }
        return value;
      })
      .readable().singleQuote().withoutKeyQuote().keepNull()
      .buildJson(data);
    etalon = "{num:555,str:'str1',internal:{num:100,str:'str100',cls:{str:'sss',num:500}}}";
    assertEquals(etalon, json);



    json = TnJson.builder()
      .handlePath((path, value) -> {
        if (path.equals(".internal.cls")) {
          return 555;
        }
        return value;
      })
      .readable().singleQuote().withoutKeyQuote().keepNull()
      .buildJson(data);
    etalon = "{num:1,str:'str1',internal:{num:100,str:'str100',cls:555}}";
    assertEquals(etalon, json);

  }

  @Test
  public void testHandleType() throws Exception {
    String json;
    String etalon;

    json = TnJson.builder()
      .handleType((value) -> {
        if (value instanceof TestCls01) {
          return ((TestCls01)value).getJsonValue();
        }
        else {
          return value;
        }
      })
      .readable().singleQuote().withoutKeyQuote().keepNull()
      .buildJson(data);
    etalon = "{num:1,str:'str1',internal:{num:100,str:'str100',cls:'sss500'}}";
    assertEquals(etalon, json);

  }

}