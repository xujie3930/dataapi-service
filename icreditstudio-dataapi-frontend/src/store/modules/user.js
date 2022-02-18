import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: '',
  userOrgId: '',
  userInfo: { userId: '' },
  roles: [],
  roleId: '',
  authType: '',
  workspaceId: '',
  userId: ''
}

const mutations = {
  SET_TOKEN: (state, token = getToken()) => {
    state.token = token
    setToken(token)
  },

  SET_ROLES: (state, roles) => {
    state.roles = roles
  },

  SET_USER_ORG_ID: (state, userOrgId) => {
    state.userOrgId = userOrgId
    // setStore('userOrgId', userOrgId)
  },

  SET_USER_INFO: (state, userInfoResult) => {
    state.userInfo = userInfoResult
    state.roleId = userInfoResult ? userInfoResult.id : ''
    // setStore('userInfo', userInfoResult)
  },

  SET_AUTH_TYPE: (state, type) => {
    state.authType = type
    // setStore('authType', type)
  },

  SET_WRKSPACE_ID(state, id) {
    state.workspaceId = id
  },

  SET_USER_ID(state, id) {
    state.userId = id
  }
}

const actions = {
  resetToken({ commit }, appEnv) {
    return new Promise(resolve => {
      commit('SET_ROLES', [])
      commit('SET_USER_INFO', {})
      commit('SET_USER_ORG_ID', '')
      commit('SET_AUTH_TYPE', '')
      window.localStorage.clear()
      removeToken()

      if (window.__POWERED_BY_QIANKUN__) {
        console.log(appEnv, 'appEnv')
        window.location.href =
          window.__POWERED_BY_QIANKUN__ && appEnv === 'openDataPlatForm'
            ? '/data-pf/login'
            : '/login'
      }

      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
