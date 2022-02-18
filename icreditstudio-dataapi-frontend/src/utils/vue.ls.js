/*
 * @Author: lizheng
 * @Description: localStorage存储
 * @Date: 2022-01-22
 */
import Vue from 'vue'
import Storage from 'vue-ls'
import { cloneDeep } from 'lodash'

// 使用localeStorage
Vue.use(cloneDeep(Storage), {
  namespace: 'jnh__', // key prefix
  name: 'ls', // name variable Vue.[ls] or this.[$ls],
  storage: 'local' // storage name session, local, memory
})
