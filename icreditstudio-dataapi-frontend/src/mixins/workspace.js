/*
 * @Description: 更换工作空间重新加载页面数据
 * @Date: 2021-08-30
 */

import { mapState } from 'vuex'

export default {
  watch: {
    workspaceId(nVal) {
      const { mixinRetrieveTableData, mixinChangeWorkspaceId } = this
      if (nVal) {
        mixinRetrieveTableData && this.mixinRetrieveTableData()
        mixinChangeWorkspaceId && mixinChangeWorkspaceId()
      }
    }
  },

  computed: {
    ...mapState('user', ['workspaceId'])
  },

  methods: {
    interceptorsRequestRetrieve(params) {
      return {
        workspaceId: this.workspaceId,
        ...params
      }
    },

    mixinChangeWorkspaceId() {}
  }
}
