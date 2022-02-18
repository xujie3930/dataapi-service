import API from '@/api/api'
import { mapActions, mapState } from 'vuex'

export default {
  data() {
    return { detailLoading: false, btnEditLoading: false }
  },

  computed: {
    ...mapState('user', ['workspaceId'])
  },

  methods: {
    ...mapActions('user', ['getPermissionListAction', 'setWorkspaceId']),

    // 删除操作
    handleDeleteClick(methodName, params, dialogName) {
      API[methodName](params)
        .then(({ success }) => {
          if (success) {
            dialogName && this.$refs[dialogName].close()
            this.$notify.success({
              title: '操作结果',
              message: '删除成功！',
              duration: 1500
            })

            // 工作空间设置页面
            if (this.$route.path === '/workspace/space-setting') {
              this.$ls.remove('workspaceId')
              this.setWorkspaceId('0')
              this.getPermissionListAction()
            }

            this.mixinRetrieveTableData()
          }
        })
        .finally(() => {
          dialogName && this.$refs[dialogName].btnLoadingClose()
        })
    },

    // 启用操作
    handleEnabledClick(methodName, params) {
      API[methodName](params)
        .then(({ success }) => {
          if (success) {
            this.$notify.success({
              title: '操作结果',
              message: '启用成功！',
              duration: 1500
            })
            this.mixinRetrieveTableData()
          }
        })
        .finally(() => {})
    },

    // 停用操作
    handleDisabledClick(methodName, params, dialogName) {
      API[methodName](params)
        .then(({ success }) => {
          if (success) {
            this.$notify.success({
              title: '操作结果',
              message: '停用成功！',
              duration: 1500
            })
            dialogName && this.$refs[dialogName].close()
            this.mixinRetrieveTableData()
          }
        })
        .finally(() => {
          dialogName && this.$refs[dialogName].btnLoadingClose()
        })
    },

    // 获取编辑详情信息操作
    handleEditClick(methodName, params, opType = 'Edit', dialogName) {
      this.detailLoading = true
      this[`btn${opType}Loading`] = true
      if (dialogName) {
        this.$refs[dialogName].detailLoading = true
        this.$refs[dialogName].$refs.baseDialog.open()
      }
      API[methodName](params)
        .then(({ success, data }) => {
          if (success) {
            this.mixinDetailInfo(data, opType)
          }
        })
        .finally(() => {
          this.detailLoading = false
          this[`btn${opType}Loading`] = false
        })
    },

    // 编辑保存操作
    mixinEditSaveClick(options) {
      console.log(options)
    },

    mixinDetailInfo(data) {
      console.log(data)
    }
  }
}
