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
      hide-footer
      @on-change="changeDialogVisible"
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

    <!-- 详情 -->
    <JDetail
      ref="versionDetail"
      v-model="detailVisible"
      :loading="detailLoading"
      :show-detail-table="true"
      :detail-configuration="detailConfiguration"
      :detail-title-key-mapping="detailTitleKeyMapping"
      :detail-table-configuration="detailTableConfiguration"
      :detail-table-title-key-mapping="detailTableTitleKeyMapping"
      :fetch-detail-data="fetchApiDetailData"
    />
  </div>
</template>

<script>
import API from '@/api/api'
import crud from '@/mixins/crud'
import { dataServiceApiVersionForm } from '@/configuration/form/index'
import { tableServiceApiVersionTableConfig } from '@/configuration/table'
import { cloneDeep } from 'lodash'
import { isPlainObject } from '@/utils'
import {
  detailConfiguration,
  detailTableConfiguration,
  detailTitleKeyMapping,
  detailTableTitleKeyMapping
} from './detail-config'

export default {
  mixins: [crud],
  data() {
    return {
      detailLoading: false,
      detailVisible: false,
      detailConfiguration: cloneDeep(detailConfiguration),
      detailTableConfiguration: cloneDeep(detailTableConfiguration),
      detailTitleKeyMapping: cloneDeep(detailTitleKeyMapping),
      detailTableTitleKeyMapping: cloneDeep(detailTableTitleKeyMapping),

      selection: [],
      currentRow: {},
      versionOptions: [],
      formOption: dataServiceApiVersionForm,
      tableConfiguration: tableServiceApiVersionTableConfig(this),
      mixinSearchFormConfig: { models: { time: [], publishStatus: '' } },
      fetchConfig: { retrieve: { url: '/apiHistory/list', method: 'post' } }
    }
  },

  methods: {
    open(row) {
      row && (this.currentRow = row)
      this.$refs.baseDialog.open()
      this.mixinRetrieveTableData()
    },

    close() {
      this.mixinHandleReset(false)
      this.$refs.baseDialog.close()
    },

    changeDialogVisible(visible) {
      !visible && this.close()
      this.$emit('on-close', visible)
    },

    handleAuthorizeClick() {},

    // 点击-查看详情
    handleApiDetailClick({ row }) {
      this.detailVisible = true
      this.$refs.versionDetail.open({ ...row, title: '详情' })
    },

    // 点击-编辑API
    handleEditApiClick({ row }) {
      this.$emit('edit-api', row)
    },

    handleSelectChange(selection) {
      this.selection = selection
    },

    // 点击-批量删除API
    handleBatchDelete() {
      if (!this.selection.length) {
        this.$message.error('请先勾选一条数据！')
        return
      }

      const { length } = this.selection.filter(
        ({ publishStatus }) => publishStatus === 2
      )

      length
        ? this.$alert('所选中的API中包含已发布的API，请重新选择！', '提示', {
            showConfirmButton: false,
            showCancelButton: true,
            type: 'warning'
          })
        : this.$confirm('请确认是否批量删除所选中的全部版本API？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              const ids = this.selection.map(({ apiHiId }) => apiHiId)
              API.deleteHistoryApiBatch(ids).then(({ success, data }) => {
                if (success && data) {
                  this.$notify.success({
                    title: '操作结果',
                    message: '删除成功！',
                    duration: 1500
                  })

                  this.mixinRetrieveTableData()
                }
              })
            })
            .catch(() => {})
    },

    //  点击-删除API
    handleDeleteApiClick({ row }) {
      const { publishStatus, apiHiId: id } = row
      publishStatus === 2
        ? this.$alert('该API已发布，请先停止发布', '提示', {
            showConfirmButton: false,
            showCancelButton: true,
            type: 'warning'
          })
        : this.$confirm('请确认是否删除该API？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              API.deleteHistoryApiItem({ id }).then(({ success, data }) => {
                if (success && data) {
                  this.$notify.success({
                    title: '操作结果',
                    message: '删除成功！',
                    duration: 1500
                  })

                  this.mixinRetrieveTableData()
                }
              })
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

    // 接口-获取详情数据
    fetchApiDetailData({ apiHiId }) {
      this.detailLoading = true
      this.detailConfiguration = cloneDeep(detailConfiguration)
      this.detailTitleKeyMapping = cloneDeep(detailTitleKeyMapping)
      this.detailTableTitleKeyMapping = cloneDeep(detailTableTitleKeyMapping)

      API.getHistoryApiDetail({ apiHiId })
        .then(({ success, data }) => {
          if (success && data) {
            const {
              paramList,
              generateApi,
              registerRequestParamSaveRequestList,
              registerResponseParamSaveRequestList,
              ...restData
            } = data

            cloneDeep(this.detailConfiguration.base).forEach((item, idx) => {
              const { key, formatter, hide } = item
              const val = key === 'model' ? generateApi?.model : restData[key]
              this.detailConfiguration.base[idx].value = formatter
                ? isPlainObject(formatter(val))
                  ? formatter(val)?.name
                  : formatter(val)
                : val

              if ('color' in item) {
                this.detailConfiguration.base[idx].color = formatter(val)?.color
              }

              if ('hide' in item) {
                this.detailConfiguration.base[idx].hide = hide(data)
              }
            })

            // 选择表
            cloneDeep(this.detailConfiguration.table).forEach(
              ({ key, value }, idx) => {
                this.detailConfiguration.table[idx].value =
                  key in cloneDeep(generateApi ?? {}) ? generateApi[key] : value
              }
            )

            // 后台服务
            cloneDeep(this.detailConfiguration.service).forEach(
              ({ key }, idx) => {
                this.detailConfiguration.service[idx].value = data[key]
              }
            )

            //选择参数
            this.detailTableConfiguration.params.tableData = paramList ?? []

            this.detailTitleKeyMapping.service.visible = !data.type
            this.detailTitleKeyMapping.table.visible = !!data.type

            Object.assign(this.detailTableConfiguration.params, {
              visible: data.type === 1 ? true : false
            })
            Object.assign(this.detailTableConfiguration.request, {
              visible: data.type === 0 ? true : false,
              tableData: registerRequestParamSaveRequestList
            })
            Object.assign(this.detailTableConfiguration.response, {
              visible: data.type === 0 ? true : false,
              tableData: registerResponseParamSaveRequestList
            })
          }
        })
        .finally(() => {
          this.detailLoading = false
        })
    },

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
