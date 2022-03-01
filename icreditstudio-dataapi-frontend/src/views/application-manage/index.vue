<!--
 * @Author: lizheng
 * @Description: 应用管理
 * @Date: 2022-02-27
-->
<template>
  <div class="app-manage">
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
        <div class="header-operate-left">
          <el-button class="jui-button--default">批量删除</el-button>
        </div>
        <div class="header-operate-right">
          <el-button type="primary" @click="handleAddAppGroupClick">
            新增应用分组
          </el-button>
          <el-button type="primary" @click="handleAddAppClick">
            新增应用
          </el-button>
        </div>
      </div>
    </Crud>

    <AddAppGroup ref="addAppGroup" @on-close="closeAddAppGroupCallback" />
  </div>
</template>

<script>
import { crud } from '@/mixins'
import { dataServiceAppForm } from '@/configuration/form'
import { dataServiceAppTableConfig } from '@/configuration/table'

import AddAppGroup from './add-app-group'

export default {
  mixins: [crud],

  components: {
    AddAppGroup
  },

  data() {
    return {
      tableConfiguration: dataServiceAppTableConfig(this),
      formOption: dataServiceAppForm,

      mixinSearchFormConfig: {
        models: {
          appGroupName: '',
          appName: '',
          certificationType: '',
          isEnable: '',
          period: ''
        }
      },

      fetchConfig: { retrieve: { url: '/appGroup/list', method: 'post' } }
    }
  },

  created() {
    this.mixinRetrieveTableData()
  },

  methods: {
    // 点击-新增应用分组
    handleAddAppGroupClick() {
      this.$refs.addAppGroup.open({ title: '新增应用分组', opType: 'add' })
    },

    // 点击-新增应用
    handleAddAppClick() {},

    closeAddAppGroupCallback(options) {
      console.log(options, 'options')
      this.mixinRetrieveTableData()
    }
  }
}
</script>

<style lang="scss" scoped>
@import '~@/assets/scss/button';

.app-manage {
  .header-operate {
    @include flex(space-between);
    padding: 20px 0;
  }
}
</style>
