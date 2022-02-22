/*
 * @Description: 工作空间管理-接口URL
 * @Date: 2021-08-26
 */
import { postAction } from './index'

// 新增业务流程
const addBusinessProcess = params => postAction('/workFlow/save', params)

// 新增API分组
const addApiGroup = params => postAction('/apiGroup/save', params)

// 获取业务流程列表
const getBusinessProcess = params => postAction('/workFlow/list', params)

// 获取API分组列表
const getBusinessProcessChild = params => postAction('/apiGroup/list', params)

// 搜索业务流程以及API分组
const searchProcessOrGroup = params => postAction('/search', params)

export default {
  addBusinessProcess,
  addApiGroup,
  getBusinessProcess,
  getBusinessProcessChild,
  searchProcessOrGroup
}
