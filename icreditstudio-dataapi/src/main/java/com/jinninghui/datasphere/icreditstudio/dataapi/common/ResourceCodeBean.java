package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import com.jinninghui.datasphere.icreditstudio.framework.systemcode.SystemCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyanhui
 */
@SystemCode
public class ResourceCodeBean {

    public enum ResourceCode {
        RESOURCE_CODE_10000000("10000000", "用户异常！"),
        RESOURCE_CODE_10000002("10000002", "名称已存在，请重新输入！"),
        RESOURCE_CODE_10000003("10000003", "分组名称已存在，请重新输入！"),
        RESOURCE_CODE_10000004("10000004", "API分组目录不能拖动到业务流程目录！"),
        RESOURCE_CODE_10000005("10000005", "业务流程目录不能拖动到API分组目录！"),
        ;

        public final String code;
        public final String message;

        ResourceCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static ResourceCode find(String code) {
            if (StringUtils.isNotBlank(code)) {
                for (ResourceCode value : ResourceCode.values()) {
                    if (code.equals(value.getCode())) {
                        return value;
                    }
                }
            }
            return null;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
