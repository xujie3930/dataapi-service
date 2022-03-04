<!--
 * @Description: 应用管理
 * @Date: 2022-02-27
-->
<template>
  <div class="log-manage">
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
    />

    <!-- 详情 -->
    <Detail
      ref="logDetail"
      v-model="detailVisible"
      width="500px"
      :loading="detailLoading"
      :detail-configuration="detailConfiguration"
      :detail-title-key-mapping="detailTitleKeyMapping"
      :fetch-detail-data="fetchDetailData"
    />
  </div>
</template>

<script>
import { crud } from '@/mixins'
import { dataServiceLogForm } from '@/configuration/form'
import { logManageTableConfig } from '@/configuration/table'

import API from '@/api/api'
import { cloneDeep } from 'lodash'
import { detailConfiguration, detailTitleKeyMapping } from './detail-config'

export default {
  mixins: [crud],

  data() {
    return {
      detailVisible: false,
      detailLoading: false,
      tableConfiguration: logManageTableConfig(this),
      formOption: dataServiceLogForm,

      mixinSearchFormConfig: {
        models: {
          apiName: '',
          appName: '',
          apiVersion: '',
          time: '',
          callStatus: ''
        }
      },

      fetchConfig: { retrieve: { url: '/log/list', method: 'post' } },

      detailConfiguration: cloneDeep(detailConfiguration({})),
      detailTitleKeyMapping: cloneDeep(detailTitleKeyMapping)
    }
  },

  created() {
    this.mixinRetrieveTableData()
  },

  methods: {
    // 点击-查看日志
    handleDetailClick({ row }) {
      this.detailVisible = true
      this.$refs.logDetail.open({ row, title: '日志详情' })
    },

    // 获取-日志详情（供Detail子组件调用）
    fetchDetailData({ row }) {
      this.detailLoading = true
      this.detailConfiguration = cloneDeep(detailConfiguration(row))

      API.getLogDetail({ id: row.id })
        .then(({ success, data }) => {
          if (success && data) {
            cloneDeep(this.detailConfiguration.base).forEach((item, idx) => {
              const { key, formatter } = item
              const val = data[key]

              this.detailConfiguration.base[idx].value = formatter
                ? typeof formatter(val) === 'object'
                  ? formatter(val)?.name
                  : formatter(val)
                : val

              if ('color' in item) {
                this.detailConfiguration.base[idx].color = formatter(val)?.color
              }
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
      const callBeginTime = time?.length ? time[0] : ''
      const callEndTime = time?.length ? time[1] : ''

      return {
        callBeginTime,
        callEndTime,
        ...restParams
      }
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
