/*
 * @Description: 数据服务API
 * @Date: 2022-02-18
 */

// 发布状态
const statusMapping = {
  0: { name: '待发布', color: '#999' },
  1: { name: '未发布', color: '#ff4d4f' },
  2: { name: '已发布', color: '#52c41a' }
}

// API类型
const apiType = {
  0: '注册API',
  1: '数据源生成'
}

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
      prop: 'name'
    },
    {
      type: 'text',
      label: 'API Path',
      prop: 'path'
    },
    {
      type: 'text',
      label: 'API类型',
      prop: 'type',
      width: 350,
      formatter: ({ type }) => apiType[type]
    },
    // {
    //   type: 'text',
    //   label: '最新版本号',
    //   prop: 'apiVersion',
    //   width: 100
    // },
    {
      type: 'statusText',
      label: '发布状态',
      prop: 'publishStatus',
      width: 100,
      color: ({ row: { publishStatus: s } }) => statusMapping[s].color,
      formatter: ({ publishStatus: s }) => statusMapping[s].name
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
      type: 'operation',
      label: '操作',
      fixed: 'right',
      width: 150,
      operationList: [
        {
          func: that.handleAuthorizeClick,
          label: '授权'
        },
        {
          func: that.handleVersionClick,
          label: '版本列表'
        }
      ]
    }
  ]
})
