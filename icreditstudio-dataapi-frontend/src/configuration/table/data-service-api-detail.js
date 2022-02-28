/*
 * @Author: lizheng
 * @Description: 详情
 * @Date: 2022-02-26
 */

export default {
  refName: 'dataServiceApiDetail',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  maxHeight: 720,
  group: [
    {
      type: 'text',
      label: '设置为返回参数',
      prop: 'isResponse',
      width: 130
    },
    {
      type: 'text',
      label: '设置为请求参数',
      prop: 'isRequest',
      width: 130
    },
    {
      type: 'text',
      label: '字段名称',
      prop: 'fieldName',
      formatter: ({ fieldName }) => fieldName?.toLocaleLowerCase() ?? ''
    },

    {
      type: 'text',
      label: '字段类型',
      prop: 'fieldType',
      width: 100
    },
    {
      type: 'text',
      label: '是否必填',
      prop: 'required',
      width: 100
    },
    {
      type: 'text',
      label: '说明',
      prop: 'desc'
    }
  ]
}
