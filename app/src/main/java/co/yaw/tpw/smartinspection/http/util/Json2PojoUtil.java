package co.yaw.tpw.smartinspection.http.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class Json2PojoUtil {

    public static JSONArray getJSONArray(String result) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static JSONObject getJSONObject(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static List<Object> fromJsonToBasePojo(JSONArray jsonArray, Class pojoClsType) throws Exception {
        return fromJsonToJava(jsonArray, pojoClsType, getInternalClsTypes(pojoClsType));
    }

    public static Object fromJsonToBasePojo(JSONObject json, Class pojoClsType) throws Exception {
        return fromJsonToJava(json, pojoClsType, getInternalClsTypes(pojoClsType));
    }

    private static List<Class<?>> getInternalClsTypes(Class pojoClsType) throws Exception {
        Method getInternalClsTypes = pojoClsType.getDeclaredMethod("getInternalClsTypes");
        List<Class<?>> internalClsTypes = (List<Class<?>>) getInternalClsTypes.invoke(null);
        return internalClsTypes;
    }

    public static List<Object> fromJsonToJava(JSONArray jsonArray, Class pojoClsType, List<Class<?>> internalClsTypes) throws Exception {
        List<Object> objList = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jobj = jsonArray.getJSONObject(i);
            Object obj = fromJsonToJava(jobj, pojoClsType, internalClsTypes);
            objList.add(obj);
        }

        return objList;
    }

    public static Object fromJsonToJava(JSONObject json, Class pojoClsType, List<Class<?>> internalClsTypes) throws Exception {
        Field[] fields = pojoClsType.getDeclaredFields();
        Object obj = pojoClsType.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                json.get(name);
            } catch (Exception ex) {
                continue;
            }

            if (json.get(name) != null && !"".equals(json.getString(name))) {
                Class<?> clsType = field.getType();
                if (clsType.equals(List.class)) {

                    Class<?> listClass = getActualType(field);
                    if (listClass != null) {
                        Object tmpObj = json.get(name);
                        Object tmp = getObjFromArrayOrObject(tmpObj, listClass, internalClsTypes);
                        if (tmp != null) {
                            field.set(obj, tmp);
                        }
                    }
                    continue;
                }

                if (clsType.equals(Long.class) || clsType.equals(long.class)) {
                    field.set(obj, Long.parseLong(json.getString(name)));
                } else if (clsType.equals(Boolean.class) || clsType.equals(boolean.class)) {
                    field.set(obj, Boolean.parseBoolean(json.getString(name)));
                } else if (clsType.equals(String.class)) {
                    field.set(obj, json.getString(name));
                } else if (clsType.equals(Double.class) || clsType.equals(double.class)) {
                    field.set(obj, Double.parseDouble(json.getString(name)));
                } else if (clsType.equals(Float.class) || clsType.equals(float.class)) {
                    field.set(obj, Float.parseFloat(json.getString(name)));
                } else if (clsType.equals(Integer.class) || clsType.equals(int.class)) {
                    field.set(obj, Integer.parseInt(json.getString(name)));
                } else if (clsType.equals(Date.class)) {
                    String value = (String) json.get(name);
                    Date date = DateParserUtil.parse(value);
                    field.set(obj, date);
                } else {
                    if (internalClsTypes != null && !internalClsTypes.isEmpty()) {
                        for (int i = 0; i < internalClsTypes.size(); i++) {
                            Class<?> tmpClsType = internalClsTypes.get(i);
                            if (clsType.equals(tmpClsType)) {
                                Object tmpObj = json.get(name);
                                Object tmp = getObjFromArrayOrObject(tmpObj, tmpClsType, internalClsTypes);
                                if (tmp != null) {
                                    field.set(obj, tmp);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return obj;
    }

    private static Class<?> getActualType(Field field) {
        Class<?> listClass = null;
        Type mainType = field.getGenericType();
        if (mainType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) mainType;
            listClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }

        return listClass;
    }

    private static Object getObjFromArrayOrObject(Object obj, Class<?> clsType, List<Class<?>> internalClsTypes) throws Exception {
        Object result = null;
        if (obj instanceof JSONArray) {
            JSONArray tmpJsonArray = (JSONArray) obj;
            result = fromJsonToJava(tmpJsonArray, clsType, internalClsTypes);
        } else if (obj instanceof JSONObject) {
            JSONObject tmpJsonObject = (JSONObject) obj;
            result = fromJsonToJava(tmpJsonObject, clsType, internalClsTypes);
        } else {
        }

        return result;
    }
}
