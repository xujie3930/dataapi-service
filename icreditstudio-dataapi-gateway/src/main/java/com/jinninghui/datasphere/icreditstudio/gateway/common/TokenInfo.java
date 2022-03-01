package com.jinninghui.datasphere.icreditstudio.gateway.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {
    //token
    private String token;
    //开始时间
    private Long createTime;
    //有效时长,单位小时
    private Long period;
}
