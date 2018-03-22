# tnjson
Map to JSON converter and JSON to Map parser with support JSON5 
[https://spec.json5.org/](https://spec.json5.org/ "json5")

# Introduction

By simple:
What is a json-object? This is a string representation of javascript object.
What is a javascript-object? This is a associative array where the string as key and any object as value.

The most similar structure in the java is a Map<String, Object>.
This is natural json representation, and this is very useful for debug, research, and in support of production system.


# How to use

## Parsing 

Suppose we have the json5-string:

```json
{
  // comments
  unquoted: 'and you can quote me on that',
  singleQuotes: 'I can use "double quotes" here',
  lineBreaks: "Look, Mom! \
No \\n's!",
  hexadecimal: 0xdecaf,
  leadingDecimalPoint: .8675309, andTrailing: 8675309.,
  positiveSign: +1,
  trailingComma: 'in objects', andIn: ['arrays',],
  "backwardsCompatible": "with JSON",
}
```

In a code:

```java
import a2u.tn.utils.json.JsonParser;
...

Map<String, Object> result = JsonParser.parse(json);
```

And we get this result :

    LinkedHashMap: result {
      unquoted -> Stirng: "and you can quote me on that"
      singleQuotes -> Stirng: "I can use \"double quotes\" here"
      lineBreaks -> Stirng: "Look, Mom! \nNo \\n's!"
      hexadecimal -> Integer: 912559
      leadingDecimalPoint -> Double: 0.8675309
      andTrailing -> Double: 8675309.0
      positiveSign -> Integer: 1
      trailingComma -> Stirng: "in objects"
      andIn -> ArrayList:[ Stirng: "arrays" ] size = 1
      backwardsCompatible -> Stirng: "with JSON"
    }


## Convert to json


Suppose we must generate next json:

```json
{
  "num": 123,
  "str": "str",
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
```

#### Import
```java
import a2u.tn.utils.json.JsonSerializer;
```


####    Fill Map structure
```java
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("num", 123);
    map.put("str", "str");

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
```

####    Generate json
```java
    String json = JsonSerializer.toJson(map);
```
Аnd everything.
