import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'

Vue.use(Vuex)

const modulesFiles = require.context('./modules', true, /\.js$/)

const modules = modulesFiles.keys().reduce((moduleItem, modulePath) => {
  const modulesObj = moduleItem
  const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1')
  const value = modulesFiles(modulePath)
  modulesObj[moduleName] = value.default
  return modulesObj
}, {})

const store = new Vuex.Store({
  namespaced: true,
  modules,
  getters
})

export default store
