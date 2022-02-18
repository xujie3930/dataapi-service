package com.jinninghui.datasphere.icreditstudio.framework.result.base;

/**
 * @author liyanhui
 */
public class BusinessBaseIdentify extends BaseObject {

    private static final long serialVersionUID = -2923446725609856732L;

    /**
     * 主键编号
     */
    private String id;

    /**
     * 创建用户ID
     */
    private String createUserId;

    /**
     * 最后更新用户ID
     */
    private String lastUpdateUserId;

    public String getId() {
        return id;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public String getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }
}

