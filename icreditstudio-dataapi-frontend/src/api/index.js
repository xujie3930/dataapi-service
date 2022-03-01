/*
 * @Description: 封装常用的请求方法
 * @Date: 2022-01-22
 */
import axios from '@/utils/request'

// post
export function postAction(url, parameter, options = {}) {
  return axios({
    url,
    method: 'post',
    data: parameter,
    ...options
  })
}

// get
export function getAction(url, parameter, options = {}) {
  return axios({
    url,
    method: 'get',
    data: true,
    params: parameter,
    ...options
  })
}

// post method= {post | put}
export function httpAction(url, parameter, method) {
  return axios({
    url,
    method,
    data: parameter
  })
}

// put
export function putAction(url, parameter) {
  return axios({
    url,
    method: 'put',
    data: parameter
  })
}

// deleteAction
export function deleteAction(url, parameter) {
  return axios({
    url,
    method: 'delete',
    params: parameter
  })
}

// fileAction
export function exportAction(url, options = {}) {
  return axios({
    url,
    method: 'get',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    ...options
  })
}

// 导入
export function importFile(url, data) {
  return axios({
    url,
    data,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
