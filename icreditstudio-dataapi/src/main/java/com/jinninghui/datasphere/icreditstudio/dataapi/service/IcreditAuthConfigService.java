package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

/**
 * <p>
 * 授权配置 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAuthConfigService extends IService<IcreditAuthConfigEntity> {

    /**
     * 通过主键批量删除
     * @author  maoc
     * @create  2022/6/2 18:17
     * @desc
     **/
    int deleteByIds(Collection<String> ids);

    int updateByIds(IcreditAuthConfigEntity entity, Collection<String> ids);
}
