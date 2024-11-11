package com.jason.hiveudf.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * @Description: 校验身份证号码
 * @author: 贾森
 * @date: 2024年11月11日 10:29
 */
public class IdCardValidator extends GenericUDF {
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

        String data = o.toString();
        if (StringUtils.isBlank(data)){
            result = false;
        }else{
            if (data.length()!=18){
                result = false;
            }else{
                // 转换为数组
                String[] split = data.split("");
                for (int i= 0; i<split.length-2;i++) {
                    if(!StringUtils.isNumeric(split[i])){
                        result = false;
                    }
                }
                if(result){
                    if (validatorIdCardOfDate(data) && validateIdCardByEighteen(split)){

                    }else {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getDisplayString(String[] strings) {
        return "";
    }

    /**
     * @description: 身份证日期位校验
     * @Author: Mr.Han
     * @Date: 2021/5/21
     */
    private static boolean validatorIdCardOfDate(String idCard){
        int year = Integer.parseInt(idCard.substring(6, 10));
        int month = Integer.parseInt(idCard.substring(10, 12));
        int day = Integer.parseInt(idCard.substring(12, 14));
        try {
            LocalDate date = LocalDate.of(year, month, day);
            if (date.getYear()!=year||date.getMonthValue() != month||date.getDayOfMonth() != day){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @description: 身份证第18位校验及其前17位是否数字
     * @Author: Mr.Han
     * @Date: 2021/5/21
     */
    private static boolean validateIdCardByEighteen(String[] split){
        int[] Wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };    // 加权因子
        int[] ValideCode = { 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };            // 身份证验证位值.10代表X
        int sum = 0;
        //String eighteen = split[17].toLowerCase();
        /*if (StringUtils.isNumeric(eighteen)){
            return true;
        }*/
        if ("x".equals(split[17].toLowerCase())){
            split[17] = "10";
        }
        //split[17]="10";

        int[] TranForm = Arrays.asList(split).stream().mapToInt(Integer::parseInt).toArray(); //将字符串数组转换为int
        for (int i=0;i<17;i++){
            sum +=Wi[i]*TranForm[i];
        }
        int valCodePosition = sum%11;
        if (TranForm[17]== ValideCode[valCodePosition]){
            return true;
        }else {
            return false;
        }
    }
}
