/*
 * @Author: lizheng
 * @Description:
 * @Date: 2022-02-18
 */
import axios from 'axios'
import Notification from './notify'
import store from '@/store'
import { MessageBox } from 'element-ui'
import { CODE_Message } from '@/config'

const getCodeMessage = code => CODE_Message[code]
const token = () => store.getters.token
const appEnv = () => store.getters.appEnv

const service = axios.create({
  baseURL: process.env.VUE_APP_PROXY,
  withCredentials: true, // send cookies when cross-domain requests
  timeout: process.env.NODE_ENV === 'development' ? 0 : 300000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    const configObj = config
    configObj.headers.Authorization = appEnv ? token() : `Bearer ${token()}`
    configObj.headers.userId = '910626036754939904'
    return configObj
  },
  error => {
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  async response => {
    const res = response.data
    const { success, returnMsg, code } = res

    // if (Object.prototype.toString.call(res) === '[object Blob]') {
    //   setStore('headers', JSON.stringify(response.headers))
    //   return res
    // }

    if (!success && code !== 200) {
      // 登录已失效或账号在其他地方登录
      if (res.code === 401) {
        MessageBox.alert(
          '很抱歉，登录已失效或账号在其他地方登录',
          '登录已过期',
          {
            confirmButtonText: '确定',
            callback: () => {
              store.dispatch('user/resetToken', store.getters.appEnv)
            }
          }
        )
        return Promise.reject()
      }
      !response.config.noNotify &&
        Notification({
          message:
            res.returnMsg ||
            res.message ||
            '请求错误，请尝试重新提交或刷新当前页面',
          type: 'error',
          duration: 3 * 1000
        })
      return Promise.reject(!response.config.noNotify ? returnMsg : res)
    } else {
      return res
    }
  },
  error => {
    if (axios.isCancel && axios.isCancel(error)) {
      return Promise.resolve()
    } else {
      console.log('%cError=', 'color:blue', `err${error}`) // for debug
      if (error.response) {
        !error.response.config.noNotify &&
          Notification({
            message: getCodeMessage(error.response.status),
            type: 'error',
            duration: 5 * 1000
          })
      } else {
        Notification({
          message: '请求超时，请尝试重新提交或刷新当前页面',
          type: 'error',
          duration: 5 * 1000
        })
      }
      return Promise.reject(error)
    }
  }
)

export default service
