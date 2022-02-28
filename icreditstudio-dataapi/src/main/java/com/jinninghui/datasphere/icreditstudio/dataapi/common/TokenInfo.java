package com.jinninghui.datasphere.icreditstudio.dataapi.common;

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
