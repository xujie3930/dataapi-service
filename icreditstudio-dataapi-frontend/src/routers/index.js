/*
 * @Author: lizheng
 * @Description: 前端路由参数配置
 * @Date: 2022-01-16
 */
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/list' },
  {
    path: '/list',
    name: 'home',
    component: () => import(/* webpackChunkName: "home" */ '@/views/demo')
  },

  {
    path: '/404',
    name: '404',
    component: () =>
      import(/* webpackChunkName: "404" */ '@/views/exception/404')
  }
]

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

export default routes
