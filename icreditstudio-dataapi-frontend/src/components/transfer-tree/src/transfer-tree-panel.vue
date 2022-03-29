<template>
  <div class="j-transfer-tree-panel">
    <p class="j-transfer-tree-panel__header">
      <el-checkbox v-model="allChecked" @change="handleAllCheckedChange">
        {{ title }}
      </el-checkbox>

      <span class="total-text"
        >{{ checkedNodesCount }}/{{ totalNodesCount }}</span
      >
    </p>

    <div class="j-transfer-tree-panel__body">
      <el-input
        class="j-transfer-tree-panel__filter"
        v-model="query"
        size="mini"
        :placeholder="placeholder"
        @mouseenter.native="inputHover = true"
        @mouseleave.native="inputHover = false"
        v-if="filterable"
      >
        <i
          slot="prefix"
          :class="['el-input__icon', 'el-icon-' + inputIcon]"
          @click="clearQuery"
        ></i>
      </el-input>

      <el-tree
        class="tree"
        node-key="id"
        ref="tree"
        show-checkbox
        default-expand-all
        highlight-current
        :props="props"
        :data="filteredData"
        :filter-node-method="filterNode"
        @check="treeNodeCheck"
      >
      </el-tree>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    placeholder: {
      type: String,
      default: '请输入'
    },
    title: String,
    filterable: Boolean,
    data: Array,
    filterMethod: Function,
    defaultChecked: Array,
    props: Object
  },

  data() {
    return {
      checked: [],
      checkedNodes: [],
      allChecked: false,
      query: '',
      inputHover: false,
      checkChangeByUser: true,
      checkedNodesCount: 0,

      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },

  computed: {
    filteredData() {
      return this.data.filter(item => {
        if (typeof this.filterMethod === 'function') {
          return this.filterMethod(this.query, item)
        } else {
          // const label = item[this.labelProp] || item[this.keyProp].toString()
          // return label.toLowerCase().indexOf(this.query.toLowerCase()) > -1
          return this.data
        }
      })
    },

    totalNodesCount() {
      return this.flattern(this.data).length ?? 0
    },

    hasNoMatch() {
      return this.query.length > 0 && this.filteredData.length === 0
    },

    inputIcon() {
      return this.query.length > 0 && this.inputHover
        ? 'circle-close'
        : 'search'
    },

    labelProp() {
      return this.props?.label || 'label'
    },

    keyProp() {
      return this.props?.key || 'id'
    },

    disabledProp() {
      return this.props.disabled || 'disabled'
    }
  },

  watch: {
    query(val) {
      this.$refs.tree.filter(val)
    },

    checkedNodes: {
      immediate: true,
      deep: true,
      handler(nVal) {
        this.$emit('select-change', nVal)
      }
    }
  },

  methods: {
    flattern(arr) {
      return arr.reduce((result, item) => {
        if (item.children && Array.isArray(item.children)) {
          return result.concat(this.flattern(item.children)).concat(item)
        }
        return result.concat(item)
      }, [])
    },

    updateAllChecked() {
      const checkableDataKeys = this.checkableData.map(
        item => item[this.keyProp]
      )
      this.allChecked =
        checkableDataKeys.length > 0 &&
        checkableDataKeys.every(item => this.checked.indexOf(item) > -1)
    },

    handleAllCheckedChange(value) {
      this.checkedNodes = value ? this.data : []
      this.checkedNodesCount = value ? this.totalNodesCount : 0
      this.data.forEach(item => {
        this.$refs.tree.setChecked(item, value, true)
      })
    },

    clearQuery() {
      if (this.inputIcon === 'circle-close') {
        this.query = ''
      }
    },

    treeNodeCheck(data, checkedData) {
      const { checkedKeys, checkedNodes } = checkedData
      this.allChecked = checkedKeys.length === this.totalNodesCount
      this.checkedNodesCount = this.$refs.tree.getCheckedKeys().length ?? 0
      this.checkedNodes = checkedNodes
      this.$emit('check', data, checkedData)
    },

    filterNode(value, data) {
      this.$emit('filter-node', value, data)
      if (!value) return true
      return data.label.indexOf(value) !== -1
    }
  }
}
</script>

<style lang="scss" scoped>
.j-transfer-tree-panel {
  width: 50%;
  height: 380px;
  overflow: hidden;
  border: 1px solid #f5f5f5;
  border-radius: 4px;

  &__header {
    @include flex(space-between);
    padding: 0 8px;
    background: #f5f5f5;
    .total-text {
      font-size: 12px;
      color: #909399;
    }
  }

  &__body {
    padding: 10px;
    padding-top: 0;

    .tree {
      max-height: calc(100% - 30px);
      overflow-x: hidden;
      overflow-y: auto;
    }
  }

  &__filter {
    position: relative;
    padding: 5px 0;

    .el-input__icon {
      cursor: pointer;
    }

    /deep/ .el-input__prefix {
      top: 6px;
    }
  }
}
</style>
