package com.cat.net.util.convert;

/**
 * 
 * 转换接口
 *
 * @param <T>
 */
public interface Converter<T> {

    T convertFrom(String content);

}