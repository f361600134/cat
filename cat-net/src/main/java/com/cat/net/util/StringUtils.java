package com.cat.net.util;

public class StringUtils {

	 /**
		 * 将字符串首字符小写
		 * 
		 * @param str
		 *            源字符串
		 * @return 首字符大写后的字符串
		 * @see [类、类#方法、类#成员]
		 */
		public static String firstCharLower(String str) {
			char ch = str.charAt(0);
			if (Character.isLowerCase(ch)) {
				return str;
			}
			char[] cs = str.toCharArray();
			cs[0] += 32;
			return String.valueOf(cs);
		}

		/**
		 * 将字符串首字符大写
		 * 
		 * @param str
		 *            源字符串
		 * @return 首字符大写后的字符串
		 * @see [类、类#方法、类#成员]
		 */
		public static String firstCharUpper(String str) {
			if (Character.isUpperCase(str.charAt(0))) {
				return str;
			}
			char[] cs = str.toCharArray();
			cs[0] -= 32;
			return String.valueOf(cs);
		}
		
		/**
		 * 是否空
		 * @param str
		 * @return true, null or empty
		 */
		public static boolean isBlank(String str) {
			return str != null && !str.equals("");
		}
    
}
