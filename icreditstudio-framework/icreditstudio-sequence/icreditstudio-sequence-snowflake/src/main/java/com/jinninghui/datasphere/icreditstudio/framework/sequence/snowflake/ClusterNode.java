package com.jinninghui.datasphere.icreditstudio.framework.sequence.snowflake;

/**
 * ClassName: ClusterNode <br/>
 * Description: sequence服务分布式节点
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
public class ClusterNode {

    /**
     * 机房id
     */
    private int centerId;

    /**
     * 机器id
     */
    private int workId;

    public ClusterNode(int centerId, int workId) {
        this.centerId = centerId;
        this.workId = workId;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }
}
