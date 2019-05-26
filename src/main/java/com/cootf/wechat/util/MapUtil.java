package com.cootf.wechat.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MapUtil {
	
	private static Logger logger = LoggerFactory.getLogger(MapUtil.class);

	/**
	 * Map key 排序
	 * @param map map
	 * @return map
	 */
	public static Map<String,String> order(Map<String, String> map){
		HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(	map.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			Map.Entry<String, String> item = infoIds.get(i);
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}


	/**
	 * 转换对象为map
	 * @param object object
	 * @param ignore ignore
	 * @return map
	 */
	public static Map<String,String> objectToMap(Object object,String... ignore){
		Map<String,String> tempMap = new LinkedHashMap<String, String>();
		for(Field f : getAllFields(object.getClass())){
			if(!f.isAccessible()){
				f.setAccessible(true);
			}
			boolean ig = false;
			if(ignore!=null&&ignore.length>0){
				for(String i : ignore){
					if(i.equals(f.getName())){
						ig = true;
						break;
					}
				}
			}
			if(ig){
				continue;
			}else{
				Object o = null;
				try {
					o = f.get(object);
				} catch (IllegalArgumentException e) {
					logger.error("", e);
				} catch (IllegalAccessException e) {
					logger.error("", e);
				}
				tempMap.put(f.getName(), o==null?"":o.toString());
			}
		}
		return tempMap;
	}

	/**
	 * map转化对象
	 * @param map 待转化map对象
	 * @param beanClass 实体对象
	 * @param <T> 泛型对象
	 * @return 泛型实体
	 * @throws Exception 异常
	 */
	public static <T> T mapToObject(Map<String, String> map,Class<T> beanClass) throws Exception {
		if (map == null) {
			return null;
		}
		try {
			Object obj = beanClass.newInstance();

			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				Method setter = property.getWriteMethod();
				if (setter != null) {
					setter.invoke(obj, map.get(property.getName()));
				}
			}
			return (T) obj;
		} catch (Exception e){
			logger.error("", e);
		}
		return null;
	}

	/**
	 * url 参数串连
	 * @param map map
	 * @param keyLower keyLower
	 * @param valueUrlencode valueUrlencode
	 * @return string
	 */
	public static String mapJoin(Map<String, String> map,boolean keyLower,boolean valueUrlencode){
		StringBuilder stringBuilder = new StringBuilder();
		for(String key :map.keySet()){
			if(map.get(key)!=null&&!"".equals(map.get(key))){
				try {
					String temp = (key.endsWith("_")&&key.length()>1)?key.substring(0,key.length()-1):key;
					stringBuilder.append(keyLower?temp.toLowerCase():temp)
								 .append("=")
								 .append(valueUrlencode?URLEncoder.encode(map.get(key),"utf-8").replace("+", "%20"):map.get(key))
								 .append("&");
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				}
			}
		}
		if(stringBuilder.length()>0){
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取所有Fields,包含父类field
	 * @param clazz clazz
	 * @return list
	 */
	private static List<Field> getAllFields(Class<?> clazz){
		if(!clazz.equals(Object.class)){
			List<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
			List<Field> fields2 = getAllFields(clazz.getSuperclass());
			if(fields2!=null){
				fields.addAll(fields2);
			}
			return fields;
		}else{
			return null;
		}
	}

}
