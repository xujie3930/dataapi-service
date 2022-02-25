/*
 * @Author: lizheng
 * @Description: 新增数据源-返回参数定义
 * @Date: 2022-02-25
 */

export default that => ({
  refName: 'dataServiceApiRequest',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  emptySlot: true,
  group: [
    {
      type: 'text',
      label: '参数名称',
      prop: 'name'
    },
    {
      type: 'text',
      label: '参数类型',
      prop: 'path'
    },
    {
      type: 'text',
      label: '是否必填',
      prop: 'type'
    },
    {
      type: 'text',
      label: '默认值'
    },

    {
      type: 'text',
      label: '说明',
      prop: 'publishUser'
    },

    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      operationList: [
        // {
        //   func: that.mixinHandleDelete,
        //   label: '历史版本'
        // }
        {
          func: that.mixinHandleDelete,
          label: '详情'
        }
      ]
    }
  ]
})
