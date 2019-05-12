# tnjson
Map to JSON converter and JSON to Map parser with support JSON5
[https://spec.json5.org/](https://spec.json5.org/ "json5")

# Introduction

By simple:

What is a json-object? This is a string representation of javascript object.

What is a javascript-object? This is a associative array where the string as key and any object as value.

The most similar structure in the java is a Map<String, Object>.\
This is natural json representation, and this is very useful for debug, research, and in support of production system.

Of course, for json-object and json-array you can use any types which implement java.util.Map and java.util.Collection interface.

# Requirement
 - Java 1.8
 - Not have any dependency


# Release note

Version 2.x

Created decorator 'TnJson' for greater convenience in large projects.
Autocomplete in IDE produces a more relevant result with less effort.
More short syntaxis.

While serializating:
 - Supporting incoming data with any type - Map, Collection, array, or any other object.
 - Supporting LocalDate, LocalTime and LocalDateTime.
 - Handlers for generating json in concrete path and by concrete type.

Incompatible with tnjson-1.x because:
 - Java with version less then 1.8 is unsupported.
 - Classes JsonSerializer and JsonParser now is internal, and you not haw access to there.
 - You must use decorator TnJson for access to functional.
 - Internal class JsonSerializer.Settings migrated to TnJsonBuilder.


# How to use


## Get jar
You can use maven dependensy
```xml
    <dependency>
      <groupId>com.github.anymaker</groupId>
      <artifactId>tnjson</artifactId>
      <version>2.0</version>
    </dependency>
```
Or download jar from sonatype.org   \
SNAPSHOT: https://oss.sonatype.org/content/repositories/snapshots/com/github/anymaker/tnjson/   \
RELEASE: https://oss.sonatype.org/service/local/repositories/public/content/com/github/anymaker/tnjson/2.0/tnjson-2.0.jar


## Parsing 

Suppose we have the json5-string:

```
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
import a2u.tn.utils.json.TnJson;
...

Map<String, Object> result = TnJson.parse(json);
```

And we get this result :
```
    LinkedHashMap: result {
      unquoted            -> Stirng: "and you can quote me on that"
      singleQuotes        -> Stirng: "I can use \"double quotes\" here"
      lineBreaks          -> Stirng: "Look, Mom! \nNo \\n's!"
      hexadecimal         -> Integer: 912559
      leadingDecimalPoint -> Double: 0.8675309
      andTrailing         -> Double: 8675309.0
      positiveSign        -> Integer: 1
      trailingComma       -> Stirng: "in objects"
      andIn               -> ArrayList:[ Stirng: "arrays" ] size = 1
      backwardsCompatible -> Stirng: "with JSON"
    }
```

By default in this parsing for collections using LinkedHashMap and ArrayList. This is very useful for debug.\
If you unlike LinkedHashMap or ArrayList, you can use method

```
public static Map<String, Object> parse(String data, IGetCollection listener)
```

For example:

```java
    String json;
    Map<String, Object> result;
    json = "{obj1:{num1:123, obj2:{list:[456, 789]}}}";

    result = TnJson.parse(json,
                          new JsonParser.IGetCollection() {
                            @Override
                            public Map<String, Object> forObject(String path) {
                              if (path.equals("root.obj1.obj2")) {
                                return new HashMap<String, Object>();
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

```



## Convertion to json


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
import a2u.tn.utils.json.TnJson;
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
    String json = TnJson.toJson(map);
```



###    Incoming data
For serialization you can use any data - Map, Collection, array, or any other object.
But, as default some types return next result:
java.util.Date          - value obtained as data.getTime(),
java.lang.Boolean       - true | false
java.time.LocalDate     - value corespond ISO-8601 '2011-12-03'          yyyy-MM-dd
java.time.LocalTime     - value corespond ISO-8601 '10:15:30'            hh:mm:ss
java.time.LocalDateTime - value corespond ISO-8601 '2011-12-03T10:15:30' yyyy-MM-ddThh:mm:ss

If incoming object is not a

Character, CharSequence, Number, Boolean,
Date, LocalDate, LocalTime, LocalDateTime,
Map, Collection, and is not array

then attempt is made to get value from the method toJson().
If this method is absent, then value collect from fields with public and default modifiers.

You can override this behavior using handlers
  TnJsonBuilder.IPathHandler - for handing generating json in concrete path
  TnJsonBuilder.ITypeHandler - or handing generating json by concrete type.



###    Specify output json format

You can use additional method for specify output format.

######    HARD
```java
    json = TnJson.toJson(data, TnJson.Mode.HARD);
```
Will be generated compact json-string, where any non-digital and non-letter character in string will be replaced with sequence uXXXX.\
This mode is default, because it has max compatibility with other clients.
```
{"num":123,"str":"str\u0020one\u0020twho",...
```

######    LIGHT
```java
    json = TnJson.toJson(data, TnJson.Mode.LIGHT);
```
Will be generated compact json-string, where non-digital and non-letter character in string will be stay in readable format, if it possible.\
This format is more compact, but is not all client can parse it.
```
{"num":123,"str":"str one twho",...
```

######    FORMATTED
```java
    json = TnJson.toJson(data, TnJson.Mode.FORMATTED);
```
Will be generated json-string in pretty read format, where non-digital and non-letter character in string will be stay in readable format, if it possible.
```json
{
  "num": 123,
  "str": "str one twho",
  "emptyobj": {

  },
  "emptylist": [
    
  ],
  "innermap": {
    "innernum": 345
  },
  "list1": [
    789,
    987
  ],
  "list2": [
    {
      "innernum": 345
    },
    {
      "innernum": 345
    }
  ]
}
```


######    JSON5
```java
    json = TnJson.toJson(data, TnJson.Mode.JSON5);
```
Will be generated json-string in max human readable format json5.\
See detail about json5 on https://json5.org/

```
{
  unquoted: "and you can quote me on that",
  singleQuotes: "I can use \"double quotes\" here",
  lineBreaks: "Look, Mom! /
No \\n's!",
  hexadecimal: 912559,
  leadingDecimalPoint: 0.8675309,
  andTrailing: 8675309.0,
  positiveSign: 1,
  trailingComma: "in objects",
  andIn: [
    "arrays"
  ],
  backwardsCompatible: "with JSON"
}
```


###    Advanced setting of output json

For advanced you can use builder for specify any aspect JSON generation
```
String json = TnJson.builder() ... TUNING ... .buildJson(data);
```
or
```
TnJson jsonBuilder = TnJson.builder() ... TUNING
...
String json = jsonBuilder.buildJson(data);
```

Where
TnJson.builder() - creating new builder
buildJson(data) - run searilization
TUNING - some functions:

withoutKeyQuote() - disable quotation generation for the key
singleQuote()     - Use single quotes
formated()        - Format the final json to pretty
readable()        - Leave characters in a strings as readable as possible
allowMultiRowString() - Allow linefeed in a strings
keepNull()        - Allow null values
handlePath(IPathHandler pathHandler) - Set handler for generating json in concrete path
handleType(ITypeHandler typeHandler) - Set handler for generating json by concrete type

Example, for generate json5 you can use
```
String json = TnJson.builder().readable().formated().withoutKeyQuote().allowMultiRowString().buildJson(map);
```
or
```
TnJson jsonBuilder = TnJson.builder().readable().formated().withoutKeyQuote().allowMultiRowString();
String json1 = jsonBuilder.buildJson(data1);
String json2 = jsonBuilder.buildJson(data2);
String json3 = jsonBuilder.buildJson(data3);
```


--
