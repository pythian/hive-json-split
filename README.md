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

Usage
===============

The UDF accepts a single JSON string containing only an array. In the Hive CLI:

```
add jar target/JsonSplit-1.0-SNAPSHOT.jar;
create temporary function json_split as 'com.pythian.hive.udf.JsonSplitUDF';

create table json_example (json string);
load data local inpath 'example.json' into table json_example;

SELECT ex.* FROM json_example LATERAL VIEW explode(json_split(json_example.json)) ex;
```

The json_split function by itself takes a json string like '[1,2,3]' and turns it into an array of tuples: 

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

Then you can use the built-in explode UDTF and LATERAL VIEW to produce one record per element in the JSON array. 

You can access the JSON string for the element with the json_string attribute. Row_id is the position in the array.
