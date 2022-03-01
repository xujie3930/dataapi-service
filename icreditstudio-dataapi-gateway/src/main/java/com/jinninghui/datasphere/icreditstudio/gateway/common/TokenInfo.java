package com.jinninghui.datasphere.icreditstudio.gateway.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {
    //token
    private String token;
    //有效期时间戳
    private Long period;
}
