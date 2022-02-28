<!--
 * @Author: lizheng
 * @Description: 详情
 * @Date: 2022-02-25
-->

<template>
  <Dialog
    hide-footer
    class="datasource-dialog"
    ref="baseDialog"
    width="850px"
    title="详情"
  >
    <div class="detail-row" v-loading="tableLoading">
      <div class="banner-title" v-for="(item, key) in renderParams" :key="key">
        <div class="text">{{ key === 'base' ? '基本信息' : '选择表' }}</div>

        <el-row class="detail-row-wrap">
          <el-col
            class="detail-row-wrap--col"
            :span="12"
            :key="list.label"
            v-for="list in item"
          >
            <div class="label">{{ list.label }}</div>
            <div class="value" :style="{ color: list.color }">
              {{ list.value }}
            </div>
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
import { cloneDeep } from 'lodash'
import { API_TYPE, STATUS_MAPPING, API_MODE } from '@/config/constant'

export default {
  data() {
    return {
      dialogVisible: false,
      tableLoading: false,
      tableData: [],
      tableOptions: [],
      tableConfig: tableServiceApiDetailTableConfig,

      renderParams: {
        base: [
          {
            label: 'API类型',
            value: '',
            key: 'type',
            formatter: value => API_TYPE[value]
          },
          { label: 'API名称', value: '', key: 'name' },
          {
            label: 'API模式',
            value: '',
            key: 'model',
            formatter: value => API_MODE[value]
          },
          { label: 'API Path', value: '', key: 'apipath,' },
          { label: '请求方式', value: '', key: 'requestType' },
          { label: '返回类型', value: '', key: 'responseType' },
          { label: '所属分组', value: '', key: 'destination' },
          {
            label: '发布状态',
            value: '',
            key: 'publishStatus',
            color: '',
            formatter: value => STATUS_MAPPING[value].name
          },
          {
            label: '版本号',
            value: '',
            key: 'apiVersion',
            formatter: value => `v${value}`
          },
          { label: '创建人', value: '', key: 'createBy' },
          { label: '创建时间', value: '', key: 'createTime' },
          { label: '发布人', value: '', key: 'publishUser' },
          { label: '发布时间', value: '', key: 'publishTime' },
          { label: '备注', value: '', key: 'desc' }
        ],

        table: [
          { label: '数据源名称', value: '', key: 'databaseName' },
          { label: '数据表名称', value: '', key: 'tableName' }
        ]
      },

      checkMapping: { 0: '否', 1: '是' }
    }
  },

  methods: {
    open(options) {
      this.options = options
      this.tableOptions = []
      this.fetchApiDetailData()
      this.$refs.baseDialog.open()
    },

    fetchApiDetailData() {
      this.tableLoading = true
      API.getDataApiDetail({ id: this.options.id })
        .then(({ success, data }) => {
          if (success && data) {
            const { paramList, generateApi, ...restData } = data

            cloneDeep(this.renderParams.base).forEach(
              ({ key, color, formatter }, idx) => {
                const val = key === 'model' ? generateApi?.model : restData[key]
                this.renderParams.base[idx].value = formatter
                  ? formatter(val)
                  : val

                color &&
                  (this.renderParams.base[idx].color = formatter(val).color)
              }
            )

            cloneDeep(this.renderParams.table).forEach(({ key }, idx) => {
              this.renderParams.table[idx].value =
                key in cloneDeep(generateApi) ? generateApi[key] : ''
            })

            this.tableData = paramList?.map(
              ({ isRequest, isResponse, required, ...rest }) => {
                return {
                  isRequest: this.checkMapping[isRequest],
                  isResponse: this.checkMapping[isResponse],
                  required: this.checkMapping[required],
                  ...rest
                }
              }
            )
          }
        })
        .finally(() => {
          this.tableLoading = false
        })
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

  &-wrap {
    margin-left: 10px;

    &--col {
      @include flex(flex-start);
      margin-top: 17px;
    }
    .label {
      width: 80px;
    }

    .value {
      width: calc(100% - 80px);
      max-height: 80px;
      overflow-x: hidden;
      overflow-y: auto;
      margin-left: 24px;
    }
  }
}
</style>
