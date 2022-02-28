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
export const API_TYPE = { 0: '注册API', 1: '数据源生成' }

// API模式
export const API_MODE = {
  0: '表单生成api',
  1: 'SQL模式生成api',
  2: '链上生成api'
}

// API模式
export const API_MODE_MAPPING = { TABLE: 0, SQL: 1, CHAIN: 2 }

// API类型
export const API_TYPE_MAPPING = { REGISTER: 0, SOURCE: 1 }

export default {
  STATUS_MAPPING,
  API_TYPE
}
