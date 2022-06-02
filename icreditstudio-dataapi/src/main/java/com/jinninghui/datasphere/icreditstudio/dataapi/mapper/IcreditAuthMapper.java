package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthListResult;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 授权表 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAuthMapper extends BaseMapper<IcreditAuthEntity> {

    List<IcreditAuthEntity> findByAppId(@Param("appId") String appId);
    List<IcreditAuthEntity> findByAppIds(@Param("appIds") Collection<String> appIds);
    List<IcreditAuthEntity> findByApiId(@Param("apiId") String appId);

    void removeByAppId(@Param("appId") String appId);
    void removeByApiId(@Param("apiId") String apiId);

    Long getApiAuthCount(@Param("delFlag") Integer delFlag);

    Long getAppAuthCount(@Param("delFlag") Integer code);

    List<AuthListResult> getListByAppId(AuthListRequest request);
    List<AuthListResult> getApiAuthList(AuthListRequest request);

    /**
     * 批量插入
     * @author  maoc
     * @create  2022/6/2 16:26
     * @desc
     **/
    int batchInsert(@Param("list") List<IcreditAuthEntity> list);

    int deletes(final Map<String, Object> paramsMap);

    Map<String, Object> getAuthNumByConfigIds(@Param("configIds")Collection<String> configIds);

}
