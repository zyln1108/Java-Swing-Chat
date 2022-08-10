package tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTool {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /*对象转换json字符串*/
    public static <T> String objToString(T obj){
        if(obj == null){
            return null;
        }
        try{
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*json字符串转换为对象*/
    public static <T> T stringToObj(String str,Class<T> clazz) throws JsonProcessingException {
        if(str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
    }
}
