/*
 * @Author: lizheng
 * @Description: App entry
 * @Date: 2022-01-16
 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'
import App from './App.vue'
import routes from './routers'
import store from './store'
import SharedModule from './share'

import '@/components/svg'
import '@/components/dialog'
import '@/components/table'
import '@/components/form'
import '@/components/container'
import '@/components/select-tree'
import '@/components/crud'

import '@/utils/vue.ls.js'
import '@/utils/vue.ss.js'
import '@/assets/scss/index.scss'

import './public-path'
import 'element-ui/lib/theme-chalk/index.css'

Vue.config.productionTip = false
Vue.use(ElementUI, { size: 'samll' })

let router = null
let instance = null

const updateState = state => {
  const { workspaceId, userInfo, token } = state
  console.log(state, 'state')
  store.commit('user/SET_WRKSPACE_ID', workspaceId)
  store.commit('user/SET_USER_INFO', userInfo)
  store.commit('user/SET_TOKEN', token)
}

const watcher = props => {
  const { state = SharedModule.getShared(), onGlobalStateChange } = props
  SharedModule.overloadShared(state)
  store.commit('app/SET_APP_ENV', state.appEnv)
  updateState(state)

  onGlobalStateChange &&
    onGlobalStateChange(nVal => {
      updateState(nVal)
    }, true)
}

function render(props = {}) {
  const { container } = props
  watcher(props)
  router = new VueRouter({
    base: window.__POWERED_BY_QIANKUN__ ? '/subapp/dataservice/' : '/',
    mode: 'history',
    routes
  })

  instance = new Vue({
    router,
    store,
    render: h => h(App)
  }).$mount(
    container
      ? container.querySelector('#icreditstudio-dataapi-app')
      : '#icreditstudio-dataapi-app'
  )
}

if (!window.__POWERED_BY_QIANKUN__) {
  render()
}

export async function bootstrap() {
  console.log('[vue] vue app bootstraped')
}

export async function mount(props) {
  console.log('%c [vue] props from main framework', 'color: purple', props)
  render(props)
}

export async function unmount() {
  instance.$destroy()
  instance.$el.innerHTML = ''
  instance = null
  router = null
}
