<template>
  <el-select
    ref="select"
    class="iframe-select-tree"
    :value="selectLabel"
    :multiple="multiple"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    collapse-tags
    @clear="handleClear"
  >
    <el-input
      v-model="filterText"
      class="iframe-select-tree__input"
      size="mini"
      :placeholder="inputPlaceholder"
    />
    <el-option
      class="iframe-select-tree__option"
      :value="selectLabel.toString()"
      :label="selectLabel.toString()"
    >
      <el-tree
        id="tree-option"
        ref="selectTree"
        class="iframe-select-tree__tree"
        :accordion="accordion"
        :data="options"
        :props="usedProps"
        :show-checkbox="multiple"
        :node-key="usedProps.value"
        :default-expand-all="defaultExpandAll"
        :default-expanded-keys="defaultExpandedKey"
        :filter-node-method="filterNode"
        @node-click="handleNodeClick"
        @check-change="handleCheckChange"
      />
    </el-option>
  </el-select>
</template>
<script>
const PROPS_DEFAULT = {
  value: 'id', // ID字段名
  label: 'label', // 显示名称
  children: 'children' // 子级字段名
}
export default {
  name: 'JSelectTree',
  props: {
    /* 配置项 */
    props: {
      type: Object,
      default: () => {
        return PROPS_DEFAULT
      }
    },
    /* 选项列表数据(树形结构的对象数组) */
    options: {
      type: Array,
      default: () => {
        return []
      }
    },
    /* 初始值 */
    value: {
      default: null
    },
    /* 可清空选项 */
    clearable: {
      type: Boolean,
      default: true
    },
    /* 是否多选 */
    multiple: {
      type: Boolean,
      default: false
    },
    /* 是否禁用 */
    disabled: {
      type: Boolean,
      default: false
    },
    /* 自动收起 */
    accordion: {
      type: Boolean,
      default: false
    },
    /* 默认展开所有节点 */
    defaultExpandAll: {
      type: Boolean,
      default: true
    },
    /* select placeholder */
    placeholder: {
      type: String,
      default: '请选择'
    },
    /* select input placeholder */
    inputPlaceholder: {
      type: String,
      default: '请输入关键字检索'
    }
  },

  data() {
    return {
      selectValue: this.value, // 初始值
      selectLabel: '',
      defaultExpandedKey: [],
      filterText: '',
      labelsByOption: {} // options中的树数据转化为的键为id 值为label的对象
    }
  },

  computed: {
    usedProps() {
      return {
        ...PROPS_DEFAULT,
        ...this.props
      }
    }
  },
  watch: {
    /* 监听初始值 */
    value() {
      this.selectValue = this.value
      this.initHandle()
    },
    /* 输入触发节点过滤 */
    filterText(val) {
      this.$refs.selectTree.filter(val)
    }
  },
  mounted() {
    this.initScroll() // 解决双滚动条
    this.labelsByOption = this.recursiveTree(this.options) // 生成labelsByOption
    this.initHandle() // 初始化数据
  },
  methods: {
    // 初始化值
    initHandle() {
      if (this.selectValue) {
        // 有值则回显
        if (this.multiple) {
          // 多选
          this.selectLabel = this.selectValue.map(e => this.labelsByOption[e]) // 初始化显示label
          this.$refs.selectTree.setCheckedKeys(this.selectValue) // 设置默认选中
        } else {
          // 单选
          this.selectLabel = this.$refs.selectTree.getNode(
            this.selectValue
          ).data[this.usedProps.label] // 初始化显示label
          this.$refs.selectTree.setCurrentKey(this.selectValue) // 设置默认选中
          this.defaultExpandedKey = [this.selectValue] // 设置默认展开
        }
      }
    },
    // 初始化滚动条
    initScroll() {
      this.$nextTick(() => {
        const scrollWrap = document.querySelectorAll(
          '.el-scrollbar .el-select-dropdown__wrap'
        )[0]
        const scrollBar = document.querySelectorAll(
          '.el-scrollbar .el-scrollbar__bar'
        )
        scrollWrap.style.cssText =
          'margin: 0px; max-height: none; overflow: hidden;'
        scrollBar.forEach(ele => {
          Object.assign(ele, { style: { width: 0 } })
          // ele.style.width = 0
        })
      })
    },
    /**
     * 把树的每一项改为{[id]: [label]}
     * @param {Array} opt
     * @return {Array} tree
     */
    recursiveTree(opt) {
      return opt.reduce((total, current) => {
        return Object.assign(
          total,
          this.getIdLabel(current),
          current[this.usedProps.children] &&
            this.recursiveTree(current[[this.usedProps.children]])
        )
      }, {})
    },
    /**
     * 转换为key是原对象id 值为原对象label的新对象
     * @param {{id: *, label: *}} obj
     * @return {object} {[id]: [label]}
     */
    getIdLabel(obj) {
      return { [obj[this.usedProps.value]]: obj[this.usedProps.label] }
    },
    /**
     * 节点选中
     * @param node
     */
    handleNodeClick(node) {
      this.$emit('nodeClick', node) // 传值给父组件
      if (!this.multiple) {
        // 单选的时候 node 点击赋值
        this.selectLabel = node[this.usedProps.label] // 获取label
        this.selectValue = node[this.usedProps.value] // 获取value
        this.$emit('input', this.selectValue) // 传值给父组件
        this.$refs.select.blur()
      }
    },

    handleCheckChange() {
      const _checked = this.$refs.selectTree.getCheckedKeys()
      this.selectLabel = _checked.map(e => this.labelsByOption[e])
      this.$emit('input', _checked) // 传值给父组件
      this.selectValue = _checked
    },
    /**
     * 树节点过滤
     * @param value
     * @param data
     * @returns {boolean}
     */
    filterNode(value, data) {
      if (!value) return true
      return data[this.usedProps.label].indexOf(value) !== -1
    },
    // select clear
    handleClear() {
      this.selectLabel = ''
      this.selectValue = null
      this.defaultExpandedKey = []
      this.$refs.selectTree.setCheckedKeys([])
      this.$emit('input', null)
    }
  }
}
</script>
