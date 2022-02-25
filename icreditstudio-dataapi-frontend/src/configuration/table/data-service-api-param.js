/*
 * @Author: lizheng
 * @Description: 数据源生成-选择参数
 * @Date: 2022-02-24
 */

export default {
  refName: 'dataServiceApiParam',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  maxHeight: 720,
  group: [
    {
      type: 'slot',
      label: '设置为返回参数',
      prop: 'isResponse',
      width: 150
    },
    {
      type: 'slot',
      label: '设置为请求参数',
      prop: 'isRequest',
      width: 150
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
      width: 150
    },
    {
      type: 'slot',
      label: '是否必填',
      prop: 'required',
      width: 150
    },
    {
      type: 'text',
      label: '说明',
      prop: 'desc'
    }
  ]
}
