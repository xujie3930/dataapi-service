package com.jinninghui.datasphere.icreditstudio.framework.feign.filter;

import feign.RequestTemplate;
import org.springframework.core.Ordered;

/**
 * @author liyanhui
 */
public interface FeignFilter extends Ordered {

     void doFilter(RequestTemplate requestTemplate);
}
