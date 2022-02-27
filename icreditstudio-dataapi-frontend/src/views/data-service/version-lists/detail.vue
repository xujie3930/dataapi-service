<!--
 * @Author: lizheng
 * @Description: 详情
 * @Date: 2022-02-25
-->

<template>
  <Dialog
    ref="baseDialog"
    class="datasource-dialog"
    width="850px"
    hide-footer
    title="详情"
  >
    <div class="detail-row">
      <div class="banner-title">
        <div class="text">基础信息</div>

        <el-row class="detail-row-item">
          <el-col :span="12">
            <span class="label">数据源名称</span>
            <span class="value">shujuyuan</span>
          </el-col>

          <el-col :span="12">
            <span class="label">数据表名称</span>
            <span class="value label-text label-text-des">table ddd</span>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="detail-row">
      <div class="banner-title">
        <div class="text">选择表</div>

        <el-row class="detail-row-item">
          <el-col :span="12">
            <span class="label">数据源名称</span>
            <span class="value">shujuyuan</span>
          </el-col>

          <el-col :span="12">
            <span class="label">数据表名称</span>
            <span class="value">table ddd</span>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="detail-table">
      <div class="banner-title">
        <div class="text">选择参数</div>
      </div>

      <JTable
        ref="editTable"
        v-loading="tableLoading"
        :table-data="tableData"
        :table-configuration="tableConfig"
      />
    </div>
  </Dialog>
</template>

<script>
import API from '@/api/api'
import { tableServiceApiDetailTableConfig } from '@/configuration/table'

export default {
  data() {
    return {
      timerId: null,
      title: '',
      opType: 'View',
      dialogVisible: false,
      detailData: { showPassword: 0 },
      rules: {
        name: [
          { required: true, message: '请输入自定义数据源名称', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '请选择活动区域', trigger: 'change' }
        ],
        resource: [
          { required: true, message: '请选择活动资源', trigger: 'change' }
        ]
      },
      tableLoading: false,
      sourceTableData: [],
      tableData: [],
      tableCount: 0,
      tableIndex: null,
      tableName: undefined,
      tableOptions: [],
      tableConfig: tableServiceApiDetailTableConfig
    }
  },

  computed: {
    hidePassword() {
      const { showPassword, password: pwd } = this.detailData
      const getHideIcon = num => Array(num).fill('*').join('')

      return showPassword ? pwd : getHideIcon(pwd ? String(pwd).length : 0)
    }
  },

  methods: {
    open() {
      this.opType = 'view'
      this.tableOptions = []
      this.handleReset()
      // this.getTableDetailData(data.id)
      this.$refs.baseDialog.open()
    },

    // 获取表结构信息详情
    getTableDetailData(id) {
      this.tableLoading = true
      API.datasourceTableDetail(id)
        .then(({ success, data }) => {
          if (success && data) {
            const { tableCount, tableOptions, columnList } = data
            this.tableCount = tableCount
            this.tableOptions = tableOptions
              ? tableOptions.map(value => ({ value }))
              : []
            this.tableData = columnList ?? []
            this.sourceTableData = columnList ?? []
          }
        })
        .finally(() => {
          this.tableLoading = false
        })
    },

    handleTableSelectChange() {
      this.tableLoading = true
      clearTimeout(this.timerId)
      if (this.tableName) {
        this.tableIndex = this.tableOptions.findIndex(
          item => item.value === this.tableName
        )
        this.tableIndex++
      } else {
        this.tableIndex = null
        this.tableData = this.sourceTableData
      }

      this.timerId = setTimeout(() => {
        this.tableLoading = false
      }, 300)
    },

    handleReset() {
      this.tableName = ''
      this.tableIndex = null
      this.tableData = []
      this.sourceTableData = []
    },

    handleClose() {
      this.dialogVisible = false
      this.handleReset()
    },

    handleConfirm() {
      this.handleClose()
      this.$emit('on-confirm')
    }
  }
}
</script>

<style lang="scss" scoped>
.datasource-dialog {
  ::v-deep {
    .el-dialog__body {
      padding: 20px;
    }
  }
}
.icredit-form {
  @include icredit-form;

  ::v-deep {
    .el-form-item--small.el-form-item {
      margin-bottom: 0;
    }
  }
}

.form-detail {
  .label-text {
    font-family: PingFangSC, PingFangSC-Regular;
    font-weight: 400;
    text-align: left;
    // color: #262626;
  }

  .label-text-des {
    max-height: 100px;
    overflow: auto;
  }
}

.detail-row,
.detail-form,
.detail-table {
  margin-top: 30px;

  .banner-title {
    position: relative;
    margin: 30px 0;
    text-align: left;

    .text {
      height: 20px;
      font-size: 14px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      text-align: left;
      color: #262626;
      line-height: 20px;
      margin-left: 10px;
    }

    &::before {
      content: '';
      position: absolute;
      top: 1px;
      left: 0;
      width: 4px;
      height: 18px;
      background: #1890ff;
      border-radius: 0px 2px 2px 0px;
    }
  }

  &-item {
    margin-top: 20px;
    margin-left: 10px;

    .value {
      margin-left: 24px;
    }
  }
}
</style>
