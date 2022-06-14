/*
 * @Description: 授权API列表
 * @Date: 2022-06-07
 */

import { interfaceSource_TYPE } from '@/config/constant'
import { dateFormat } from '@/utils'

export default that => ({
  refName: 'appAuthList',
  id: 'appAuthListId',
  isBorder: true,
  isStripe: true,
  hasPage: true,
  rowKey: 'apiId',
  group: [
    {
      type: 'selection',
      width: '80px',
      prop: 'selection'
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
      width: 200
    },
    {
      type: 'text',
      label: 'API来源',
      width: 120,
      prop: 'apiInterfaceSource',
      formatter: ({ apiInterfaceSource }) =>
        interfaceSource_TYPE[apiInterfaceSource]
    },
    {
      type: 'text',
      label: '授权有效期',
      prop: 'authPeriodBegin',
      width: 120,
      formatter: ({ authPeriodBegin }) =>
        authPeriodBegin < 0 ? '永久' : '短期'
    },
    {
      type: 'text',
      label: '有效时间范围',
      prop: 'authPeriodBegin',
      width: 300,
      formatter: ({ authPeriodBegin, authPeroidEnd }) =>
        authPeriodBegin < 0
          ? ''
          : `${dateFormat(authPeriodBegin, 'yyyy-MM-dd hh:mm')} ~ ${dateFormat(
              authPeroidEnd,
              'yyyy-MM-dd hh:mm'
            )}`
    },
    {
      type: 'text',
      label: '可调用次数',
      prop: 'authAllowCall',
      width: 100,
      formatter: ({ authAllowCall }) =>
        authAllowCall < 0 ? '无限次' : '有限次'
    },
    {
      type: 'text',
      label: '调用次数',
      prop: 'authAllowCall',
      width: 150,
      formatter: ({ authAllowCall }) => (authAllowCall < 0 ? '' : authAllowCall)
    },

    {
      type: 'operation',
      label: '操作',
      // fixed: 'right',
      width: 130,
      operationList: [
        {
          func: that.handleConfigureClick,
          label: '配置',
          disabled: ({ row: { apiInterfaceSource } }) => !!apiInterfaceSource
        },
        {
          func: that.handleDeleteClick,
          label: '删除'
        }
      ]
    }
  ]
})
