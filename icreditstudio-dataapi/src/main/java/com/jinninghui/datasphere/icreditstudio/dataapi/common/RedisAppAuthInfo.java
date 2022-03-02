package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisAppAuthInfo {

    private String apiId;
    private String appId;
    //有效起始时间(-1表示无穷)
    private Long periodBegin;
    //有效结束时间(-1表示无穷)
    private Long periodEnd;
    //允许调用次数(-1表示无穷)
    private Integer allowCall;
}
