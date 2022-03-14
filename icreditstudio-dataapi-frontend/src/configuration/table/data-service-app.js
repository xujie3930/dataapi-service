/*
 * @Description: 数据服务API
 * @Date: 2022-02-18
 */

import {
  CERTIFICATION_TYPE,
  TOEKN_PERIOD,
  ENABLED_STATUS
} from '@/config/constant'

export default that => ({
  refName: 'dataServiceAppName',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  indent: 0,
  customHeaderButton: true,
  expandRowKeys: that.expandRowKeys,
  group: [
    {
      type: 'index',
      width: '80px',
      label: '序号'
    },
    {
      type: 'text',
      label: '名称',
      prop: 'name',
      align: 'left',
      className: 'name-column'
    },
    {
      type: 'text',
      label: 'ID',
      prop: 'generateId',
      width: 160
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
      prop: 'isEnable',
      width: 100,
      color: ({ row: { isEnable: e } }) => ENABLED_STATUS[e]?.color,
      formatter: ({ isEnable: e }) => ENABLED_STATUS[e]?.name
    },
    {
      type: 'text',
      label: 'token有效期',
      prop: 'tokenType',
      width: 120,
      formatter: ({ tokenType }) => TOEKN_PERIOD[tokenType]
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
      width: 250,
      operationList: [
        // 停用状态
        {
          func: that.handleAuthorizeClick,
          label: '启用',
          visible: ({ row }) => row.isEnable === 0
        },
        {
          func: that.handleAuthorizeClick,
          label: '编辑',
          visible: ({ row }) => row.isEnable === 0
        },
        {
          func: that.handleAuthorizeClick,
          label: '删除',
          visible: ({ row }) => row.isEnable === 0
        },

        // 启用状态
        {
          func: that.handleAuthorizeClick,
          label: '停用',
          visible: ({ row }) => row.isEnable
        },
        {
          func: that.handleAuthorizeClick,
          label: '授权',
          visible: ({ row }) => row.isEnable
        },
        {
          func: that.handleDetailClick,
          label: '详情',
          visible: ({ row }) => row.isEnable
        },

        // 一级数据
        {
          func: that.handleAddAppClick,
          label: '新增应用',
          visible: ({ row }) => 'children' in row
        },
        {
          func: that.handleAddAppClick,
          label: '编辑分组',
          visible: ({ row }) => 'children' in row
        },
        {
          func: that.handleAddAppClick,
          label: '删除分组',
          visible: ({ row }) => 'children' in row
        }
      ]
    }
  ]
})
