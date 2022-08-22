package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common;

import com.jinninghui.datasphere.icreditstudio.framework.systemcode.SystemCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyanhui
 */
@SystemCode
public class ResourceCodeBean {

    public enum ResourceCode {
        RESOURCE_CODE_10000000("10000000", "应用已停用!"),
        RESOURCE_CODE_10000001("10000001", "密钥验证失败!"),
        RESOURCE_CODE_10000002("10000002", "API尚未发布或者已删除!"),
        RESOURCE_CODE_10000003("10000003", "token失效，请重新获取!"),
        RESOURCE_CODE_10000004("10000004", "参数缺失!"),
        RESOURCE_CODE_10000005("10000005", "该应用未启用！"),
        RESOURCE_CODE_10000006("10000006", "该API未授权该应用!"),
        RESOURCE_CODE_10000007("10000007", "授权已到期!"),
        RESOURCE_CODE_10000008("10000008", "已达调用次数上限!"),
        RESOURCE_CODE_10000009("10000009", "应用未启用!"),
        RESOURCE_CODE_10000010("10000010", "应用IP不在白名单内!"),
        RESOURCE_CODE_10000011("10000011", "token失效，请重新获取!"),
        RESOURCE_CODE_10000012("10000012", "请求中缺失token!"),
        RESOURCE_CODE_10000013("10000013", "请求失败!"),
        RESOURCE_CODE_10000014("10000014", "授权时间未生效!"),
        RESOURCE_CODE_10000015("10000015", "授权时间已失效!"),
        RESOURCE_CODE_10000016("10000016", "系统异常，请稍后再次尝试!"),
        RESOURCE_CODE_10000017("10000017", "请求方式和API设置不匹配!"),
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
