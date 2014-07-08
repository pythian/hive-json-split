hive-json-split
===============

A simple UDF to split JSON arrays into Hive arrays.

Building
===============

Check out the code and run 

```
   mvn package
```

to build an uberjar with everything you need. 

Split UDF
===============

The split UDF accepts a single JSON string containing only an array. In the Hive CLI:

```
add jar target/JsonSplit-1.0-SNAPSHOT.jar;
create temporary function json_split as 'com.pythian.hive.udf.JsonSplitUDF';

create table json_example (json string);
load data local inpath 'split_example.json' into table json_example;

SELECT ex.* FROM json_example LATERAL VIEW explode(json_split(json_example.json)) ex;
```

```json_split``` converts the string to the following array of structs, which are exploded into individual records: 

```
[
  {
    row_id:1, 
    json_string:'1' 
  },
  { 
    row_id:2, 
    json_string:'2' 
  }, 
  {
    row_id:3, 
    json_string:'3' 
  }
]
```

You can access the JSON string for the element with the ```json_string``` attribute. The ```json_string``` can be any arbitrary JSON string, including another array or a nested object. ```row_id``` is the position in the array.

Map UDF
==============

The map UDF accepts a flat JSON object (only integer and string values, no arrays or maps) and converts it into a Hive map. The elements of the map don't have to be defined until query-time, and can be accessed with the square bracket syntax ['key'].

```
add jar target/JsonSplit-1.0-SNAPSHOT.jar;
create temporary function json_map as 'com.pythian.hive.udf.JsonMapUDF';

create table json_map_example (json string);
load data local inpath 'map_example.json' into table json_map_example;

SELECT json_map(json)['x'] FROM json_map_example LATERAL VIEW explode(json_split(json_example.json)) ex;
```

The above converts the JSON string to a map, then pulls out the value for each record's key 'x'.
