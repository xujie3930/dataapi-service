package com.jinninghui.datasphere.icreditstudio.framework.feign.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author liyanhui
 */
public class FilterChain implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FilterChain.class);
    private FilterRepository repository;

    public FilterChain(FilterRepository repository) {
        this.repository = repository;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        List<FeignFilter> feignFilters = repository.getFeignFilters();
        for(FeignFilter feignFilter: feignFilters){
            try {
                feignFilter.doFilter(requestTemplate);
            }catch (Exception e){
                logger.error("Error while doing {} with {}, cause: {}", feignFilter.getClass(), requestTemplate.toString(), e);
            }
        }
    }
}
