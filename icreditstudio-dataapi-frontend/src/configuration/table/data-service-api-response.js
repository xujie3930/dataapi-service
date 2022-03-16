/*
 * @Description: 新增数据源-返回参数定义
 * @Date: 2022-02-25
 */

import { REGISTER_API_TYPR } from '@/config/constant'

export default that => ({
  refName: 'dataServiceApiResponse',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  emptySlot: true,
  appendSlot: true,
  group: [
    {
      type: 'editInput',
      label: '参数名称',
      prop: 'filedName'
    },
    {
      type: 'select',
      label: '参数类型',
      prop: 'filedType',
      width: 150,
      options: REGISTER_API_TYPR
    },

    {
      type: 'editInput',
      label: '默认值',
      prop: 'defalutValue'
    },

    {
      type: 'editInput',
      label: '说明',
      prop: 'desc'
    },

    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      width: 80,
      operationList: [
        {
          func: params => that.handleDeleteRowClick(params, 'Response'),
          label: '删除'
        }
      ]
    }
  ]
})
