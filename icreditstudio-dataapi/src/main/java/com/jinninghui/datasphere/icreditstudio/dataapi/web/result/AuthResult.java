package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class AuthResult {

    private Integer tokenType;

    private Long periodBegin;

    private Long periodEnd;

    private Integer callCountType;

    private Integer allowCall;

    private Integer authEffectiveTime;

}
