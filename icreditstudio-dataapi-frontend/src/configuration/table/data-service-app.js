/*
 * @Description: 数据服务API
 * @Date: 2022-02-18
 */

import { CERTIFICATION_TYPE, TOEKN_PERIOD } from '@/config/constant'

export default that => ({
  refName: 'dataServiceApi',
  id: 'dataServiceApi',
  isBorder: true,
  isStripe: true,
  hasPage: false,
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
      prop: 'generateId',
      width: 150
    },
    {
      type: 'text',
      label: '认证方式',
      prop: 'type',
      width: 100,
      formatter: ({ certificationType: t }) => CERTIFICATION_TYPE[t]
    },
    {
      type: 'statusText',
      label: '状态',
      prop: 'publishStatus',
      width: 100
      // color: ({ row: { publishStatus: s } }) => STATUS_MAPPING[s].color,
      // formatter: ({ publishStatus: s }) => STATUS_MAPPING[s].name
    },
    {
      type: 'text',
      label: 'token有效期',
      prop: 'period',
      width: 120,
      formatter: ({ period }) => TOEKN_PERIOD[period]
    },
    {
      type: 'text',
      label: '创建人',
      prop: 'createBy',
      width: 100
    },
    {
      type: 'date',
      label: '创建时间',
      prop: 'createTime',
      width: 180
    },
    {
      type: 'text',
      label: '备注',
      prop: 'desc'
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
