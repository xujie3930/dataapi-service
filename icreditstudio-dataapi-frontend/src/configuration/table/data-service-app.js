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
  customHeaderButton: true,
  group: [
    {
      type: 'selection',
      width: '80px'
    },
    {
      type: 'text',
      label: '名称',
      prop: 'name'
    },
    {
      type: 'text',
      label: 'ID',
      prop: 'path'
    },
    {
      type: 'text',
      label: '认证方式',
      prop: 'type',
      width: 350,
      formatter: ({ type }) => API_TYPE[type]
    },
    // {
    //   type: 'text',
    //   label: '最新版本号',
    //   prop: 'apiVersion',
    //   width: 100
    // },
    {
      type: 'statusText',
      label: '状态',
      prop: 'publishStatus',
      width: 100,
      color: ({ row: { publishStatus: s } }) => STATUS_MAPPING[s].color,
      formatter: ({ publishStatus: s }) => STATUS_MAPPING[s].name
    },
    {
      type: 'text',
      label: 'token有效期',
      prop: 'publishUser'
    },
    {
      type: 'text',
      label: '创建人',
      prop: 'publishUser',
      width: 80
    },
    {
      type: 'text',
      label: '创建时间',
      prop: 'publishUser',
      width: 80
    },
    {
      type: 'date',
      label: '备注',
      prop: 'publishTime',
      width: 180
    },
    {
      type: 'operation',
      label: '操作',
      fixed: 'right',
      width: 180,
      operationList: [
        // {
        //   func: that.handleAuthorizeClick,
        //   label: '授权'
        // },
        // {
        //   func: that.handleVersionClick,
        //   label: '版本列表'
        // }

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
