package com.jinninghui.datasphere.icreditstudio.dataapi.common;

/**
 * @author Peng
 */
public class SysUser {
    public SysUser() {
        super();
    }

    public SysUser(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
