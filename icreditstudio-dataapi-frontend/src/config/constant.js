/*
 * @Author: lizheng
 * @Description:
 * @Date: 2022-02-28
 */

// 发布状态
export const STATUS_MAPPING = {
  0: { name: '待发布', color: '#999' },
  1: { name: '未发布', color: '#ff4d4f' },
  2: { name: '已发布', color: '#52c41a' }
}

// API类型
export const API_TYPE = { 0: '注册API', 1: '数据源生成API' }

// 接口来源
export const interfaceSource_TYPE = { 0: '内部', 1: '外部' }

// API模式
export const API_MODE = {
  0: '单表模式生成API',
  1: 'SQL模式生成API',
  2: '链上生成API'
}

// API: 认证方式
export const CERTIFICATION_TYPE = { 0: '密钥认证', 1: '证书认证' }

// API: Token有效期
export const TOEKN_PERIOD = { 0: '长期', 1: '8小时', 2: '自定义' }

// API： 授权有效期
export const APP_AUTH_PERIOD = { 1: '短期', 2: '永久' }

// API: 参数类型
export const REGISTER_API_TYPR = [
  { label: 'STRING', value: 'STRING' },
  { label: 'INT', value: 'INT' },
  { label: 'LONG', value: 'LONG' },
  { label: 'FLOAT', value: 'FLOAT' },
  { label: 'DOUBLE', value: 'DOUBLE' },
  { label: 'BOOLEAN', value: 'BOOLEAN' }
]

// 应用：状态是否启用
export const ENABLED_STATUS = {
  0: { name: '停用', color: '#ff4d4f' },
  1: { name: '启用', color: '#52c41a' }
}

// 应用：可调用次数
export const CALL_TYPE = {
  0: '有限次',
  1: '无限次'
}

// 日志：请求或调用状态是否启用
export const REQUEST_STATUS = {
  0: { name: '请求中', color: '#faad14' },
  1: { name: '成功', color: '#52c41a' },
  2: { name: '失败', color: '#ff4d4f' }
}

// 数据库类型
export const DATABASE_TYPE = {
  mysql: 1,
  oracle: 2,
  postgresql: 3,
  sqlserver: 4
}

export default {
  STATUS_MAPPING,
  API_TYPE,
  CERTIFICATION_TYPE,
  TOEKN_PERIOD,
  ENABLED_STATUS,
  REQUEST_STATUS,
  APP_AUTH_PERIOD,
  interfaceSource_TYPE
}
