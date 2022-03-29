<template>
  <div class="jnh-transfer-table jui-transfer-table-container">
    <el-row ref="scrollBox" class="scroll-box">
      <el-col :span="11" align="left">
        <div class="table-desc">
          <span>{{ titles[0] }}</span>
          <span v-if="showTableLength">{{ elLeftData.length }}</span>
        </div>
        <div class="transfer-table-filter">
          <el-input
            :placeholder="tableFilterConfig.placeholder || '请输入'"
            v-model="leftFilter"
          >
          </el-input>
        </div>
        <div class="table-container">
          <j-table
            ref="leftTable"
            v-loading="tableLoading"
            :table-configuration="leftTableConfiguration"
            :table-data="
              elLeftData | filterTableList(tableFilterConfig.prop, leftFilter)
            "
            @selection-change="handleSelectChange($event, 'left')"
          ></j-table>
        </div>
      </el-col>
      <el-col
        :span="2"
        align="center"
        class="btn-group"
        :style="{
          marginTop: elLeftData.length || elLeftData.length ? '250px' : '100px'
        }"
      >
        <el-button
          circle
          class="btn"
          size="mini"
          type="primary"
          icon="el-icon-caret-left"
          :disabled="!rightSelected.length"
          @click="transToLeft"
        />
        <el-button
          circle
          class="btn"
          size="mini"
          type="primary"
          icon="el-icon-caret-right"
          :disabled="!leftSelected.length"
          @click="transToRight"
        />
      </el-col>
      <el-col :span="11" align="left">
        <div class="table-desc">
          <span>{{ titles[1] }}</span>
          <span v-if="showTableLength">{{ elRightData.length }}</span>
        </div>
        <div class="transfer-table-filter">
          <el-input
            :placeholder="tableFilterConfig.placeholder || '请输入'"
            v-model="rightFilter"
          ></el-input>
        </div>
        <div class="table-container">
          <j-table
            ref="rightTable"
            v-loading="tableLoading"
            :table-configuration="rightTableConfiguration"
            :table-data="
              elRightData | filterTableList(tableFilterConfig.prop, rightFilter)
            "
            @selection-change="handleSelectChange($event, 'right')"
          ></j-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import _ from 'lodash'
import JTable from '~/components/table'

export default {
  name: 'JTransferTable',

  components: { JTable },

  props: {
    tableLoading: {
      type: Boolean,
      default: false
    },
    tableFilterConfig: {
      type: Object
    },
    leftTableConfiguration: {
      type: Object
    },
    rightTableConfiguration: {
      type: Object,
      default() {
        return {
          ..._.cloneDeep(this.leftTableConfiguration),
          refName: 'rightTable'
        }
      }
    },
    leftData: {
      type: Array,
      default: () => []
    },
    rightData: {
      type: Array,
      default: () => []
    },
    titles: {
      type: Array,
      default: () => ['', '']
    },
    showTableLength: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      leftFilter: '',
      rightFilter: '',
      tableData: [],
      elLeftData: [], // 左表数据数据
      leftSelected: [], // 左表选中项
      elRightData: [], // 右表数据
      rightSelected: [] // 右表选中项
    }
  },

  filters: {
    filterTableList(lists, key, model) {
      return lists.filter(e => e[key].includes(model))
    }
  },

  methods: {
    init() {
      const { refName } = this.leftTableConfiguration
      this.leftFilter = ''
      this.rightFilter = ''
      this.$refs.leftTable.$refs[refName].clearSelection()
      this.$refs.rightTable.$refs[
        [this.rightTableConfiguration.refName]
      ].clearSelection()
      const { leftData, rightData } = this
      this.elLeftData = leftData
      this.elRightData = rightData
    },

    handleSelectChange(data, type) {
      switch (type) {
        case 'left':
          this.leftSelected = data || []
          this.updateData()
          break
        case 'right':
          this.rightSelected = data || []
          this.updateData()
          break
        default:
          break
      }
    },

    updateData() {
      this.$emit('update:leftData', this.elLeftData)
      this.$emit('update:rightData', this.elRightData)
    },

    transToRight() {
      const { leftSelected } = this
      this.elRightData = [...leftSelected, ...this.elRightData]
      this.elLeftData = this.elLeftData.filter(
        item => !leftSelected.some(s => s === item)
      )
      this.$refs.leftTable.$refs[
        this.leftTableConfiguration.refName
      ].clearSelection()
      this.updateData()
    },

    transToLeft() {
      const { rightSelected } = this
      this.elLeftData = [...rightSelected, ...this.elLeftData]
      this.elRightData = this.elRightData.filter(
        item => !rightSelected.some(s => s === item)
      )
      this.$refs.rightTable.$refs[
        [this.rightTableConfiguration.refName]
      ].clearSelection()
      this.updateData()
    }
  }
}
</script>

<style lang="scss">
.jui-transfer-table-container {
  .scroll-box {
    height: auto;
  }

  .table-desc {
    height: 70px;
    opacity: 1;
    font-size: 18px;
    font-family: PingFangSC, PingFangSC-Medium;
    font-weight: 500;
    text-align: center;
    color: #323232;
    margin: 0 auto;
    line-height: 70px;

    span {
      font-weight: 500;
      text-align: center;
      color: #323232;
      opacity: 1;
      font-size: 18px;
      font-family: PingFangSC, PingFangSC-Medium;
      line-height: 25px;
    }
  }

  .transfer-table-filter {
    margin-bottom: 5px;
  }

  .btn-group {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .btn {
      margin: 16px 0;
    }
  }

  ::v-deep .el-table--border th {
    border-right: none;
  }

  ::v-deep .el-table--border td {
    border-right: none;
  }
}
</style>
