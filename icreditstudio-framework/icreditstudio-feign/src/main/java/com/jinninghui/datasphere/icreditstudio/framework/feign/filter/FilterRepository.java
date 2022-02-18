package com.jinninghui.datasphere.icreditstudio.framework.feign.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liyanhui
 */
public class FilterRepository implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FilterRepository.class);

    /**
     * 根据bean加载的顺序调用，先加载先调用，可以使用Order设置顺序
     *
     * <pre>
     * <code>@Bean</code>
     * <code>@Order(1)</code>
     * <code>public FeignFilter headerFilter(){</code>
     *   <code>return requestTemplate -> {</code>
     *        <code>RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();</code>
     *              <code>if(null != requestAttributes) {</code>
     *                  <code>HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();</code>
     *                  <code>Enumeration<String> headerNames = request.getHeaderNames();</code>
     *                  <code>if (headerNames != null) {</code>
     *                      <code>while (headerNames.hasMoreElements()) {</code>
     *                          <code>String name = headerNames.nextElement();</code>
     *                          <code>String values = request.getHeader(name);</code>
     *                          <code>requestTemplate.header(name, values);</code>
     *                      <code>}</code>
     *                   <code>}</code>
     *             <code> }</code>
     *      <code>};</code>
     * <code>}</code>
     * <pre>
     *
     * @see org.springframework.core.annotation.Order
     */
    private LinkedList<FeignFilter> feignFilters = new LinkedList<>();

    public List<FeignFilter> getFeignFilters(){
        return Collections.unmodifiableList(feignFilters);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof FeignFilter){
            FeignFilter feignFilter = (FeignFilter) bean;
            feignFilters.addLast(feignFilter);
            logger.info("Loaded Feign Filter - {}", bean.getClass());
        }
        return bean;
    }
}
