/*
 * @Description: 应用管理接口API
 * @Date: 2022-03-01
 */

import { postAction, getAction } from './index'

// 获取应用分组列表
const getAppGroupList = params => postAction('/appGroup/list', params)

// 获取分组ID
const getAppgGroupUniqueId = params =>
  postAction('/appGroup/generateId', params)

// 获取应用ID
const getAppUniqueId = params => getAction('/generate/random', params)

// 新增应用分组
const addAppGroup = params => postAction('/appGroup/save', params)

// 新增应用
const addApp = params => postAction('/app/save', params)

// 校验分组名称是否唯一
const checkNameUniqueness = params =>
  postAction('/appGroup/checkAppGroupName', params)

export default {
  getAppGroupList,
  getAppUniqueId,
  getAppgGroupUniqueId,
  addAppGroup,
  addApp,
  checkNameUniqueness
}
