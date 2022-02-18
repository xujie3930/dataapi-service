package com.jinninghui.datasphere.icreditstudio.framework.validate;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author liyanhui
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(ValidatorConfig.class)
public @interface EnableValidator {
}
