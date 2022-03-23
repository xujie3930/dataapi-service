/*
 * @Description: 新增数据源-返回参数定义
 * @Date: 2022-02-25
 */

export default {
  refName: 'dataServiceApiResponseDetail',
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
      prop: 'fieldType'
    },

    {
      type: 'text',
      label: '默认值',
      prop: 'defaultValue'
    },

    {
      type: 'text',
      label: '说明',
      prop: 'desc'
    }
  ]
}
