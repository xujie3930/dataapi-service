package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.SysUser;

import java.util.Objects;

/**
 * @author Peng
 */
public class UserUtil {

    public static ThreadLocal<SysUser> local = new ThreadLocal<>();

    public static final void setUser(SysUser user) {
        local.set(user);
    }

    public static final SysUser getUser() {
        SysUser user = local.get();
        if (Objects.isNull(user)) {
            return new SysUser();
        }
        return user;
    }
}
