/*
 * @Description: 调用日志
 * @Date: 2022-03-03
 */

import { postAction } from './index'

// 获取调用日志详情
const getLogDetail = params => postAction('/log/detail', params)

export default {
  getLogDetail
}
