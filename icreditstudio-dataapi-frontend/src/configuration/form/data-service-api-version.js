/*
 * @Description: 版本列表
 * @Date: 2022-02-18
 */

export default [
  {
    type: 'daterange',
    label: '发布日期',
    startPlaceholder: '开始日期',
    endPlaceholder: '结束日期',
    model: '',
    ruleProp: 'time',
    isSearch: true,
    format: 'yyyy-MM-dd',
    valueFormat: 'yyyy-MM-dd'
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
      { value: 2, label: '已发布' },
      { value: 1, label: '未发布' },
      { value: 0, label: '待发布' }
    ]
  }
]
