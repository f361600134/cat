package com.cat.net.util;

import java.lang.reflect.Field;
import java.util.stream.Stream;


/**
 * 枚举工具类
 */
public class EnumUtils{
	
	/**
	 * 根据枚举类名和Key的名字获得枚举对象
	 * 
	 * @param <T>
	 * @param  enumClass		枚举类对象
	 * @param  fieldName		类型名	
	 * @return T
	 */
	public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String fieldName) {
		return Enum.valueOf(enumClass, fieldName);
	}

	/**
	 * 根据枚举的类名和一个常量值构建枚举对象
	 * 
	 * @param <T>
	 * @param enumClass			枚举类对象
	 * @param value				需要查询的值
	 * @return
	 */
	public static <T extends Enum<T>> T getEnum(Class<T> enumClass, Integer value) {
		return enumClass.getEnumConstants()[value];
	}
	
	/**
	 * 根据枚举名称查找枚举
	 * @param enumClass
	 * @param name
	 * @return
	 */
	public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
		return Stream.of(enumClass.getEnumConstants()).filter(t -> name.equals(t.name())).findFirst().orElse(null);
	}
	
	/**
	 * 根据枚举的类型和值获取枚举
	 * @param enumClass	枚举类型
	 * @param value		枚举值
	 * @return
	 */
	
	public static <T extends Enum<T>,K> T getEnumByValue(Class<T> enumClass, K value) {
		T[] enums = enumClass.getEnumConstants();
		Field field = getValueField(enumClass, value.getClass());
		for(T enumObject : enums){
			try {
				@SuppressWarnings("unchecked")
				K enumValue = (K) field.get(enumObject);
				if(enumValue.equals(value)){
					return enumObject;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException(String.format("枚举类%s找不到值为%s的类型", enumClass.getName(), value));
	}
	
	private static <K> Field getValueField(Class<?> enumClass,Class<K> valueClass){
		Field[] fields = enumClass.getDeclaredFields();
		for(Field field : fields){
			if(TypeUtils.primitiveToWrapper(field.getType()) == valueClass){
				field.setAccessible(true);
				return field;
			}
		}
		throw new RuntimeException();
	}
	
	/**
	 * 获取枚举的字段值如果有多个则获取第一个字段值
	 * @param enumObject
	 * @return
	 */
	public static Object getEnumValue(Object enumObject){
		Field[] fields = enumObject.getClass().getDeclaredFields();
		for(Field field : fields){
			if(!TypeUtils.isAssignable(field.getType(), enumObject.getClass()) && !TypeUtils.isArrayType(field.getType())){
				try {
					if(!field.isAccessible()){
						field.setAccessible(true);
					}
					return field.get(enumObject);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		throw new RuntimeException(String.format("枚举%s没有可取的字段值", enumObject.getClass()));
	}

}
