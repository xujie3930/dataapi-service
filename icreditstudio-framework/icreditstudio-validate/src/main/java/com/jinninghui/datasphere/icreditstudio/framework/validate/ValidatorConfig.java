package com.jinninghui.datasphere.icreditstudio.framework.validate;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author liyanhui
 */
public class ValidatorConfig {

    /**
     * 实例化HibernateValidator
     *
     * @return Validator
     */
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation
                .byProvider(HibernateValidator.class)
                .configure().failFast(true)
                .buildValidatorFactory();

        return validatorFactory.getValidator();
    }

    /**
     * 默认是普通模式，会返回所有的验证不通过信息集合
     * @return
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator());
        return postProcessor;
    }
}

