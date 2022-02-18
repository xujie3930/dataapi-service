package com.jinninghui.datasphere.icreditstudio.framework.result.base;

/**
 * @author liyanhui
 */
public class BusinessBasePeriod extends BusinessBaseIdentify {
    private static final long serialVersionUID = 4257604668089816087L;

    /**
     * 创建时间-开始
     */
    private Long createBeginTime;

    /**
     * 创建时间-结束
     */
    private Long createEndTime;

    /**
     * 最后更新时间-开始
     */
    private Long lastUpdateBeginTime;

    /**
     * 最后更新时间-结束
     */
    private Long lastUpdateEndTime;

    public Long getCreateBeginTime() {
        return createBeginTime;
    }

    public Long getCreateEndTime() {
        return createEndTime;
    }

    public Long getLastUpdateBeginTime() {
        return lastUpdateBeginTime;
    }

    public Long getLastUpdateEndTime() {
        return lastUpdateEndTime;
    }

    public void setCreateBeginTime(Long createBeginTime) {
        this.createBeginTime = createBeginTime;
    }

    public void setCreateEndTime(Long createEndTime) {
        this.createEndTime = createEndTime;
    }

    public void setLastUpdateBeginTime(Long lastUpdateBeginTime) {
        this.lastUpdateBeginTime = lastUpdateBeginTime;
    }

    public void setLastUpdateEndTime(Long lastUpdateEndTime) {
        this.lastUpdateEndTime = lastUpdateEndTime;
    }
}
