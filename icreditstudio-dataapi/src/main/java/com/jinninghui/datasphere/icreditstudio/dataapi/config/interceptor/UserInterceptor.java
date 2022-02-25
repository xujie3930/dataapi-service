package com.jinninghui.datasphere.icreditstudio.dataapi.config.interceptor;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.SysUser;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.UserUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Peng
 */
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        UserUtil.setUser(new SysUser(userId));
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserUtil.local.remove();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
