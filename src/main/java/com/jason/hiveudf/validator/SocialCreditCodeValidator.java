package com.jason.hiveudf.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * @Description: 统一社会信用代码校验
 * @author: 贾森
 * @date: 2024年11月11日 10:39
 */
public class SocialCreditCodeValidator extends GenericUDF {
    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length !=1) {
            throw  new UDFArgumentLengthException("please give me  only one arg");
        }

        if (!arguments[0].getCategory().equals(ObjectInspector.Category.PRIMITIVE)){
            throw  new UDFArgumentTypeException(1, "i need primitive type arg");
        }

        return PrimitiveObjectInspectorFactory.javaBooleanObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {

        boolean result = true;
        Object o = arguments[0].get();
        if(o==null){
            return false;
        }
        result = validatorSocialCreditCode(o.toString());
        return result;
    }

    @Override
    public String getDisplayString(String[] strings) {
        return "";
    }

    public static boolean validatorSocialCreditCode(String data){
        if (StringUtils.isBlank(data)){
            return false;
        }
        boolean result = true;
        String patrn = "/^[0-9A-Z]+$/";
        int ancodevalue = 0; //统一社会信用代码每一个值的权重
        int total = 0;
        int logiccheckcode = 0;
        int[] weightedfactors = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28}; //加权因子
        String string = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,J,K,L,M,N,P,Q,R,T,U,W,X,Y";
        String str = "0123456789ABCDEFGHJKLMNPQRTUWXY";
        if (data.length()!=18||data.matches(patrn)){
            result = false;
        }else {
            String[] datasplit = data.split("");
            for (int i = 0;i<datasplit.length-1;i++){
                ancodevalue = str.indexOf(datasplit[i]);
                total = total+ancodevalue*weightedfactors[i];
            }
            logiccheckcode = 31 - total % 31;
            if (logiccheckcode == 31){
                logiccheckcode = 0;
            }
            String[] split = string.split(",");
            String logicCheckCodeSplit = split[logiccheckcode];
            String eighteen = data.substring(17, 18);
            if (!logicCheckCodeSplit.equals(eighteen)){
                result = false;
            }

        }
        return result;
    }
}
