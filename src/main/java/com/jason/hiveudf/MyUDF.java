package com.jason.hiveudf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * @Description:
 * @author: 贾森
 * @date: 2024年11月11日 09:47
 */
public class MyUDF extends GenericUDF {
    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length !=1) {
            throw  new UDFArgumentLengthException("please give me  only one arg");
        }

        if (!arguments[0].getCategory().equals(ObjectInspector.Category.PRIMITIVE)){
            throw  new UDFArgumentTypeException(1, "i need primitive type arg");
        }

        return PrimitiveObjectInspectorFactory.javaIntObjectInspector;
    }

    /**
     * 解决具体逻辑的
     */
    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        Object o = arguments[0].get();
        if(o==null){
            return 0;
        }
        return o.toString().length();
    }
    // 用于获取解释的字符串
    @Override
    public String getDisplayString(String[] strings) {
        return "";
    }
}
