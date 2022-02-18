package com.jinninghui.datasphere.icreditstudio.framework.utils;

import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;


public class FeignExceptionCode {
	public static String getFeignExceptionCode(Throwable e) {
		if (e == null) {
			return null;
		}
		if (e instanceof FeignException) {
			String subStr = StringUtils.substringBetween(e.getMessage(), "<span>", "</span>");
			if (StringUtils.isNotBlank(subStr)) {
				JSONObject jsonObject = JSONObject.parseObject(subStr.replaceAll("&quot;", "\""), JSONObject.class);
				return jsonObject.get("code").toString();
			}
		}
		return null;
	}
}
