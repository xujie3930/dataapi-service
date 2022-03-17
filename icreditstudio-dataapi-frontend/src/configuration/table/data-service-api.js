/*
 * @Description: 数据服务API
 * @Date: 2022-02-18
 */

import { API_TYPE, STATUS_MAPPING } from '@/config/constant'

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
      prop: 'name',
      minWidth: 220
    },
    {
      type: 'text',
      label: 'API Path',
      prop: 'path',
      width: 220
    },
    {
      type: 'text',
      label: 'API类型',
      prop: 'type',
      width: 160,
      formatter: ({ type }) => API_TYPE[type]
    },
    {
      type: 'text',
      label: '最新版本号',
      prop: 'apiVersion',
      width: 100
    },
    {
      type: 'statusText',
      label: '发布状态',
      prop: 'publishStatus',
      width: 100,
      color: ({ row: { publishStatus: s } }) => STATUS_MAPPING[s].color,
      formatter: ({ publishStatus: s }) => STATUS_MAPPING[s].name
    },
    {
      type: 'text',
      label: '发布人',
      prop: 'publishUser',
      width: 100
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
      width: 200,
      operationList: [
        // {
        //   func: that.handleAuthorizeClick,
        //   label: '授权'
        // },
        {
          func: that.handleVersionClick,
          label: '历史版本'
        },

        {
          func: that.handleUpdateStatusClick,
          label: '发布',
          visible: ({ row: { publishStatus } }) => publishStatus !== 2
        },
        {
          func: that.handleUpdateStatusClick,
          label: '停止发布',
          visible: ({ row: { publishStatus } }) => publishStatus === 2
        },
        {
          func: that.handleDetailClick,
          label: '详情'
        }
      ]
    }
  ]
})
