/*
 * @Description: 调用日志管理
 * @Date: 2022-03-03
 */
import { REQUEST_STATUS } from '@/config/constant'
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
    label: '应用名称',
    model: '',
    ruleProp: 'appName',
    isSearch: true
  },
  // {
  //   type: 'text',
  //   label: 'API版本',
  //   ruleProp: 'apiVersion',
  //   model: '',
  //   inline: true,
  //   isSearch: true
  // },

  {
    type: 'daterange',
    label: '调用时间',
    ruleProp: 'time',
    startPlaceholder: '开始日期',
    endPlaceholder: '结束日期',
    model: '',
    inline: true,
    isSearch: true,
    format: 'yyyy-MM-dd',
    valueFormat: 'yyyy-MM-dd'
  },
  {
    type: 'select',
    label: '请求状态',
    ruleProp: 'callStatus',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      ...objectConvertToArray(REQUEST_STATUS)
    ]
  }
]
