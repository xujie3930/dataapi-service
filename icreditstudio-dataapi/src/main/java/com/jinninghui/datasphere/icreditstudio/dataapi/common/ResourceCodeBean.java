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

        RESOURCE_CODE_20000001("20000001", "API Path不能为空，只能由字母组成，长度为16！"),
        RESOURCE_CODE_20000002("20000002", "API 名称不能为空，只能由字母、数字、下划线组成，长度不能超过50！"),
        RESOURCE_CODE_20000003("20000003", "API 名称已存在！"),
        RESOURCE_CODE_20000004("20000004", "请先勾选返回参数！"),
        RESOURCE_CODE_20000005("20000005", "API Path已存在！"),
        RESOURCE_CODE_20000006("20000006", "sql语句不能使用【select *】的形式！"),
        RESOURCE_CODE_20000007("20000007", "请检查sql语句语法是否正确，或选择的数据源与sql语句中的表是否对应！"),
        RESOURCE_CODE_20000008("20000008", "sql语句不能为空！"),
        RESOURCE_CODE_20000009("20000009", "请选择API！"),
        RESOURCE_CODE_20000010("20000010", "无法匹配应用！"),


        RESOURCE_CODE_60000001("60000001", "获取资源库连接失败！"),
        RESOURCE_CODE_60000002("60000002", "数据源不明确，id为空！"),
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
