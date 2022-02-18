package com.jinninghui.datasphere.icreditstudio.framework.result.base;

/**
 * @author liyanhui
 */
public class BusinessBaseTimes extends BusinessBaseIdentify {
    private static final long serialVersionUID = -1915432938815132522L;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后更新用户时间
     */
    private Long lastUpdateTime;

    public Long getCreateTime() {
        return createTime;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}