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

// 获取应用详情
const getAppDetail = params => postAction('/app/detail', params)

// 校验分组名称是否唯一
const checkNameUniqueness = params =>
  postAction('/appGroup/checkAppGroupName', params)

// 更新-对API进行授权
const updateApiAuthorization = params => postAction('/auth/save', params)

// 获取业务流程
const getBusinessPcoessList = params =>
  postAction('/workFlow/getBusinessProcessList', params)

// 获取分组信息
const getGroupList = params =>
  postAction('/apiGroup/getGroupListByWorkFlowId', params)

// 获取api信息
const getApiInfoList = params => postAction('/apiBase/getApiList', params)

// 获取授权详情
const getAppAuthDetail = params => postAction('/auth/info', params)

// 获取App编辑信息详情
const getAppEditDetail = params => postAction('/app/updatePageInfo', params)

// 编辑应用分组
const editAppGroup = params => postAction('/appGroup/rename', params)

// 删除或批量删除应用、分组
const deleteAppGroup = params => postAction('/appGroup/del', params)

// 启用或停用应用
const enableOrStopApp = params => postAction('/app/enableOrStop', params)

// 删除或批量删除授权API
const deleteAuthApi = params => postAction('/auth/del', params)

// 配置或批量配置授权API
const deployAuthApi = params => postAction('/auth/config', params)

export default {
  getAppGroupList,
  getAppUniqueId,
  getAppDetail,
  getAppgGroupUniqueId,
  getBusinessPcoessList,
  getGroupList,
  getApiInfoList,
  getAppEditDetail,
  addAppGroup,
  addApp,
  checkNameUniqueness,
  updateApiAuthorization,
  getAppAuthDetail,
  editAppGroup,
  deleteAppGroup,
  enableOrStopApp,
  deleteAuthApi,
  deployAuthApi
}
