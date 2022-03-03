/*
 * @Description: 日志详情参数配置
 * @Date: 2022-03-03
 */

import { ENABLED_STATUS, CERTIFICATION_TYPE } from '@/config/constant'
import { dateFormat } from '@/utils'

export const detailConfiguration = {
  base: [
    { label: 'API名称', value: '', key: 'generateId', span: 24 },
    {
      span: 24,
      label: 'API Path',
      value: '',
      key: 'certificationType',
      formatter: val => CERTIFICATION_TYPE[val]
    },
    {
      span: 24,
      label: '调用IP',
      value: '',
      key: 'isEnable',
      color: '',
      formatter: val => ENABLED_STATUS[val]
    },
    { span: 24, label: 'API版本号', value: '', key: 'name' },
    { span: 24, label: 'API类型', value: '', key: 'secretContent' },
    { span: 24, label: '协议', value: '', key: 'appGroupName' },
    { span: 24, label: '请求方式', value: '', key: 'period' },
    { span: 24, label: '返回类型', value: '', key: 'allowIp' },
    { span: 24, label: '调用参数', value: '', key: 'createBy' },
    { span: 24, label: '返回结果', value: '', key: 'createBy' },
    {
      span: 24,
      label: '调用开始时间',
      value: '',
      key: 'createTime',
      formatter: val => dateFormat(val)
    },
    { span: 24, label: '调用结束时间', value: '', key: 'desc' },
    { span: 24, label: '执行时长', value: '', key: 'desc' },
    { span: 24, label: '调用状态', value: '', key: 'desc' }
  ]
}

export const detailTitleKeyMapping = {
  base: null
}
