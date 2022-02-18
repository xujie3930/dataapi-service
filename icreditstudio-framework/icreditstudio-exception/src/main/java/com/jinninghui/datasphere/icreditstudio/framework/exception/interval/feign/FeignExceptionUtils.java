package com.jinninghui.datasphere.icreditstudio.framework.exception.interval.feign;

import com.alibaba.fastjson.JSONObject;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;

/**
 * 描述 ：解析FeignException工具类
 *
 * @author lidab
 * @date 2018/3/27.
 */
public class FeignExceptionUtils {

    /**
     * 解析FeignException异常信息
     *
     * @param e
     * @return
     */
    public static FeignExceptionDTO parseFeignException(Throwable e) {
        if (!(e instanceof FeignException)) {
            return null;
        }
        // 如果是 FeignExceptionDTO 异常，则是有上游服务返回的异常，需要解析该异常内容
        String subStr = StringUtils.substringBetween(e.getMessage(), "<span>", "</span>");
        if (StringUtils.isBlank(subStr)) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(subStr.replaceAll("&quot;", "\""), JSONObject.class);
        if (jsonObject == null) {
            return null;
        }

        FeignExceptionDTO feignExceptionDTO = new FeignExceptionDTO();
        feignExceptionDTO.setCode(jsonObject.get("code").toString());
        if (jsonObject.get("cause") != null) {
        	feignExceptionDTO.setCause(jsonObject.get("cause").toString());
		}
        return feignExceptionDTO;
    }
    
    
    /**
	 * 解析Exception:
	 * 如果是AppException，则返回其错误码；
	 * 如果是FeignException，则试图从其中解析出AppException的错误码，成功则返回该错误码
	 * 否则返回入参指定的默认错误码
	 * 此处添加@Logable便于记录异常信息
	 * @param e
	 * @return
	 */
//	@Logable(businessTag="parseException")
	public static String parseException(Exception e, String defaultErrorCode) {
		if(e instanceof AppException)
		{
			return ((AppException)e).getErrorCode();
		}
		FeignExceptionDTO dto = FeignExceptionUtils.parseFeignException(e);
		if (dto == null) {
			return defaultErrorCode;
		} else {
			if(dto.getCode().length()>5) {
				return defaultErrorCode;
			} else {
				return dto.getCode();
			}
		}
	}
}