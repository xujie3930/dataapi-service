package com.jinninghui.datasphere.icreditstudio.framework.feign.filter.interval;

import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.FeignFilter;
import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.FilterRepository;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liyanhui
 */
public class LoggingFilter implements FeignFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private FilterRepository repository;

    public LoggingFilter(FilterRepository repository) {
        this.repository = repository;
    }

    @Override
    public void doFilter(RequestTemplate requestTemplate) {
        logger.info("Begin do feign filter chain. filter size : {}", repository.getFeignFilters().size());
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}
