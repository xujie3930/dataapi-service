package com.jinninghui.datasphere.icreditstudio.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Peng
 */
@AllArgsConstructor
@Getter
public enum DialectEnum {
    MYSQL("mysql"),
    ORACLE("oracle"),
    ;
    private String dialect;
}
