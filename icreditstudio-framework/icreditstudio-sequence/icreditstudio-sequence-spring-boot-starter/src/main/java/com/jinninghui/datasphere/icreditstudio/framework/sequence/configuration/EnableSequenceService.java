package com.jinninghui.datasphere.icreditstudio.framework.sequence.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author liyanhui
 * @since 1.0.14
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SequenceAutoConfiguration.class)
public @interface EnableSequenceService {

}
