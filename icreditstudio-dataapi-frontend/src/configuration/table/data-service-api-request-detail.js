/*
 * @Description: 新增数据源-请求参数定义
 * @Date: 2022-02-25
 */

export default {
  refName: 'dataServiceApiRequestDetail',
  isBorder: true,
  isStripe: true,
  hasPage: false,
  group: [
    {
      type: 'text',
      label: '参数名称',
      prop: 'fieldName'
    },
    {
      type: 'text',
      label: '参数类型',
      prop: 'fieldType',
      width: 150
    },
    {
      type: 'text',
      label: '是否必填',
      prop: 'required',
      width: 150,
      formatter: ({ required }) => (required ? '否' : '是')
    },
    {
      type: 'text',
      label: '默认值',
      prop: 'defaultValue',
      maxlength: 100,
      showWordLimit: true
    },
    {
      type: 'text',
      label: '说明',
      prop: 'desc'
    }
  ]
}
