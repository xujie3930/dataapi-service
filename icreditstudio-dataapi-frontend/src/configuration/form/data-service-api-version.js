/*
 * @Description: 版本列表
 * @Date: 2022-02-18
 */

export default [
  {
    type: 'text',
    label: '发布人',
    model: '',
    ruleProp: 'name',
    isSearch: true
  },

  {
    type: 'select',
    label: '版本号',
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
