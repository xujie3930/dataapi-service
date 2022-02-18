/*
 * @Author: lizheng
 * @Description: sessionStorage方式存储
 * @Date: 2022-01-22
 */
import Vue from 'vue'
import Storage from 'vue-ls'
import { cloneDeep } from 'lodash'

// 使用sessionStorage
Vue.use(cloneDeep(Storage), {
  namespace: 'jnh__', // key prefix
  name: 'ss', // name variable Vue.[ls] or this.[$ls],
  storage: 'session' // storage name session, local, memory
})
