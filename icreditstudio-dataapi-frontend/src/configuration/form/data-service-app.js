/*
 * @Author: lizheng
 * @Description: 应用管理
 * @Date: 2022-02-28
 */
export default [
  {
    type: 'text',
    label: '分组名称',
    model: '',
    ruleProp: 'appGroupName',
    isSearch: true
  },
  {
    type: 'text',
    label: '应用名称',
    model: '',
    ruleProp: 'appName',
    isSearch: true
  },
  {
    type: 'select',
    label: '认证方式',
    ruleProp: 'certificationType',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      { value: 0, label: '密钥验证' },
      { value: 1, label: '证书验证' }
    ]
  },

  {
    type: 'select',
    label: '状态',
    ruleProp: 'isEnable',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      { value: 0, label: '启用' },
      { value: 1, label: '停用' }
    ]
  },
  {
    type: 'select',
    label: 'token有效期',
    ruleProp: 'period',
    model: '',
    inline: true,
    isSearch: true,
    options: [
      { value: '', label: '全部' },
      { value: 0, label: '长期' },
      { value: 1, label: '8小时' },
      { value: 2, label: '自定义' }
    ]
  }
]
