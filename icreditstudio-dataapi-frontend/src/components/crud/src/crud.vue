<!--
 * @Description: 继承了表格表单的增删改查组件
 * @Date: 2022-01-22
-->

<template>
  <div class="h100">
    <Container
      :title="title"
      :custom-header-button="tableConfiguration.customHeaderButton"
      :show-menu="!hideMenu"
      :custom-btn-config="
        tableConfiguration.customBtnConfig &&
        tableConfiguration.customBtnConfig.length
          ? tableConfiguration.customBtnConfig
          : customBtnConfig
      "
      @handleAdd="handleAdd"
      @handleImport="handleImport"
      @handleExport="handleExport"
      @handleCustomMenuEvent="handleCustomMenuEvent"
    >
      <template #header v-if="!hideHeader">
        <slot name="header">
          <j-form
            ref="searchForm"
            :form-items="formItemsSearch"
            :form-func="formFuncSearch"
            :form-config="formConfigSearch"
            @mixinSearch="handleSearch"
            @mixinReset="handleReset"
          ></j-form>
        </slot>
      </template>
      <template #content v-if="!hideContent">
        <slot name="operation" />
        <slot name="content">
          <j-table
            ref="table"
            v-loading="tableLoading"
            :table-configuration="tableConfiguration"
            :table-pagination="tablePagination"
            :table-data="tableData"
            @handleSizeChange="handleSizeChange"
            @handleCurrentChange="handleCurrentChange"
            @selection-change="_handleCurrentChange"
          ></j-table>
        </slot>
      </template>
    </Container>

    <el-dialog
      :title="dialogTitle ? dialogTitle : dialogType ? '编辑' : '新增'"
      :visible.sync="visible"
      :width="dialogWidth"
      :close-on-click-modal="false"
    >
      <j-form
        ref="form"
        :form-items="formItemsDialog"
        :form-func="formFuncDialog"
        :form-config="formConfigDialog"
        @mixinSave="handleUpdate"
        @mixinCancel="handleCancel"
      ></j-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Crud',
  props: {
    hideHeader: {
      type: Boolean,
      default: false
    },
    hideContent: {
      type: Boolean,
      default: false
    },
    hideMenu: {
      type: Boolean,
      default: false
    },
    // 列表标题
    title: {
      type: String,
      default: ''
    },
    // 列表搜索项
    formItemsSearch: {
      type: Array,
      default() {
        return []
      }
    },
    // 搜索模块按钮
    formFuncSearch: {
      type: Array,
      default() {
        return []
      }
    },
    // 搜索模块配置
    formConfigSearch: {
      type: Object
    },
    // table loading
    tableLoading: {
      type: Boolean
    },
    // 表格配置
    tableConfiguration: {
      type: Object,
      default() {
        return {
          customBtnConfig: []
        }
      }
    },
    customBtnConfig: {
      type: Array,
      default: () => []
    },
    // 表格分页
    tablePagination: {
      type: Object
    },
    // 表格分页
    tableData: {
      type: Array,
      default() {
        return []
      }
    },
    dialogType: {
      type: Number
    },
    dialogTitle: {
      type: String
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
    dialogWidth: {
      type: [String, Number],
      default: '70%'
    },
    // 列表搜索项
    formItemsDialog: {
      type: Array,
      default() {
        return []
      }
    },
    // 搜索模块按钮
    formFuncDialog: {
      type: Array,
      default() {
        return []
      }
    },
    // 搜索模块配置
    formConfigDialog: {
      type: Object
    },
    // 新增
    handleAdd: {
      type: Function
    },
    // 导出
    handleExport: {
      type: Function,
      default() {
        return () => {}
      }
    },
    // 导入
    handleImport: {
      type: Function,
      default() {
        return () => {}
      }
    },
    // 批量删除
    handleMultipleDelete: {
      type: Function,
      default() {
        return () => {}
      }
    },
    // 查询
    handleSearch: {
      type: Function
    },
    // 重重置
    handleReset: {
      type: Function
    },
    // 重重置
    handleSizeChange: {
      type: Function
    },
    // 重重置
    handleCurrentChange: {
      type: Function
    },
    // 重重置
    handleUpdate: {
      type: Function
    },
    // 重重置
    handleCancel: {
      type: Function
    },
    handleSelectChange: {
      type: Function,
      default() {
        return () => {}
      }
    }
  },
  computed: {
    visible: {
      get() {
        return this.dialogVisible
      },
      set(e) {
        this.$emit('update:dialogVisible', e)
      }
    }
  },
  data() {
    return {
      selection: []
    }
  },
  methods: {
    updateModels(row) {
      if (!row) {
        this.$nextTick(() => {
          const refDom = this.$refs.form.$refs[this.formConfigDialog.refName]
          if (refDom) {
            refDom.resetFields()
          }
        })
      } else {
        const t = {}
        if (row && row.length) {
          row.forEach(item => {
            t[item.ruleProp] = item.model
          })
          this.$nextTick(() => {
            this.formConfigDialog.models = t
          })
        }
      }
    },
    _handleCurrentChange(selection) {
      this.selection = selection
      return this.handleSelectChange(selection)
    },
    handleCustomMenuEvent({ options }) {
      const { eventName, selectType } = options
      const len = this.selection.length
      let validMsg = ''
      // 公共校验
      switch (selectType) {
        case 'none':
          validMsg = ''
          break
        case 'single':
          validMsg = len === 1 ? '' : '只支持单选!'
          break
        case 'multiple':
          validMsg = len > 0 ? '' : '请至少选择一条数据！'
          break
        default:
          validMsg = ''
          break
      }
      if (validMsg) {
        this.$message.warning(validMsg)
      } else {
        this.$emit(eventName, options)
      }
    }
  }
}
</script>
