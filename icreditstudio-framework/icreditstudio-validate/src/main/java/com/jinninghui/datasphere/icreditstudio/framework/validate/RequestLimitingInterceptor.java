package com.jinninghui.datasphere.icreditstudio.framework.validate;

/**
 * @author xujie
 * @description 请求限流
 * @create 2021-12-23 10:05
 **/
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.jinninghui.datasphere.icreditstudio.framework.validate.RequestLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RequestLimitingInterceptor implements HandlerInterceptor {
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //这里可以抽出去定义返回信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("10001", "玩命加载中,请稍后再试");
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                RequestLimiter rateLimit = handlerMethod.getMethodAnnotation(RequestLimiter.class);
                //判断是否有注解
                if (rateLimit != null) {
                    // 获取请求url
                    String url = request.getRequestURI();
                    RateLimiter rateLimiter;
                    // 判断map集合中是否有创建好的令牌桶
                    if (!rateLimiterMap.containsKey(url)) {
                        // 创建令牌桶,以n r/s往桶中放入令牌
                        rateLimiter = RateLimiter.create(rateLimit.QPS());
                        rateLimiterMap.put(url, rateLimiter);
                    }
                    rateLimiter = rateLimiterMap.get(url);
                    // 获取令牌
                    boolean acquire = rateLimiter.tryAcquire(rateLimit.timeout(), rateLimit.timeunit());
                    if (acquire) {
                        //获取令牌成功
                        return true;
                    } else {
                        log.warn("请求被限流,url:{}", request.getServletPath());
                        makeResult(response, renderJson(jsonObject));
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            makeResult(response, renderJson(jsonObject));
            return false;
        }
    }

    private void makeResult(HttpServletResponse response, JSONObject jo) {
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.append(jo.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject renderJson(Object o) {
        return JSONObject.parseObject(JSON.toJSONString(o));
    }
}
