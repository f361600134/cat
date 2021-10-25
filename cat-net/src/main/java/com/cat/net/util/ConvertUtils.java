package com.cat.net.util;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cat.net.util.convert.Converter;
import com.cat.net.util.convert.EnumMode;
import com.cat.net.util.convert.ValueMode;
import com.google.common.reflect.TypeToken;

/**
 * 转换工具类
 */
public abstract class ConvertUtils {
	
	private static ConcurrentMap<Type, Converter<?>> converterMap = new ConcurrentHashMap<>();

	public static void registerConverter(Converter<?> converter) {
		Type classType = TypeUtils.resolveTypeArguments(converter.getClass(), Converter.class)[0];
		Converter<?> exitsConverter = converterMap.putIfAbsent(classType, converter);
		if (exitsConverter != null) {
			throw new RuntimeException(String.format("重复的转换器注册[%s]", classType.getTypeName()));
		}
	}

	public static byte toByte(Object input) {
		if (input instanceof Number) {
			return ((Number) input).byteValue();
		} else if (input instanceof String) {
			return Byte.valueOf((String) input);
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 byte 型", input));
		}
	}

	public static byte toByte(Object input, byte valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toByte(input);
	}
	
	public static short toShort(Object input) {
		if (input instanceof Number) {
			return ((Number) input).shortValue();
		} else if (input instanceof String) {
			return Short.valueOf((String) input);
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 short 型", input));
		}
	}

	public static int toInt(Object input) {
		if (input instanceof Number) {
			return ((Number) input).intValue();
		} else if (input instanceof String) {
			return Integer.valueOf((String) input);
		} else if (input instanceof Enum) {
			return ((Enum<?>) input).ordinal();
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 int 型", input));
		}
	}

	public static int toInt(Object input, int valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toInt(input);
	}

	public static String toString(Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof String) {
			return (String) input;
		} else if (input instanceof byte[]) {
			try {
				return new String((byte[]) input, Charset.defaultCharset());
			} catch (Exception e) {
				throw new RuntimeException(String.format("字符集编码 [%s] 构建 String !", Charset.defaultCharset()));
			}
		}
		return String.valueOf(input);
	}

	public static String toString(Object input, String valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toString(input);
	}

	public static float toFloat(Object input) {
		if (input instanceof Number) {
			return ((Number) input).floatValue();
		} else if (input instanceof String) {
			return Float.valueOf((String) input);
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 float 型", input));
		}
	}

	public static float toFloat(Object input, float valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toFloat(input);
	}

	public static double toDouble(Object input) {
		if (input instanceof Number) {
			return ((Number) input).doubleValue();
		} else if (input instanceof String) {
			return Double.valueOf((String) input);
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 double 型", input));
		}
	}

	public static double toDouble(Object input, double valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toDouble(input);
	}

	public static long toLong(Object input) {
		if (input instanceof Number) {
			return ((Number) input).longValue();
		} else if (input instanceof String) {
			return Long.valueOf((String) input);
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 long 型", input));
		}
	}

	public static long toLong(Object input, long valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toLong(input);
	}

	public static boolean toBoolean(Object input) {
		if (input instanceof Boolean) {
			return (Boolean) input;
		} else if (input instanceof Number) {
			return ((Number) input).intValue() == 1;
		} else if (input instanceof String) {
			String stringValue = (String) input;
			return "true".equalsIgnoreCase(stringValue) || "1".equalsIgnoreCase(stringValue);
		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 boolean 型", input));
		}
	}

	public static boolean toBoolean(Object input, boolean valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toBoolean(input);
	}

	public static Date toDate(Object input) {
		if (input == null) {
			return null;
		}
		if (input instanceof Date) {
			return (Date) input;
		} else if (input instanceof Number) {
			return new Date(toLong(input));
		} else if (input instanceof String) {
			String stringValue = (String) input;

			try {
				SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sFormat.parse(stringValue);

			} catch (Exception e) {
				try {
					if (stringValue.matches("^(-{0,1})\\d+$")) {// 是字符串的数字标识则尝试将其看成时间戳转换
						return new Date(toLong(input));
					}
				} catch (Exception e2) {
				}
			}

			throw new RuntimeException(String.format("值 [%s] 不能转换为 date 型", input));

		} else {
			throw new RuntimeException(String.format("值 [%s] 不能转换为 date 型", input));
		}
	}
	
	public static LocalDateTime toLocalDateTime(Object input){
		if(input == null){
			return null;
		}
		try {
		
			if(input instanceof LocalDateTime){
				return (LocalDateTime) input;
			} else if(input instanceof Long){
				return LocalDateTime.ofInstant(Instant.ofEpochMilli((long)input), ZoneId.systemDefault());
			} else if(input instanceof Integer){
				return LocalDateTime.ofInstant(Instant.ofEpochSecond((int)input), ZoneId.systemDefault());
			} else if(input instanceof String){
				return LocalDateTime.parse(input.toString());
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		throw new RuntimeException(String.format("值 [%s] 不能转换为 LocalDateTime 型", input));
	}

	public static Date toBoolean(Object input, Date valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toDate(input);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toEnum(Object input, Class<T> enumClass) {
		if (input == null) {
			return null;
		}
		if (input.getClass() == enumClass) {
			return (T) input;
		}

		EnumMode enumMode = enumClass.getAnnotation(EnumMode.class);
		ValueMode valueMode = enumMode == null ? ValueMode.ORDINAL : enumMode.value();
		if (input instanceof Number && valueMode == ValueMode.ORDINAL) {
			return (T) EnumUtils.getEnum((Class) enumClass, ((Number) input).intValue());
		} else if ((input instanceof String && enumMode == null) || valueMode == ValueMode.NAME) {
			return (T) EnumUtils.valueOf((Class) enumClass, (String) input);
		} else if (valueMode == ValueMode.VALUE) {
			return (T) EnumUtils.getEnumByValue((Class) enumClass, input);
		} else {
			throw new RuntimeException(String.format("无法从[%s] 类型转换为 [%s]类型!", input, enumClass));
		}
	}

	public static <T> T toEnum(Object input, Class<T> enumClass, T valueIfNull) {
		if (input == null) {
			return valueIfNull;
		}
		return toEnum(input, enumClass);
	}
	
	public static  Object getEnumValue(Object input){
		if (input == null) {
			return null;
		}
		
		EnumMode enumMode = input.getClass().getAnnotation(EnumMode.class);
		if(enumMode != null){
			if(enumMode.value() == ValueMode.NAME){
				return ((Enum<?>)input).name();
			} else if(enumMode.value() == ValueMode.VALUE){
				return EnumUtils.getEnumValue(input);
			} 
		} 
		return ((Enum<?>)input).ordinal();
	} 
	
	/**
	 * 类型转换
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, V> T cast(V value){
		return (T)value;
	}

	/**
	 * 将输出对象转换为指定类型
	 * 
	 * @param input
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertTo(Object input, TypeReference<T> type) {
		Object object = convert(input, type.getType());
		if (object == null) {
			return null;
		}
		return (T) object;
	}

	/**
	 * 将输出对象转换为指定类型
	 * 
	 * @param input
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertTo(Object input, Class<T> targetType) {
		Object object = convert(input, targetType);
		if (object == null) {
			return null;
		}
		return (T) object;
	}
	

	/**
	 * 将指定对象转换为目标类实例
	 * 
	 * @param <T>
	 * @param input
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object convert(Object input, Type targetType) {

		if (input == null) {
			return null;
		}

		if (targetType == null) {
			return null;
		}
		
		Class inputClass = input.getClass();
		if (inputClass.isArray()) { // 数组

			Class inputComponentType = inputClass.getComponentType();

			if (TypeUtils.isArrayType(targetType)) {// 目标是数组

				Type targetComponentType = TypeUtils.getArrayComponentType(targetType);
				Class targetComponentClass = TypeUtils.getRawType(targetComponentType, inputComponentType);
				int len = Array.getLength(input);
				Object arr = Array.newInstance(targetComponentClass, len);
				for (int i = 0; i < len; i++) {
					Object o = Array.get(input, i);
					Object v = convert(o, targetComponentType);
					Array.set(arr, i, v);
				}

				return arr;

			} else if (TypeUtils.isAssignable(targetType, Collection.class)) {// 目标是集合

				int len = Array.getLength(input);
				Collection collection = (Collection) newCollection(targetType, len);

				TypeToken<?> subType = TypeToken.of(targetType);
				Type targetComponentType = subType.resolveType(Collection.class.getTypeParameters()[0]).getType();
				if (targetComponentType instanceof TypeVariable) {// List list;
																	// 的情况
																	// 没有附带任何泛型
					targetComponentType = ((TypeVariable) targetComponentType).getBounds()[0];
				}

				for (int i = 0; i < len; i++) {
					Object o = Array.get(input, i);
					Object v = convert(o, targetComponentType);
					collection.add(v);
				}

				return collection;
			} else if (inputComponentType == byte.class) {
				if (targetType == String.class) {
					try {
						return new String((byte[]) input, Charset.defaultCharset());
					} catch (Exception e) {
						throw new RuntimeException(String.format("字符集编码 [%s] 构建 String !", Charset.defaultCharset()));
					}
				}
			}

		} else if (Collection.class.isAssignableFrom(inputClass)) { // 集合

			Class inputComponentType = inputClass.getClass();

			if (TypeUtils.isArrayType(targetType)) {// 目标是数组

				Type targetComponentType = TypeUtils.getArrayComponentType(targetType);
				Class targetComponentClass = TypeUtils.getRawType(targetComponentType, inputComponentType);
				int len = Array.getLength(input);
				Object arr = Array.newInstance(targetComponentClass, len);
				int index = 0;
				for (Object o : (Collection) input) {
					Object v = convert(o, targetComponentType);
					Array.set(arr, index++, v);
				}
				return arr;

			} else if (TypeUtils.isAssignable(targetType, Collection.class)) {// 目标是集合

				Collection collection = (Collection) newCollection(targetType, ((Collection) input).size());
				TypeToken<?> subType = TypeToken.of(targetType);
				Type targetComponentType = subType.resolveType(Collection.class.getTypeParameters()[0]).getType();
				if (targetComponentType instanceof TypeVariable) {
					targetComponentType = ((TypeVariable) targetComponentType).getBounds()[0];
				}

				for (Object o : (Collection) input) {
					Object v = convert(o, targetComponentType);
					collection.add(v);
				}

				return collection;
			}

		} else if (Map.class.isAssignableFrom(inputClass)) {// Map

			if (TypeUtils.isAssignable(targetType, Map.class)) {

				Map map = (Map) newCollection(targetType, ((Map) input).size());

				TypeToken<?> subType = TypeToken.of(targetType);

				Type keyType = subType.resolveType(Map.class.getTypeParameters()[0]).getType();
				if (keyType instanceof TypeVariable) {
					keyType = ((TypeVariable) keyType).getBounds()[0];
				}
				Type valueType = subType.resolveType(Map.class.getTypeParameters()[1]).getType();
				if (valueType instanceof TypeVariable) {
					valueType = ((TypeVariable) valueType).getBounds()[0];
				}

				for (Entry<?, ?> entry : ((Map<?, ?>) input).entrySet()) {
					Object key = convert(entry.getKey(), keyType);
					Object value = convert(entry.getValue(), valueType);
					map.put(key, value);
				}

				return map;
			}

		} else {

			if (targetType == byte.class || targetType == Byte.class) {
				return input == null ? 0 : toByte(input);
			}
			if (targetType == short.class || targetType == Short.class) {
				return input == null ? 0 : toShort(input);
			}
			if (targetType == int.class || targetType == Integer.class) {
				return input == null ? 0 : toInt(input);
			}
			if (targetType == long.class || targetType == Long.class) {
				return input == null ? 0 : toLong(input);
			}
			if (targetType == float.class || targetType == Float.class) {
				return input == null ? 0 : toFloat(input);
			}
			if (targetType == double.class || targetType == Double.class) {
				return input == null ? 0 : toDouble(input);
			}
			if (targetType == boolean.class || targetType == Boolean.class) {
				return input == null ? false : toBoolean(input);
			}
			if (targetType == Object.class) {
				return input;
			}
			if (TypeUtils.isAssignable(targetType, Enum.class)) {
				return input == null ? null : toEnum(input, (Class) targetType);
			}
			if (TypeUtils.isAssignable(targetType, Date.class)) {
				return input == null ? null : toDate(input);
			}
			if(TypeUtils.isAssignable(targetType, LocalDateTime.class)){
				return input == null ? null : toLocalDateTime(input);
			}

			// 源是string 目标不是string则json转换
			if (input instanceof String && !TypeUtils.isAssignable(targetType, String.class)) {

				Converter converter = converterMap.get(targetType);
				if (converter != null) {
					return converter.convertFrom(String.valueOf(input));
				}

				String content = (String) input;
				if (StringUtils.isBlank(content)) {
					return null;
				}
				return JSON.parseObject(content, targetType);
			}
		}
		
		if (TypeUtils.isInstance(input, targetType)) {
			return input;
		}

		throw new RuntimeException(String.format("无法从 [%s] 类型转换为 [%s]类型!", input.getClass(), targetType));
	}

	/**
	 * 类型转换如果对象为null则返回默认值
	 * 
	 * @param content
	 * @param type
	 * @return
	 */
	public static Object convertOrDefault(Object content, Class<?> type) {
		if (TypeUtils.isInstance(content, type)) {
			return content;
		}

		if (content == null) {
			return getDefaultValue(type);
		}

		return convert(content, type);
	}

	/**
	 * 获取指定类型默认值
	 * 
	 * @param type
	 * @return
	 */
	public static Object getDefaultValue(Class<?> type) {
		if (TypeUtils.isAssignable(type, int.class)) {
			return 0;
		} else if (TypeUtils.isAssignable(type, long.class)) {
			return 0L;
		} else if (TypeUtils.isAssignable(type, float.class)) {
			return 0.0f;
		} else if (TypeUtils.isAssignable(type, double.class)) {
			return 0.0d;
		} else if (TypeUtils.isAssignable(type, boolean.class)) {
			return false;
		} else if (TypeUtils.isAssignable(type, char.class)) {
			return '\u0000';
		} else if (TypeUtils.isAssignable(type, short.class)) {
			return (short) 0;
		} else if (TypeUtils.isAssignable(type, boolean.class)) {
			return false;
		}
		return null;
	}

	/**
	 * 查询具体的集合类型
	 * 
	 * @param type
	 * @param size
	 * @return
	 */
	private static Object newCollection(Type type, int size) {
		if (TypeUtils.isAssignable(type, List.class)) {
			if (size >= 0) {
				return new ArrayList<>(size);
			} else {
				return new ArrayList<>();
			}
		} else if (TypeUtils.isAssignable(type, Set.class)) {
			if (size >= 0) {
				return new LinkedHashSet<>(size);
			} else {
				return new LinkedHashSet<>();
			}
		} else if (TypeUtils.isAssignable(type, Queue.class)) {
			return new LinkedList<>();
		} else if (TypeUtils.isAssignable(type, Map.class)) {
			if (size >= 0) {
				return new LinkedHashMap<>(size);
			} else {
				return new LinkedHashMap<>();
			}
		} else {
			throw new RuntimeException(String.format("无法识别的集合类型 [%s]!", type));
		}
	}

}
