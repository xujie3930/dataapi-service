<!--
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
          <el-button class="jui-button--default" disabled>批量删除</el-button>
        </div>
        <div class="header-operate-right">
          <el-button type="primary" @click="handleAddAppGroupClick">
            新增应用分组
          </el-button>
          <el-button
            type="primary"
            :disabled="!mixinTableData.length"
            @click="handleAddAppClick"
          >
            新增应用
          </el-button>
        </div>
      </div>
    </Crud>

    <!-- 新增应用 -->
    <AddApp ref="addApp" @on-close="closeAddAppCallback" />

    <!-- 新增应用分组 -->
    <AddAppGroup ref="addAppGroup" @on-close="closeAddAppGroupCallback" />

    <!-- 授权 -->
    <AppAuthorization ref="authorize" @on-close="closeAuthorizeCallback" />

    <!-- 详情 -->
    <Detail
      ref="appDetail"
      v-model="detailVisible"
      :loading="detailLoading"
      :detail-configuration="detailConfiguration"
      :detail-title-key-mapping="detailTitleKeyMapping"
      :fetch-detail-data="fetchDetailData"
    />
  </div>
</template>

<script>
import { crud } from '@/mixins'
import { dataServiceAppForm } from '@/configuration/form'
import { dataServiceAppTableConfig } from '@/configuration/table'

import API from '@/api/api'
import { cloneDeep } from 'lodash'

import AddApp from './add-app'
import AddAppGroup from './add-app-group'
import AppAuthorization from './app-authorization'

import { detailConfiguration, detailTitleKeyMapping } from './detail-config'

export default {
  mixins: [crud],

  components: {
    AddApp,
    AddAppGroup,
    AppAuthorization
  },

  data() {
    return {
      detailVisible: false,
      detailLoading: false,
      detailOptions: {},
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

      fetchConfig: { retrieve: { url: '/appGroup/list', method: 'post' } },

      detailConfiguration: cloneDeep(detailConfiguration),
      detailTitleKeyMapping: cloneDeep(detailTitleKeyMapping)
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
    handleAddAppClick({ row }) {
      console.log(row, 'rowrowrow')

      this.$refs.addApp.open({ row, title: '新增应用', opType: 'add' })
    },

    // 点击-详情
    handleDetailClick({ row }) {
      this.detailVisible = true
      this.detailOptions = { title: '详情' }
      this.$refs.appDetail.open({ id: row.id, title: '详情' })
    },

    // 点击-授权
    handleAuthorizeClick({ row }) {
      this.$refs.authorize.open({ row, title: '授权', opType: 'add' })
    },

    closeAddAppGroupCallback(options) {
      console.log(options, 'options')
      this.mixinRetrieveTableData()
    },

    closeAddAppCallback() {
      this.mixinRetrieveTableData()
    },

    // 回调-授权设置弹窗回调
    closeAuthorizeCallback() {},

    // 获取-详情接口
    fetchDetailData({ id }) {
      this.detailLoading = true
      this.detailConfiguration = cloneDeep(detailConfiguration)

      API.getAppDetail({ id })
        .then(({ success, data }) => {
          if (success && data) {
            const { authResult, apiResult } = data
            // 基础信息模块赋值
            cloneDeep(this.detailConfiguration.base).forEach((item, idx) => {
              const { key, formatter } = item
              const val = data[key]

              this.detailConfiguration.base[idx].value = formatter
                ? typeof formatter(val) === 'object'
                  ? formatter(val)?.name
                  : formatter(val)
                : val

              // key值根据状态渲染颜色值
              if ('color' in item) {
                this.detailConfiguration.base[idx].color = formatter(val)?.color
              }
            })

            // 显示授权信息模块
            this.detailTitleKeyMapping.auth.visible = Boolean(apiResult)

            // 显示授权时间模块
            this.detailTitleKeyMapping.authTime.visible = Boolean(authResult)

            // 授权信息赋值
            apiResult &&
              (this.detailConfiguration.auth[0].value = apiResult?.apiNames)

            // 授权时间赋值
            authResult &&
              cloneDeep(this.detailConfiguration.authTime).forEach(
                (item, idx) => {
                  const { key: k, formatter } = item

                  this.detailConfiguration.authTime[idx].value = formatter
                    ? typeof formatter(authResult[k]) === 'object'
                      ? formatter(authResult[k])?.name
                      : formatter(authResult)
                    : authResult[k]

                  // 根特定条件隐藏某个label
                  if ('hide' in item) {
                    const { periodBegin, periodEnd } = authResult
                    this.detailConfiguration.authTime[idx].hide =
                      periodBegin > -1 && periodEnd > -1
                  }
                }
              )
          }
        })
        .finally(() => {
          this.detailLoading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '~@/assets/scss/button';

.app-manage {
  .header-operate {
    @include flex(space-between);
    padding-bottom: 20px;
  }
}
</style>
