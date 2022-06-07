<template>
  <div>
    <Dialog
      class="app-auth-list"
      ref="baseDialog"
      width="90vw"
      hide-footer
      :title="options.title"
      @on-change="change"
      @on-confirm="addApiAuthorization"
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
      >
        <div class="header-operate" slot="operation">
          <div class="header-operate-right">
            <el-button
              class="jui-button--default"
              @click="handleBatchDeleteClick"
              >批量删除</el-button
            >
            <el-button type="primary" @click="handleBatchConfigureClick">
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
      console.log(options, 'koko')
      this.options = options
      this.$refs.baseDialog.open()
      this.mixinRetrieveTableData()
    },

    change() {},

    addApiAuthorization() {},

    handleBatchDeleteClick() {},
    handleBatchConfigureClick() {},
    handleAddAuthorizeAppClick() {
      const { row } = this.options
      this.$refs.authorizeApi.open({ row, title: '新增授权API', opType: 'add' })
    },

    closeAuthorizeCallback() {},

    handleConfigureClick() {},
    handleDetailClick() {},

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
.app-auth-list {
  .header-operate {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    align-items: center;
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
