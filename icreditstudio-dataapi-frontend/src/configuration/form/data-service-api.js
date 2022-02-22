/*
 * @Description: 数据服务API
 * @Date: 2022-02-18
 */

export default [
  {
    type: 'text',
    label: 'API名称',
    model: '',
    ruleProp: 'name',
    isSearch: true
  },
  {
    type: 'text',
    label: 'API Path',
    model: '',
    ruleProp: 'path',
    isSearch: true
  },
  {
    type: 'select',
    label: '发布状态',
    ruleProp: 'publishStatus',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      { value: 1, label: '已发布' },
      { value: 2, label: '未发布' },
      { value: 3, label: '草稿' }
    ]
  },
  {
    type: 'select',
    label: 'API类型',
    ruleProp: 'type',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      { value: 0, label: '注册API' },
      { value: 1, label: '数据源生成API' }
    ]
  },

  {
    type: 'daterange',
    label: '发布时间',
    startPlaceholder: '开始日期',
    endPlaceholder: '结束日期',
    model: '',
    ruleProp: 'time',
    isSearch: true,
    format: 'yyyy-MM-dd',
    valueFormat: 'timestamp'
  }
]
