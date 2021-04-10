package com.cat.net.http.base;

/**
 * @author Jeremy
 */
public enum ErrorCode {
	
	SUCCESS(0, "成功"),
	;
	
	private final int code;
    private final String desc;
    
    ErrorCode(int moduleId, String desc) {
        this.code = moduleId;
        this.desc = desc;
    }
    
}
