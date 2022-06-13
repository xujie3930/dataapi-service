<template>
  <div>
    <Dialog
      class="app-auth-list"
      ref="baseDialog"
      width="90vw"
      hide-footer
      :title="options.title"
      @on-change="change"
      @on-confirm="handleAddApiAuthorization"
    >
      <Crud
        ref="crud"
        :form-items-search="mixinSearchFormItems"
        :form-func-search="mixinSearchFormFunc"
        :form-config-search="mixinSearchFormConfig"
        :form-items-dialog="mixinDialogFormItems"
        :form-func-dialog="mixinDialogFormFunc"
        :form-config-dialog="mixinDialogFormConfig"
        :tableLoading="mixinTableLoading"
        :table-configuration="tableConfiguration"
        :table-pagination="mixinTablePagination"
        :table-data="mixinTableData"
        :dialog-type="mixinDialogType"
        :dialog-visible.sync="mixinDialog"
        :handleSizeChange="mixinHandleSizeChange"
        :handleCurrentChange="mixinHandleCurrentChange"
        :handleAdd="mixinHandleAdd"
        :handleSearch="mixinHandleSearch"
        :handleReset="mixinHandleReset"
        :handleImport="mixinHandleImport"
        :handleExport="mixinHandleExport"
        :handleUpdate="mixinHandleCreateOrUpdate"
        :handleCancel="mixinHandleCancel"
        :handleSelectChange="handleSelectChange"
      >
        <div class="header-operate" slot="operation">
          <div class="header-operate-left">
            <el-button
              :disabled="!tableSelections.length"
              class="jui-button--default"
              @click="handleBatchDeleteClick"
              >批量删除</el-button
            >
          </div>
          <div class="header-operate-right">
            <el-button
              :disabled="!tableSelections.length"
              type="primary"
              @click="handleBatchConfigureClick"
            >
              批量配置
            </el-button>

            <el-button type="primary" @click="handleAddAuthorizeAppClick">
              新增授权API
            </el-button>
          </div>
        </div>
      </Crud>
    </Dialog>

    <!-- 授权 -->
    <AppAuthorization ref="authorizeApi" @on-close="closeAuthorizeCallback" />
  </div>
</template>

<script>
import API from '@/api/api'
import { crud } from '@/mixins'
import { formAppAuthList } from '@/configuration/form'
import { tableAppAuthList } from '@/configuration/table'
import AppAuthorization from './app-authorization.vue'

export default {
  mixins: [crud],

  components: { AppAuthorization },

  data() {
    return {
      options: { title: '' },
      tableSelections: [],
      formOption: formAppAuthList,
      tableConfiguration: tableAppAuthList(this),
      mixinSearchFormConfig: {
        models: {
          appName: '',
          apiPath: '',
          periodType: '',
          durationType: '',
          validTime: []
        }
      },
      fetchConfig: { retrieve: { url: '/auth/list', method: 'post' } }
    }
  },

  methods: {
    open(options) {
      this.options = options
      this.mixinRetrieveTableData()
      this.$refs.baseDialog.open()
    },

    close() {
      this.mixinHandleReset(false)
      this.$refs.baseDialog.close()
    },

    change(visible) {
      !visible && this.close()
      this.$emit('on-close', visible)
    },

    handleAddApiAuthorization() {},

    handleSelectChange(selection) {
      this.tableSelections = selection
    },

    // 按钮点击-批量删除API
    handleBatchDeleteClick() {
      const { id: appId } = this.options.row
      const params = {
        appId,
        authList: this.tableSelections.map(({ authId }) => authId)
      }
      this.$confirm('请确认是否删除批量选中的所有API？', '删除提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.deleteApi(params)
        })
        .catch(() => {})
    },

    // 按钮点击-单个删除API
    handleDeleteClick({ row }) {
      const { id: appId } = this.options.row
      const params = { appId, authList: [row.authId] }
      this.$confirm(
        '删除该API后，应用则不能对该API进行调用，请确认是否删除？',
        '删除提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(() => {
          this.deleteApi(params)
        })
        .catch(() => {})
    },

    // 删除API
    deleteApi(params) {
      API.deleteAuthApi(params).then(({ success, data }) => {
        if (success && data) {
          this.$notify.success({
            title: '操作结果',
            message: `删除成功！`,
            duration: 1500
          })
          this.tableSelections = []
          this.mixinRetrieveTableData()
        }
      })
    },

    // 按钮点击-批量配置API
    handleBatchConfigureClick() {
      const { row } = this.options

      const apiSourceArr = this.tableSelections.map(
        ({ apiInterfaceSource }) => apiInterfaceSource
      )

      const externalApiSourceArr = apiSourceArr.filter(item => item)

      // 勾选的数据只包含外部API
      if (externalApiSourceArr.length === apiSourceArr.length) {
        this.$alert(
          '批量选中的API均为外部来源API，请到资源目录下进行配置！',
          '批量配置提示',
          {
            showConfirmButton: false,
            showCancelButton: true,
            type: 'warning'
          }
        )
        return
      }

      // 勾选的数据包含内部以及外部API
      if (apiSourceArr.includes(1) && apiSourceArr.includes(0)) {
        this.$confirm(
          '批量选中的API中包含外部来源的API，点击“确定”按钮则过滤外部来源API后继续配置，重新选择请点击“取消”按钮。',
          '批量配置提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
          .then(() => {
            this.tableSelections
              .filter(({ apiInterfaceSource }) => apiInterfaceSource)
              .forEach(item =>
                this.$refs?.crud.$refs?.table.$refs?.appAuthList.toggleRowSelection(
                  item,
                  false
                )
              )
            this.$refs.authorizeApi.open({
              row,
              apiIds: this.tableSelections
                .filter(({ apiInterfaceSource }) => !apiInterfaceSource)
                .map(({ apiId }) => apiId),
              title: '授权参数配置',
              opType: 'batchDeploy'
            })
          })
          .catch(err => {
            console.log(err, 'err')
          })
      } else {
        this.$refs.authorizeApi.open({
          row,
          apiIds: this.tableSelections.map(({ apiId }) => apiId),
          title: '授权参数配置',
          opType: 'batchDeploy'
        })
      }
    },

    // 按钮点击-单个配置API
    handleConfigureClick(options) {
      const { row } = this.options
      const { apiId } = options.row
      this.$refs.authorizeApi.open({
        row,
        apiIds: [apiId],
        title: '授权参数配置',
        opType: 'deploy'
      })
    },

    // 按钮点击-新增授权API
    handleAddAuthorizeAppClick() {
      const { row } = this.options
      this.$refs.authorizeApi.open({ row, title: '新增授权API', opType: 'add' })
    },

    closeAuthorizeCallback() {
      this.mixinRetrieveTableData()
      this.tableSelections = []
    },

    // 拦截-表格请求接口参数拦截
    interceptorsRequestRetrieve(params) {
      const { id: appId } = this.options?.row ?? {}
      const { validTime, ...restParams } = params
      const periodBegin = validTime?.length ? validTime[0] : ''
      const periodEnd = validTime?.length
        ? validTime[1] + 24 * 60 * 60 * 1000 - 1
        : ''

      return {
        appId,
        periodBegin,
        periodEnd,
        ...restParams
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '~@/assets/scss/button';

.app-auth-list {
  .header-operate {
    @include flex(space-between);
    padding-bottom: 20px;
  }

  /deep/ .iframe-layout-basic-container {
    padding: 16px;
  }

  /deep/ .iframe-layout-basic-main-top {
    padding-bottom: 0;
  }
}
</style>
