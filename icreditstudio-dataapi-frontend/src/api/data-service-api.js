/*
 * @Description: 工作空间管理-接口URL
 * @Date: 2021-08-26
 */
import { postAction } from './index'

// 新增业务流程
const addBusinessProcess = params => postAction('/workFlow/save', params)

// 新增API
const addApiInfo = params => postAction('/apiBase/datasourceApi/create', params)

// 新增API分组
const addApiGroup = params => postAction('/apiGroup/save', params)

// 获取业务流程列表
const getBusinessProcess = params => postAction('/workFlow/list', params)

// 获取API分组列表
const getBusinessProcessChild = params => postAction('/apiGroup/list', params)

// 获取某个分组下的API下的列表
const getApiGroupList = params => postAction('/apiBase/list', params)

// 获取数据源名称Options
const getDatasourceOptions = params =>
  postAction('/apiBase/getDatasourceListByType', params)

// 获取数据表名称Options
const getDataTableOptions = params =>
  postAction('/apiBase/getTableByDatasourceId', params)

// 获取数据表字段
const getDataTableFields = params =>
  postAction('/apiBase/getTableFieldList', params)

// 获取API Path
const getDataApiPath = params => postAction('/apiBase/generate/apiPath', params)

// 获取某个API详情
const getDataApiDetail = params => postAction('/apiBase/detail', params)

// 搜索业务流程以及API分组
const searchProcessOrGroup = params => postAction('/search', params)

// 更新某个API的状态：停止发布 <=> 发布
const updateDataApiStatus = params =>
  postAction('/apiBase/publishOrStop', params)

export default {
  addBusinessProcess,
  addApiInfo,
  addApiGroup,
  getBusinessProcess,
  getBusinessProcessChild,
  getApiGroupList,
  getDatasourceOptions,
  getDataTableOptions,
  getDataTableFields,
  getDataApiPath,
  getDataApiDetail,
  searchProcessOrGroup,
  updateDataApiStatus
}