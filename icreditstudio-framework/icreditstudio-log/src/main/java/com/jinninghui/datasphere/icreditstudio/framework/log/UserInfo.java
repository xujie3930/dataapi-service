package com.jinninghui.datasphere.icreditstudio.framework.log;

import com.jinninghui.datasphere.icreditstudio.framework.utils.Constants;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述 ：用户信息
 *
 * @author lidab
 * @date 2017/11/15.
 */
public class UserInfo {
    String userId;
    String userType;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static UserInfo getUserInfo() {
        String userId = "NONE";
        String userType = "NONE";
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request != null) {
                userId = request.getHeader(Constants.HeadKey.USERID.code) == null ? "NONE" : request.getHeader(Constants.HeadKey.USERID.code);
                userType = request.getHeader(Constants.HeadKey.USERTYPE.code) == null ? "NONE" : request.getHeader(Constants.HeadKey.USERTYPE.code);
            }
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUserType(userType);
        return userInfo;
    }

}
