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
      :handleSelectChange="handleSelectChange"
      :handleSelect="handleRowSelect"
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
      expandRowKeys: [],
      tableSelections: [],
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

  computed: {
    tableConfiguration() {
      return dataServiceAppTableConfig(this)
    }
  },

  created() {
    this.mixinRetrieveTableData()
  },

  methods: {
    // 点击-Table选择项发生变化时
    handleSelectChange(selection) {
      // const { children } = selection
      // const tableComponentDom =
      //   this.$refs.crud.$refs?.table?.$refs[this.tableConfiguration.refName]
      // console.log(selection, tableComponentDom, 'sectionsss')
      // // 一级checkbox
      // console.log(children)
      // if (children) {
      //   children.forEach(item =>
      //     tableComponentDom.toggleRowSelection(item, true)
      //   )
      // }
      this.tableSelections = selection
    },

    // 点击-Table选择项发生变化时
    handleRowSelect({ row }) {
      // const { children } = row
      // const tableComponentDom =
      //   this.$refs.crud.$refs?.table?.$refs[this.tableConfiguration.refName]
      console.log(row, 'sectionsss')
      // 一级checkbox
      // console.log(children)
      // if (children) {
      //   children.forEach(item => tableComponentDom.toggleRowSelection(item))
      // }
      // this.tableSelections = selection
    },

    // 点击-新增应用分组
    handleAddAppGroupClick() {
      this.$refs.addAppGroup.open({ title: '新增应用分组', opType: 'add' })
    },

    // 点击-新增应用
    handleAddAppClick({ row }) {
      this.$refs.addApp.open({ row, title: '新增应用', opType: 'add' })
    },

    // 点击-编辑应用分组
    handleEditGroupClick({ row }) {
      this.$refs.addAppGroup.open({
        title: '编辑应用分组',
        opType: 'edit',
        row
      })
    },

    // 点击-删除应用删除
    handleDeleteGroupClick({ row }) {
      console.log(row, 'row')
      const { children, id } = row
      const enableApiArr = children.filter(({ isEnable }) => isEnable)
      enableApiArr.length
        ? this.$alert(
            '该分组下包含启用状态的应用，请停用后再进行删除！',
            '提示',
            {
              confirmButtonText: '确定',
              type: 'warning'
            }
          )
        : this.$confirm(
            '删除该分组后，分组下的所有应用都将全部被删除，请确认是否删除该分组？',
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
          )
            .then(() => {
              this.deleteApiGroup([id])
            })
            .catch(() => {})
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

    // 点击-停用或启用App状态
    handleChangeAppStatusClick({ row }) {
      this.$confirm(
        '停用该应用后将不能进行API调用，请确认是否停用该应用？',
        '停用提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(() => {
          this.changeAppStatus(row)
        })
        .catch(() => {})
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

    // 删除分组
    deleteApiGroup(ids) {
      API.deleteAppGroup({ ids }).then(({ success, data }) => {
        if (success && data) {
          this.$notify.success({
            title: '操作结果',
            message: '删除成功！',
            duration: 1500
          })

          this.mixinRetrieveTableData()
        }
      })
    },

    changeAppStatus(row) {
      const { id, isEnable } = row
      API.enableOrStopApp({ id, isEnable: isEnable ? 0 : 1 }).then(
        ({ success, data }) => {
          if (success && data) {
            this.$notify.success({
              title: '操作结果',
              message: `${isEnable ? '停用' : '启用'}成功！`,
              duration: 2000
            })

            this.mixinRetrieveTableData()
          }
        }
      )
    },

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
    },

    interceptorsResponseTableData(data) {
      data?.length && (this.expandRowKeys = [data[0].id])
      return data
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
