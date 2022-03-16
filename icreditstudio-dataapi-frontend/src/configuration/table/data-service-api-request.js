/*
 * @Description: 新增数据源-请求参数定义
 * @Date: 2022-02-25
 */

import { REGISTER_API_TYPR } from '@/config/constant'

export default that => ({
  refName: 'dataServiceApiRequest',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  emptySlot: true,
  appendSlot: true,
  group: [
    {
      type: 'editInput',
      label: '参数名称',
      prop: 'filedName',
      maxlength: 50,
      showWordLimit: true,
      resize: 'botn'
    },
    {
      type: 'select',
      label: '参数类型',
      prop: 'filedType',
      width: 150,
      options: REGISTER_API_TYPR
    },
    {
      type: 'slot',
      label: '是否必填',
      prop: 'required',
      width: 150
    },
    {
      type: 'editInput',
      label: '默认值',
      prop: 'defalutValue',
      maxlength: 100,
      showWordLimit: true
    },

    {
      type: 'editInput',
      label: '说明',
      prop: 'desc',
      maxlength: 100,
      showWordLimit: true
    },

    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      width: 80,

      operationList: [
        {
          func: params => that.handleDeleteRowClick(params, 'Request'),
          label: '删除'
        }
      ]
    }
  ]
})
