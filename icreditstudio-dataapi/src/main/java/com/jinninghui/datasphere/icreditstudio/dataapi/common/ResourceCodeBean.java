package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import com.jinninghui.datasphere.icreditstudio.framework.systemcode.SystemCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyanhui
 */
@SystemCode
public class ResourceCodeBean {

    public enum ResourceCode {
        RESOURCE_CODE_00000001("00000001", "主键ID不能为空！"),
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
        RESOURCE_CODE_20000009("20000009", "请勾选API！"),
        RESOURCE_CODE_20000010("20000010", "无法匹配应用！"),
        RESOURCE_CODE_20000011("20000011", "应用分组名称不能为空，只能以英文字母或中文开头，并且只能包含英文、中文、数字，长度50以内！"),
        RESOURCE_CODE_20000013("20000013", "分组ID已存在！"),
        RESOURCE_CODE_20000014("20000014", "包含不规范字符，请重新输入"),
        RESOURCE_CODE_20000015("20000015", "请输入以英文字母或者汉字开头的2~50字的名称"),
        RESOURCE_CODE_20000016("20000016", "应用名称不能为空，只能包含英文、中文及数字，50字以内"),
        RESOURCE_CODE_20000017("20000017", "业务描述250字以内"),
        RESOURCE_CODE_20000018("20000018", "名称不能为空"),
        RESOURCE_CODE_20000019("20000019", "API详情id不能为空"),
        RESOURCE_CODE_20000020("20000020", "应用分组id不能为空"),
        RESOURCE_CODE_20000021("20000021", "应用id不能为空"),
        RESOURCE_CODE_20000022("20000022", "应用名称不能为空"),
        RESOURCE_CODE_20000023("20000023", "应用名称50字以内"),
        RESOURCE_CODE_20000024("20000024", "应用分组id不能为空"),
        RESOURCE_CODE_20000025("20000025", "应用分组名称不能为空"),
        RESOURCE_CODE_20000026("20000026", "请输入以英文字母或者汉字开头的50字的名称"),
        RESOURCE_CODE_20000027("20000027", "备注输入250字以内"),
        RESOURCE_CODE_20000028("20000028", "API分组id不能为空"),
        RESOURCE_CODE_20000029("20000029", "业务流程id不能为空!"),
        RESOURCE_CODE_20000030("20000030", "请输入以英文字母或者汉字开头的2~50字的分组名称"),
        RESOURCE_CODE_20000031("20000031", "分组描述200字以内"),
        RESOURCE_CODE_20000032("20000032", "密钥只能包含数字、英文字母且必须包含数字和英文字母，长度为16"),
        RESOURCE_CODE_20000033("20000033", "描述文字必须在250以内"),
        RESOURCE_CODE_20000034("20000034", "IP白名单只能包含【数字】、【.】、【,】，且长度不能超过255"),
        RESOURCE_CODE_20000035("20000035", "IP白名单中有不规范的IP地址"),
        RESOURCE_CODE_20000036("20000036", "调用次数不能输入负数"),
        RESOURCE_CODE_20000037("20000037", "该业务流程下有已发布的api，不能删除,请先下线完该业务流程下的全部API后再进行操作！"),
        RESOURCE_CODE_20000038("20000038", "该api分组下有已发布的api，不能删除,请先下线完该api分组下的全部API后再进行操作！"),
        RESOURCE_CODE_20000039("20000039", "默认业务流程不能删除！"),
        RESOURCE_CODE_20000040("20000040", "默认API分组不能删除！"),
        RESOURCE_CODE_20000041("20000041", "sql中的where条件只支持id = #{id}的形式！"),
        RESOURCE_CODE_20000042("20000042", "后台服务Host不能为空，且只能以“http://”开头，后面为ip地址！"),
        RESOURCE_CODE_20000043("20000043", "后台Path不能为空，且只支持【英文、数字、-、_】！"),
        RESOURCE_CODE_20000044("20000044", "字段名只能由英文、中文构成，长度在50以内！"),
        RESOURCE_CODE_20000045("20000045", "字段默认值长度不能超过100！"),
        RESOURCE_CODE_20000046("20000046", "字段描述长度不能超过100！"),
        RESOURCE_CODE_20000047("20000047", "该应用分组名称已存在！"),
        RESOURCE_CODE_20000048("20000048", "请至少选择一个应用分组进行删除！"),
        RESOURCE_CODE_20000049("20000049", "该分组下包含启用状态的应用，请停用后再进行删除！"),

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
