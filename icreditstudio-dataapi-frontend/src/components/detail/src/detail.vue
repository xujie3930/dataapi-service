<!--
 * @Description: 详情
 * @Date: 2022-03-02
-->

<template>
  <Dialog
    hide-footer
    class="detail-dialog"
    ref="baseDialog"
    :width="width"
    :visible="visible"
    :title="options.title"
    @on-change="$emit('on-change', $event)"
  >
    <div class="detail-row" v-loading="loading">
      <div
        :class="[
          'banner-title',
          detailTitleKeyMapping[key].label ? '' : 'banner-title-hide'
        ]"
        v-for="(item, key) in configuration"
        :key="key"
      >
        <div v-if="detailTitleKeyMapping[key].label" class="text">
          {{ detailTitleKeyMapping[key].label }}
        </div>

        <el-row class="detail-row-wrap">
          <el-col
            class="detail-row-wrap--col"
            :span="list.span || 12"
            :key="list.label"
            v-for="list in filterItem(item)"
          >
            <div class="label">{{ list.label }}</div>

            <slot v-if="list.slot" :name="list.key"></slot>

            <div v-else class="value" :style="{ color: list.color }">
              {{ list.value }}
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <template v-if="showDetailTable && detailTableConfiguration">
      <div
        class="detail-table"
        :key="key"
        v-for="(table, key) in tableConfiguration"
      >
        <div class="banner-title">
          <div class="text">{{ detailTableTitleKeyMapping[key].label }}</div>
        </div>

        <div class="sql-wrap" v-if="table.tableConfig === 'textarea'">
          {{ table.tableData }}
        </div>

        <JTable
          v-else
          ref="editTable"
          v-loading="table.tableLoading"
          :table-data="table.tableData"
          :table-configuration="table.tableConfig"
        />
      </div>
    </template>
  </Dialog>
</template>

<script>
export default {
  name: 'JDetail',

  model: {
    prop: 'visible',
    event: 'on-change'
  },

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    showDetailTable: {
      type: Boolean,
      default: false
    },

    loading: {
      type: Boolean,
      default: false
    },

    width: {
      type: String,
      default: '850px'
    },

    detailTableConfiguration: {
      type: Object
    },

    detailConfiguration: {
      type: Object,
      default: () => ({})
    },

    detailTitleKeyMapping: {
      type: Object,
      default: () => ({})
    },

    detailTableTitleKeyMapping: {
      type: Object,
      default: () => ({})
    },

    fetchDetailData: {
      type: Function,
      default: null
    }
  },

  data() {
    return {
      options: {},
      dialogVisible: false
    }
  },

  computed: {
    configuration() {
      const showDetailModule = {}
      for (const key in this.detailTitleKeyMapping) {
        if (
          'visible' in this.detailTitleKeyMapping[key]
            ? this.detailTitleKeyMapping[key].visible
            : true
        ) {
          showDetailModule[key] = this.detailConfiguration[key]
        }
      }
      return showDetailModule
    },

    tableConfiguration() {
      const temp = {}
      for (const key in this.detailTableConfiguration) {
        if (this.detailTableConfiguration[key].visible)
          temp[key] = this.detailTableConfiguration[key]
      }

      return temp
    }
  },

  methods: {
    open(options) {
      this.options = options
      this.fetchDetailData(options)
    },

    filterItem(item) {
      return item?.filter(label => ('hide' in label ? label.hide : true))
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-dialog {
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
      margin-bottom: 17px;
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

  .sql-wrap {
    text-align: left;
  }

  .banner-title-hide {
    &::before {
      display: none;
    }
  }

  &-wrap {
    margin-left: 10px;

    &--col {
      @include flex(flex-start);
      margin-bottom: 17px;
    }
    .label {
      width: 100px;
    }

    .value {
      width: calc(100% - 100px);
      max-height: 80px;
      overflow-x: hidden;
      overflow-y: auto;
      margin-left: 24px;
    }
  }
}
</style>
