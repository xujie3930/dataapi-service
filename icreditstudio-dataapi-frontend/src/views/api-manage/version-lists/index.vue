<!--
 * @Author: lizheng
 * @Description: 版本列表
 * @Date: 2022-02-26
-->
<template>
  <div>
    <Dialog
      class="base-dialog"
      title="版本列表"
      width="1000px"
      ref="baseDialog"
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
        @handleBatchDelete="handleBatchDelete"
      />
    </Dialog>
    <VersionDetail ref="versionDetail" />
  </div>
</template>

<script>
import crud from '@/mixins/crud'
import { dataServiceApiVersionForm } from '@/configuration/form/index'
import { tableServiceApiVersionTableConfig } from '@/configuration/table'
import VersionDetail from './detail'

export default {
  mixins: [crud],
  components: { VersionDetail },

  data() {
    return {
      mixinTableData: [
        { name: 11, publishStatus: 2 },
        { name: 2323, publishStatus: 1 }
      ],
      formOption: dataServiceApiVersionForm,
      tableConfiguration: tableServiceApiVersionTableConfig(this),
      mixinSearchFormConfig: { models: { name: '', publishStatus: '' } },
      fetchConfig: { retrieve: { url: '/apiBase/list', method: 'post' } }
    }
  },

  methods: {
    open() {
      this.$refs.baseDialog.open()
    },

    handleBatchDelete() {},

    handleAuthorizeClick() {},

    handleVersionDetailClick({ row }) {
      console.log(row, 'rowrow')
      this.$refs.versionDetail.open()
    }
  }
}
</script>

<style lang="scss" scoped>
.base-dialog {
  ::v-deep {
    .iframe-form-item {
      width: 35%;

      .iframe-label .iframe-form-label {
        width: 64px;
      }
    }

    .iframe-form-btn {
      width: unset;
    }
  }
}
</style>
