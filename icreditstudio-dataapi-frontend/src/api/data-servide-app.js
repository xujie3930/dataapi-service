/*
 * @Description: 应用管理接口API
 * @Date: 2022-03-01
 */

import { postAction } from './index'

// 获取应用分组列表
const getAppGroupList = params => postAction('/appGroup/list', params)

// 获取分组ID以及应用ID
const getAppUniqueId = params => postAction('/appGroup/generateId', params)

// 新增应用分组
const addAppGroup = params => postAction('/appGroup/save', params)

// 校验分组名称是否唯一
const checkNameUniqueness = params =>
  postAction('/appGroup/checkAppGroupName', params)

export default {
  getAppGroupList,
  getAppUniqueId,
  addAppGroup,
  checkNameUniqueness
}
