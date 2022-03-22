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
        :handleSelectChange="handleSelectChange"
        @handleBatchDelete="handleBatchDelete"
      />
    </Dialog>
    <VersionDetail ref="versionDetail" />
  </div>
</template>

<script>
import API from '@/api/api'
import crud from '@/mixins/crud'
import { dataServiceApiVersionForm } from '@/configuration/form/index'
import { tableServiceApiVersionTableConfig } from '@/configuration/table'
import VersionDetail from './detail'

export default {
  mixins: [crud],
  components: { VersionDetail },

  data() {
    return {
      selection: [],
      currentRow: {},
      versionOptions: [],
      formOption: dataServiceApiVersionForm,
      tableConfiguration: tableServiceApiVersionTableConfig(this),
      mixinSearchFormConfig: { models: { time: '', publishStatus: '' } },
      fetchConfig: { retrieve: { url: '/apiHistory/list', method: 'post' } }
    }
  },

  methods: {
    open(row) {
      row && (this.currentRow = row)
      this.$refs.baseDialog.open()
      this.mixinRetrieveTableData()
      // this.fetchApiVersionOptions()
    },

    close() {
      this.mixinHandleReset(false)
      this.$refs.baseDialog.close()
    },

    handleAuthorizeClick() {},

    // 点击-查看详情
    handleApiDetailClick({ row }) {
      this.$refs.versionDetail.open(row)
    },

    // 点击-编辑API
    handleEditApiClick({ row }) {
      this.$emit('edit-api', row)
    },

    // 点击-发布API
    handlePublishApiClick() {},

    // 点击-停止发布
    handleStopApiClick({ row }) {
      const { publishStatus } = row
      publishStatus === 2
        ? this.$confirm(
            '停止发布后，该版本API将不能授权给其他应用，并且已授权的应用也将全部失效，需重新发布该版本API后才能继续被授权的应用调用，请确认是否停止发布该版本API？',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
          )
            .then(() => {
              this.$message({ type: 'success', message: '删除成功!' })
            })
            .catch(() => {})
        : this.stopOrPublishApi()
    },

    handleSelectChange(selection) {
      console.log(selection, this.selection, 'ddsds')
      this.selection = selection

      // 批量删除
    },

    // 点击-批量删除
    handleBatchDelete() {
      if (!this.selection.length) {
        this.$message.error('请先勾选一条数据！')
        return
      }
    },

    //  点击-删除或批量删除API
    handleDeleteApiClick({ row }) {
      const { publishStatus } = row
      publishStatus === 2
        ? this.$alert('该API已发布，请先停止发布', '提示', {
            confirmButtonText: '确定',
            type: 'warning'
          })
        : this.$confirm('请确认是否删除该API？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              this.$message({ type: 'success', message: '删除成功!' })
            })
            .catch(() => {})
    },

    // 点击-发布或停止发布确认
    handleUpdateStatusClick({ row }) {
      const { apiHiId, publishStatus } = row
      console.log(row, 'roeee')
      if (publishStatus === 2) {
        this.$confirm(
          '停止发布后，该版本API将不能授权给其他应用，并且已授权的应用也将全部失效，需重新发布该版本API后才能继续被授权的应用调用，请确认是否停止发布该版本API？',
          '停止发布',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
          .then(() => {
            this.stopOrPublishApi(apiHiId, 1)
          })
          .catch(() => {})
      } else {
        this.stopOrPublishApi(apiHiId, 2)
      }
    },

    // 接口-发布或停止发布
    stopOrPublishApi(apiHiId, publishStatus) {
      API.updateDataApiStatus({ apiHiId, publishStatus }).then(
        ({ success, data }) => {
          if (success && data) {
            this.$notify.success({
              title: '操作结果',
              message: `${publishStatus === 2 ? '发布' : '停止发布'}成功！`,
              duration: 1500
            })

            this.mixinRetrieveTableData()
          }
        }
      )
    },

    // 接口-获取版本号Options
    // fetchApiVersionOptions() {
    //   API.getHistoryApiVesionOptions({ apiId: this.currentRow.id }).then(
    //     ({ success, data }) => {
    //       if (success && data) {
    //         this.versionOptions = data.apiVersions.reverse().map(item => ({
    //           label: `v${item}`,
    //           value: item
    //         }))

    //         const versionObj = this.mixinSearchFormItems[1]
    //         this.mixinSearchFormItems.splice(
    //           1,
    //           Object.assign(versionObj, { options: this.versionOptions })
    //         )
    //       }
    //     }
    //   )
    // },

    // 拦截-表格请求接口参数拦截

    interceptorsRequestRetrieve(params) {
      const { time, ...restParams } = params
      const publishDateStart = time?.length ? time[0] : ''
      const publishDateEnd = time?.length ? time[1] : ''

      return {
        apiId: this.currentRow.id,
        publishDateStart,
        publishDateEnd,
        ...restParams
      }
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
        width: 84px;
      }
    }

    .iframe-form-btn {
      width: unset;
    }
  }
}
</style>
