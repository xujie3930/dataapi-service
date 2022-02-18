const state = {
  appEnv: '' // 主应用运行环境
}

const mutations = {
  SET_APP_ENV: (state, appEnv) => {
    state.appEnv = appEnv
  }
}

export default {
  namespaced: true,
  state,
  mutations
}
