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

import { interfaceSource_TYPE } from '@/config/constant'

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
      prop: 'apiVersion',
      width: 70,
      formatter: ({ apiVersion }) => `v${apiVersion}`
    },
    {
      type: 'text',
      label: 'API名称',
      prop: 'name'
    },
    {
      type: 'text',
      label: '接口来源',
      prop: 'interfaceSource',
      width: 90,
      formatter: ({ interfaceSource }) => interfaceSource_TYPE[interfaceSource]
    },
    {
      type: 'text',
      label: '发布人',
      prop: 'publishUser',
      width: 100
    },
    {
      type: 'date',
      label: '发布日期',
      prop: 'publishTime',
      width: 180
    },
    {
      type: 'statusText',
      label: '状态',
      prop: 'publishStatus',
      width: 80,
      color: ({ row: { publishStatus: s } }) => statusMapping[s].color,
      formatter: ({ publishStatus: s }) => statusMapping[s].name
    },

    {
      type: 'operation',
      label: '操作',
      // fixed: 'right',
      align: 'left',
      width: 200,
      operationList: [
        {
          func: that.handleEditApiClick,
          label: '编辑',
          visible: ({ row }) => row.interfaceSource === 0
        },
        {
          func: that.handleDeleteApiClick,
          label: '删除',
          visible: ({ row }) => row.interfaceSource === 0
        },
        {
          func: that.handleApiDetailClick,
          label: '详情',
          visible: ({ row }) => row.publishStatus !== 0
        },
        {
          func: that.handleUpdateStatusClick,
          label: '停止发布',
          visible: ({ row }) => row.publishStatus === 2
        },
        {
          func: that.handleUpdateStatusClick,
          label: '发布',
          visible: ({ row }) => row.publishStatus !== 2
        }
      ]
    }
  ]
})
