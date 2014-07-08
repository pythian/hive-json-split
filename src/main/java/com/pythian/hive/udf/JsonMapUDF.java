package com.pythian.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *   UDF to split a JSON object into a Hive map
 *
 */

@Description(name="json_map",
        value = "_FUNC_(json) - Returns a map of key-value pairs from a JSON object"
)
public class JsonMapUDF extends GenericUDF {
    private StringObjectInspector stringInspector;

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        try {
            String jsonString = this.stringInspector.getPrimitiveJavaObject(arguments[0].get());

            ObjectMapper om = new ObjectMapper();
            HashMap<String, String> map = new HashMap<String, String>();
            TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>() {};
            HashMap<String, Object> root = (HashMap<String, Object>) om.readValue(jsonString, mapType);
            for (String s: root.keySet()){
                map.put(s, root.get(s).toString());
            }
            return map;
        } catch( JsonProcessingException jsonProc) {
            throw new HiveException(jsonProc);
        } catch (IOException e) {
            throw new HiveException(e);
        } catch (NullPointerException npe){
            return null;
        }

    }

    @Override
    public String getDisplayString(String[] arg0) {
        return "json_split(" + arg0[0] + ")";
    }

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments)
            throws UDFArgumentException {
        if(arguments.length != 1
           || ! arguments[0].getCategory().equals( Category.PRIMITIVE)
           || ((PrimitiveObjectInspector)arguments[0]).getPrimitiveCategory() != PrimitiveCategory.STRING) {
            throw new UDFArgumentException("Usage : json_split(jsonstring) ");
        }
        stringInspector = (StringObjectInspector) arguments[0];

        return ObjectInspectorFactory.getStandardMapObjectInspector(PrimitiveObjectInspectorFactory.javaStringObjectInspector,
                                                                    PrimitiveObjectInspectorFactory.javaStringObjectInspector);

    }

}
