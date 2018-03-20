package a2u.tn.utils.json;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Navigate by Map represented object
 * Every value in the Map must be
 *   either a simple value (string or number or boolean),
 *   either by a Map - a nested object (Map<String, Object>),
 *   either an array of values (List<Object>)
 */
public class MapNavigator {
  public static Object fromPath(Map<String, Object> map, String path) throws Exception {

    Object res = map;
    List<String> pathList =  Arrays.asList(path.split("\\."));

    StringBuilder node = new StringBuilder();

    for (String pat : pathList) {
      if (res == null) {
        return null;
      }
      if (res instanceof Map) {
        res = ((Map) res).get(pat);
      }
      else if (res instanceof List) {
        int ix = Integer.parseInt(pat);
        res = ((List) res).get(ix);
      }
      else {
        throw new Exception("Incorrect path: node=" + node.toString() + " is not a Map or List.");
      }
      node.append(".").append(pat);
    }

    return res;
  }
}
