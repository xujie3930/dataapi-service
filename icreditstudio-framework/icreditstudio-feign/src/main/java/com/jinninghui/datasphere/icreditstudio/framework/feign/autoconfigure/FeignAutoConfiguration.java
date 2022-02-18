package com.jinninghui.datasphere.icreditstudio.framework.feign.autoconfigure;

import com.jinninghui.datasphere.icreditstudio.framework.feign.decoder.CustomerFeignDecoder;
import com.jinninghui.datasphere.icreditstudio.framework.feign.encoder.CustomerFeignEncoder;
import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.FeignFilter;
import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.FilterChain;
import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.FilterRepository;
import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.interval.HeaderFilter;
import com.jinninghui.datasphere.icreditstudio.framework.feign.filter.interval.LoggingFilter;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.hystrix.HystrixFeign;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.*;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FeignAutoConfiguration extends FeignClientsConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList<>();

    @Autowired(required = false)
    private Logger logger;

    @Override
    @Bean
    @ConditionalOnMissingBean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new CustomerFeignDecoder(this.messageConverters));
    }

//    @Override
    @Bean
    public Encoder feignEncoder() {
        //结合原来的springEncoder，修改该编码器对文件和非文件的处理
        return new CustomerFeignEncoder(this.messageConverters);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public Contract feignContract(ConversionService feignConversionService) {
        return new SpringMvcContract(this.parameterProcessors, feignConversionService);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public FormattingConversionService feignConversionService() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        for (FeignFormatterRegistrar feignFormatterRegistrar : feignFormatterRegistrars) {
            feignFormatterRegistrar.registerFormatters(conversionService);
        }
        return conversionService;
    }

//    @Configuration
    protected static class HystrixFeignConfiguration {
        @Bean
        @Scope("prototype")
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = false)
        public Feign.Builder feignHystrixBuilder() {
            return HystrixFeign.builder();
        }
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Override
    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public Feign.Builder feignBuilder(Retryer retryer) {
        return Feign.builder().retryer(retryer);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public FeignLoggerFactory feignLoggerFactory() {
        return new DefaultFeignLoggerFactory(logger);
    }

    @Bean
    public FilterChain interceptor(){
        return new FilterChain(interceptorRepository());
    }

    @Bean
    public FilterRepository interceptorRepository(){
        return new FilterRepository();
    }

    @Bean
    public FeignFilter loggingFilter(){
        return new LoggingFilter(interceptorRepository());
    }

    @Bean
    public FeignFilter headerFilter(){
        return new HeaderFilter();
    }
}
