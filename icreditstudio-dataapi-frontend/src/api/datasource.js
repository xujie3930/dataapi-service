/*
 * @Description: 工作空间管理-接口URL
 * @Date: 2021-08-26
 */
import { postAction, getAction } from './index'

const demoAdd = params => postAction('/datasource/save', params)

const demoEdit = params => getAction('/datasource/save', params)

export default {
  demoAdd,
  demoEdit
}
