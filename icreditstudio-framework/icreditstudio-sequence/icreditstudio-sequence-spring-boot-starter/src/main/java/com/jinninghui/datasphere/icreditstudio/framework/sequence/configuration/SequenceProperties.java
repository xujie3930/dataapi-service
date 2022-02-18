package com.jinninghui.datasphere.icreditstudio.framework.sequence.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClassName: SequenceProperties <br/>
 * Description: sequence配置
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
@ConfigurationProperties(
        prefix = "sequence"
)
public class SequenceProperties {

    private boolean enable = true;

    /**
     * default, snowflake
     */
    private String type;

    /**
     * mac, random, simple
     */
    private String generate;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenerate() {
        return generate;
    }

    public void setGenerate(String generate) {
        this.generate = generate;
    }
}
