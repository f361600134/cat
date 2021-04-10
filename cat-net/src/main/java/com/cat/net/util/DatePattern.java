package com.cat.net.util;


public class DatePattern {

	/** 
	 * HH:mm 格式
	 */
	public static final String PATTERN_HH_MM = "HH:mm";

	/** 
	 * yyyyMMdd 格式
	 */
	public static final  String PATTERN_YYYYMMDD = "yyyyMMdd";

	/** 
	 * yyyy-MM-dd 格式
	 */
	public static final  String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";

	/** 
	 * yyyyMMddHHmm 格式
	 */
	public static final  String PATTERN_YYYYMMDDHHMM = "yyyyMMddHHmm";

	/** 
	 * yyyy-MM-dd HH:mm:ss 格式
	 */
	public static final  String PATTERN_NORMAL = "yyyy-MM-dd HH:mm:ss";

	/** 
	 * yyyyMMddHHmmss 格式
	 */
	public static final  String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	
	/**
	 * EEE MMM dd HH:mm:ss zzz yyyy 系统默认格式
	 */
	public static final String SYSTEM_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";
	
	
	/**
	 * 根据日期字符串长度获取日期格式
	 * @param dateString
	 * @return
	 */
	public static String getPattern(String dateString){
		int length = dateString.length();
		if(length == PATTERN_HH_MM.length()){
			return PATTERN_HH_MM;
		} else if(length == PATTERN_YYYYMMDD.length()){
			return PATTERN_YYYYMMDD;
		} else if(length == PATTERN_YYYY_MM_DD.length()){
			return PATTERN_YYYY_MM_DD;
		} else if(length == PATTERN_YYYYMMDDHHMM.length()){
			return PATTERN_YYYYMMDDHHMM;
		} else if(length == PATTERN_NORMAL.length()){
			return PATTERN_NORMAL;
		} else if(length == PATTERN_YYYYMMDDHHMMSS.length()){
			return PATTERN_YYYYMMDDHHMMSS;
		} else if(length == SYSTEM_DEFAULT.length()){
			return SYSTEM_DEFAULT;
		}
		return PATTERN_NORMAL;
	}
	
}