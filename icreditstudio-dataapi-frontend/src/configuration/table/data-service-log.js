/*
 * @Description:调用日志管理
 * @Date: 2022-03-03
 */

import { REQUEST_STATUS } from '@/config/constant'

export default that => ({
  refName: 'logManagement',
  id: 'logManagement',
  isBorder: true,
  isStripe: true,
  hasPage: true,
  group: [
    {
      type: 'index',
      label: '序号',
      width: 60
    },
    {
      type: 'text',
      label: 'API名称',
      prop: 'apiName'
    },
    {
      type: 'text',
      label: 'API Path',
      prop: 'apiPath',
      width: 160
    },
    {
      type: 'text',
      label: '应用名称',
      prop: 'appName'
    },
    {
      type: 'statusText',
      label: '调用IP',
      prop: 'callIp'
    },
    {
      type: 'text',
      label: 'API版本',
      prop: 'apiVersion',
      width: 100,
      formatter: ({ apiVersion }) => (apiVersion ? `v${apiVersion}` : '')
    },
    {
      type: 'date',
      label: '调用开始时间',
      prop: 'callBeginTime',
      width: 200
    },
    {
      type: 'time',
      label: '执行时长（ms）',
      prop: 'runTime',
      width: 200
    },
    {
      type: 'statusText',
      label: '请求状态',
      prop: 'callStatus',
      width: 120,
      color: ({ row: { callStatus: c } }) => REQUEST_STATUS[c].color,
      formatter: ({ callStatus: c }) => REQUEST_STATUS[c].name
    },
    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      width: 130,
      operationList: [
        {
          func: that.handleDetailClick,
          label: '查看日志'
        }
      ]
    }
  ]
})
