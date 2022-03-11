import { API_TYPE, STATUS_MAPPING, API_MODE } from '@/config/constant'
import { dateFormat } from '@/utils'

export default {
  base: [
    {
      label: 'API类型',
      value: '',
      key: 'type',
      formatter: value => API_TYPE[value]
    },
    { label: 'API名称', value: '', key: 'name' },
    {
      label: 'API模式',
      value: '',
      key: 'model',
      formatter: value => API_MODE[value]
    },
    { label: 'API Path', value: '', key: 'apiPath' },
    { label: '请求方式', value: '', key: 'requestType' },
    { label: '返回类型', value: '', key: 'responseType' },
    { label: '协议', value: '', key: 'protocol' },
    { label: '接口地址', value: '', key: 'interfaceAddress' },
    { label: '所属分组', value: '', key: 'destination' },
    {
      label: '发布状态',
      value: '',
      key: 'publishStatus',
      color: '',
      formatter: value => STATUS_MAPPING[value]
    },
    {
      label: '版本号',
      value: '',
      key: 'apiVersion',
      formatter: value => (value ? `v${value}` : '')
    },
    { label: '创建人', value: '', key: 'createBy' },
    {
      label: '创建时间',
      value: '',
      key: 'createTime',
      formatter: val => dateFormat(val)
    },
    { label: '发布人', value: '', key: 'publishUser' },
    {
      label: '发布时间',
      value: '',
      key: 'publishTime',
      formatter: val => dateFormat(val)
    },
    { label: '备注', value: '', key: 'desc' }
  ],

  table: [
    { label: '数据库类型', value: 'MySQL', key: 'databaseType' },
    { label: '数据源名称', value: '', key: 'databaseName' },
    { label: '数据表名称', value: '', key: 'tableName' }
  ]
}
