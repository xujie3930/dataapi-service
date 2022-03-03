/*
 * @Description: 日志详情参数配置
 * @Date: 2022-03-03
 */

import { API_TYPE, REQUEST_STATUS } from '@/config/constant'
import { dateFormat } from '@/utils'

export const detailConfiguration = row => ({
  base: [
    { span: 24, label: 'API名称', value: '', key: 'apiName' },
    { span: 24, label: 'API Path', value: '', key: 'apiPath' },
    { span: 24, label: '调用IP', value: '', key: 'callIp' },
    {
      span: 24,
      label: 'API版本号',
      value: '',
      key: 'apiVersion',
      formatter: val => `v${val}`
    },
    {
      span: 24,
      label: 'API类型',
      value: '',
      key: 'apiType',
      formatter: val => API_TYPE[val]
    },
    { span: 24, label: '协议', value: '', key: 'requestProtocol' },
    { span: 24, label: '请求方式', value: '', key: 'requestType' },
    { span: 24, label: '返回类型', value: '', key: 'responseType' },
    { span: 24, label: '调用参数', value: '', key: 'requestParam' },
    { span: 24, label: '返回结果', value: '', key: 'responseParam' },
    {
      span: 24,
      label: '调用开始时间',
      value: '',
      key: 'callBeginTime',
      formatter: val => dateFormat(val)
    },
    {
      span: 24,
      label: '调用结束时间',
      value: '',
      key: 'callEndTime',
      formatter: val => dateFormat(val)
    },
    {
      span: 24,
      label: '执行时长',
      value: '',
      key: 'runTime',
      formatter: val => `${val}ms`
    },
    {
      span: 24,
      label: '调用状态',
      value: '',
      key: 'callStatus',
      color: '',
      formatter: val => REQUEST_STATUS[val]
    },
    {
      span: 24,
      label: '异常日志',
      value: '',
      key: 'exceptionDetail',
      hide: row?.callStatus === 2
    }
  ]
})

export const detailTitleKeyMapping = {
  base: null
}
