/*
 * @Description: 版本列表
 * @Date: 2022-02-18
 */

export default that => [
  {
    type: 'text',
    label: '发布人',
    model: '',
    ruleProp: 'publishUser',
    isSearch: true
  },

  {
    type: 'select',
    label: '版本号',
    ruleProp: 'apiVersion',
    model: '',
    inline: true,
    isSearch: true,
    filterable: true,
    options: that.versionOptions
  }
]
