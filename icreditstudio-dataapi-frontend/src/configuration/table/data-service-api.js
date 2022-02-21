/*
 * @Description: 数据服务API
 * @Date: 2022-02-18
 */

export default that => ({
  refName: 'dataServiceApi',
  isBorder: true,
  isStripe: true,
  hasPage: true,
  customBtnConfig: [
    {
      label: '新增API',
      type: 'primary',
      key: 'addDataSource',
      isHide: false,
      options: {
        eventType: 'click',
        eventName: 'handleAddDataServiceApi'
      }
    }
  ],
  group: [
    {
      type: 'index',
      label: '序号',
      width: '80px'
    },
    {
      type: 'text',
      label: 'API名称',
      prop: 'type'
    },
    {
      type: 'text',
      label: 'API Path',
      prop: 'name',
      width: 200
    },
    {
      type: 'text',
      label: 'API类型',
      prop: 'uri',
      width: 350
    },
    {
      type: 'text',
      label: '最新版本号',
      prop: 'status',
      width: 100
    },
    {
      type: 'statusText',
      label: '发布状态',
      prop: 'status',
      width: 100,
      color: ({ row: { status } }) => {
        return status === 1 ? '#52c41a' : status === 2 ? '#ff4d4f' : '#999'
      },
      formatter: ({ status }) => {
        return status === 1 ? '已授权' : status === 2 ? '未发布' : '草稿'
      }
    },
    {
      type: 'text',
      label: '发布人',
      prop: 'admin',
      width: 80
    },
    {
      type: 'text',
      label: '发布时间',
      prop: 'lastSyncTime',
      width: 180
    },
    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      width: 220,
      operationList: [
        {
          func: that.mixinHandleDelete,
          label: '停止发布',
          visible: ({ row }) => row.status === 1
        },

        {
          func: that.mixinHandleDelete,
          label: '授权'
        },
        {
          func: that.mixinHandleDelete,
          label: '发布',
          visible: ({ row }) => row.status === 2
        },
        {
          func: that.mixinHandleDelete,
          label: '编辑'
        },
        {
          func: that.mixinHandleDelete,
          label: '历史版本',
          visible: ({ row }) => row.status === 2
        },
        {
          func: that.mixinHandleDelete,
          label: '详情'
        }
      ]
    }
  ]
})
