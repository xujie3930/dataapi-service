package com.jinninghui.datasphere.icreditstudio.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 时间区间，前闭后开
 * @author YangMeng
 *
 */
public class TimeInterval {
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
    Date startTime;
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
    Date endTime;
	@Override
	public String toString()
	{
		return JSON.toJSONString(this);
	}
	public TimeInterval()
	{
		startTime=null;
		endTime=null;
	}
	public TimeInterval(Date start, Date end)
	{
		if(start.getTime()>=end.getTime()) {
            throw new RuntimeException("create TimeInterval failed, endTime must later than start");
        }
		startTime = new Date(start.getTime());
		endTime = new Date(end.getTime());
	}
	
	/**
	 * 两个时间区间是否有重叠范围
	 * 注意因为前闭后开，所以[20180419 00:00:00,20180420 00:00:00)与[20180420 00:00:00,20180421 00:00:00)是不重叠的
	 * @param other
	 * @return
	 */
	public boolean overlapped(TimeInterval other)
	{
		if(this.endTime.getTime()<=other.getStartTime().getTime()) {
            return false;
        }
		if(other.getEndTime().getTime()<=this.getStartTime().getTime()) {
            return false;
        }
		return true;
	}
	
	public boolean contain(Date time)
	{
		return startTime.getTime()<=time.getTime() && endTime.getTime()>time.getTime();
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
