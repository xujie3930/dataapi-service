/*
 * @Description: 授权API列表
 * @Date: 2022-06-07
 */

import { APP_AUTH_PERIOD, CALL_TYPE } from '@/config/constant'
import { objectConvertToArray } from '@/utils'

export default [
  {
    type: 'text',
    label: 'API名称',
    model: '',
    ruleProp: 'apiName',
    isSearch: true
  },

  {
    type: 'text',
    label: 'API Path',
    model: '',
    ruleProp: 'apiPath',
    isSearch: true
  },

  {
    type: 'select',
    label: '授权有效期',
    ruleProp: 'periodType',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      ...objectConvertToArray(APP_AUTH_PERIOD)
    ]
  },

  {
    type: 'select',
    label: '可调用次数',
    ruleProp: 'durationType',
    model: '',
    inline: true,
    isSearch: true,
    options: [{ value: '', label: '全部' }, ...objectConvertToArray(CALL_TYPE)]
  },

  {
    type: 'datetimerange',
    label: '有效范围时间',
    ruleProp: 'validTime',
    startPlaceholder: '开始日期',
    endPlaceholder: '结束日期',
    model: '',
    inline: true,
    isSearch: true,
    format: 'yyyy-MM-dd HH:mm',
    valueFormat: 'timestamp'
  }
]
