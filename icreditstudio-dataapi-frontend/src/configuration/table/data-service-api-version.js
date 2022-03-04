/*
 * @Description: 版本列表
 * @Date: 2022-02-26
 */

// 发布状态
const statusMapping = {
  0: { name: '待发布', color: '#999' },
  1: { name: '未发布', color: '#ff4d4f' },
  2: { name: '已发布', color: '#52c41a' }
}

export default that => ({
  refName: 'dataServiceApiVersion',
  isBorder: true,
  isStripe: true,
  hasPage: true,
  customBtnConfig: [
    {
      label: '批量删除',
      type: 'primary',
      isHide: false,
      options: {
        eventType: 'click',
        eventName: 'handleBatchDelete'
      }
    }
  ],
  group: [
    {
      type: 'selection',
      width: '80px'
    },
    {
      type: 'text',
      label: '版本',
      prop: 'name'
    },
    {
      type: 'text',
      label: 'API名称',
      prop: 'name'
    },
    {
      type: 'text',
      label: '发布人',
      prop: 'publishUser',
      width: 80
    },
    {
      type: 'date',
      label: '发布时间',
      prop: 'publishTime',
      width: 180
    },
    {
      type: 'statusText',
      label: '发布状态',
      prop: 'publishStatus',
      width: 100,
      color: ({ row: { publishStatus: s } }) => statusMapping[s].color,
      formatter: ({ publishStatus: s }) => statusMapping[s].name
    },

    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      align: 'left',
      width: 200,
      operationList: [
        {
          func: that.mixinHandleDelete,
          label: '编辑'
        },
        {
          func: that.mixinHandleDelete,
          label: '删除'
        },
        {
          func: that.handleVersionDetailClick,
          label: '详情'
        },
        {
          func: that.mixinHandleDelete,
          label: '停止发布',
          visible: ({ row }) => row.publishStatus === 2
        },
        {
          func: that.mixinHandleDelete,
          label: '发布',
          visible: ({ row }) => row.publishStatus !== 2
        }
      ]
    }
  ]
})