<!--
 * @Description: 树形选择框
 * @Date: 2022-03-29
-->
<template>
  <div class="j-transfer-tree">
    <TransferTreePanel
      :data="leftTreeData"
      v-bind="$props"
      @select-change="handleLeftSelectChange"
    />
    <div class="j-transfer-tree_buttons">
      <el-button
        circle
        class="btn"
        size="mini"
        type="primary"
        icon="el-icon-caret-right"
        :disabled="!leftSelectedData.length"
        @click="transferTreeData(leftSelectedData, 'right')"
      />
      <el-button
        circle
        class="btn"
        size="mini"
        type="primary"
        icon="el-icon-caret-left"
        :disabled="!rightSelectedData.length"
        @click="transferTreeData(rightSelectedData, 'left')"
      />
    </div>
    <TransferTreePanel
      ref="rightTreePanel"
      :data="rightTreeData"
      v-bind="$props"
      @select-change="handleRightSelectChange"
    />
  </div>
</template>

<script>
import TransferTreePanel from './transfer-tree-panel'

export default {
  name: 'JTransferTree',
  components: { TransferTreePanel },
  props: {
    leftTreeData: {
      type: Array,
      default() {
        return []
      }
    },
    rightTreeData: {
      type: Array,
      default() {
        return []
      }
    },
    title: {
      type: String,
      default: '全选'
    },
    filterMethod: Function,
    leftDefaultChecked: {
      type: Array,
      default() {
        return []
      }
    },
    rightDefaultChecked: {
      type: Array,
      default() {
        return []
      }
    },

    filterable: {
      type: Boolean,
      default: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    props: {
      type: Object,
      default() {
        return {
          label: 'label',
          key: 'key',
          disabled: 'disabled'
        }
      }
    }
  },

  data() {
    return {
      leftSelectedData: [],
      rightSelectedData: [],
      checkedData: {}
    }
  },

  methods: {
    handleLeftSelectChange(data, checkedData) {
      console.log(data, 'data')
      this.leftSelectedData = data
      this.checkedData = checkedData
      this.$emit('left-select-change', data)
    },

    handleRightSelectChange(data, checkedKeys) {
      this.rightSelectedData = data
      this.checkedKeys = checkedKeys
      this.$emit('right-select-change', data)
    },

    transferTreeData(data, to) {
      this.$emit('transfer-data', data, to, this.checkedData)
    },

    removeNode(data) {
      this.$refs.rightTreePanel.removeNode(data)
    },

    setCheckedKeys(keys, leafOnly) {
      this.$refs.rightTreePanel.setCheckedKeys(keys, leafOnly)
    },

    setCheckedNodesCount() {
      this.$refs.rightTreePanel.setCheckedNodesCount()
    }
  }
}
</script>

<style lang="scss" scoped>
.j-transfer-tree {
  @include flex(space-between);
  // border: 1px solid #ddd;

  &_buttons {
    @include flex(space-between, center, column);
    width: 80px;
    height: 80px;

    .el-button--mini.is-circle {
      padding: 7px !important;
      margin-left: 0;
    }
  }
}
</style>
