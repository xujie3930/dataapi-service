<!--
 * @Author: lizheng
 * @Description: 应用管理
 * @Date: 2022-02-27
-->
<template>
  <div>
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
      @handleAddDataServiceApi="handleAddDataServiceApi"
    />
  </div>
</template>

<script>
import { crud } from '@/mixins'
import { dataServiceAppForm } from '@/configuration/form'
import { dataServiceAppTableConfig } from '@/configuration/table'

export default {
  mixins: [crud],

  data() {
    return {
      tableConfiguration: dataServiceAppTableConfig(this),
      formOption: dataServiceAppForm,

      mixinSearchFormConfig: {
        models: { name: '', type: '', path: '', publishStatus: '', time: [] }
      }
    }
  },

  methods: {
    handleAddDataServiceApi() {}
  }
}
</script>

<style></style>
