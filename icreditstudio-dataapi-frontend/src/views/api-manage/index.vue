<!--
 * @Description: 数据服务
 * @Date: 2022-02-18
-->
<template>
  <div class="data-service" :style="style">
    <aside
      class="data-service-aside"
      :style="{ pointerEvents: opType ? 'none' : 'unset' }"
    >
      <header class="data-service-aside__header">
        <h3 class="title">API开发</h3>
        <div>
          <el-dropdown
            trigger="click"
            placement="bottom-start"
            @command="handleCommandClick"
          >
            <i
              class="header-icon el-icon-circle-plus-outline"
              :style="{ color: opType ? '#bcbec2' : '' }"
            ></i>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="process">
                新建业务流程
              </el-dropdown-item>
              <el-dropdown-item command="group">新建API分组</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <i
            :class="[
              'header-icon',
              'el-icon-refresh',
              isRefreshTreeData ? 'is-refresh' : ''
            ]"
            :style="{ color: opType ? '#bcbec2' : '' }"
            @click="handleRefreshClick"
          ></i>
        </div>
      </header>

      <div class="search-select">
        <el-input
          class="text-select"
          clearable
          placeholder="请输入关键词搜索"
          size="mini"
          v-model.trim="selectValue"
          @input="fetchTreeDataByName"
        >
          <!-- @clear="fetchBusinessProcessList()" -->
        </el-input>
      </div>

      <el-tree
        class="tree"
        ref="tree"
        node-key="id"
        highlight-current
        v-loading="isTreeLoading"
        :lazy="isTreeLazy"
        :prop="treeProps"
        :data="treeData"
        :load="fetchTreeData"
        :expand-on-click-node="true"
        :default-expanded-keys="defalutExpandKey"
        @current-change="handleNodeChangeClick"
        @node-contextmenu="handleNodeContextmenuClick"
        @node-click="handleNodeExpandClick"
        @node-expand="handleCloseContentMenuClick"
        @node-collapse="handleCloseContentMenuClick"
      >
        <div slot-scope="{ node, data }" :id="node.id" class="custom-tree-node">
          <JSvg class="jsvg-icon" :svg-name="data.icon"></JSvg>

          <el-input
            style="width: 100%"
            size="mini"
            v-if="data.isRename"
            v-model.trim="data.name"
            @blur="handleTreeNodeInputBlur(data, node)"
          ></el-input>

          <el-tooltip
            v-else
            effect="light"
            :disabled="isTooltipDisabled"
            :content="data.name"
            placement="right"
          >
            <span
              class="node-label"
              @mouseenter="handleMouseEnterClick"
              @dblclick.stop.prevent="handleTreeDoubleClick(data)"
              >{{ data.name }}
            </span>
          </el-tooltip>
        </div>
      </el-tree>
    </aside>

    <div v-if="!isFirstNodeHasChild" class="data-service-empty">
      <img class="img" :src="noGroupImg" />
      <span class="text">该业务流程下暂无分组</span>
    </div>

    <transition v-else name="data-service">
      <Crud
        class="data-service-main"
        v-if="!opType"
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
        @handleAddDataServiceApi="handleAddDataServiceApi"
      />

      <GenerateApi
        v-else
        ref="datasourceGenerate"
        :op-type="opType"
        @on-jump="jumpCallback"
        @on-save="saveCallback"
      />
    </transition>

    <!-- 右键菜单 -->
    <div
      class="contentmenu"
      v-if="isShowContentmenu"
      :style="contentMenuStyle"
      @click="handleContentmenuCommandClick"
    >
      <p
        v-if="!curNodeData.workId"
        data-command="addGroup"
        class="contentmenu-label"
      >
        新增API分组
      </p>
      <p
        v-if="curNodeData.id !== '0'"
        data-command="edit"
        class="contentmenu-label"
      >
        编辑
      </p>
      <p
        v-if="curNodeData.id !== '0' && curNodeData.workId"
        data-command="view"
        class="contentmenu-label"
      >
        查看
      </p>
      <p
        v-if="curNodeData.id !== '0'"
        data-command="delete"
        class="contentmenu-label"
      >
        删除
      </p>
    </div>

    <!-- 新增业务流程 -->
    <AddBusinessPorcess
      ref="addProcessDialog"
      @on-close="closeDialogCallback"
    />

    <!-- 新增API分组 -->
    <AddApiGroup ref="addGroupDialog" @on-close="closeDialogCallback" />

    <!-- 历史版本列表 -->
    <VersionLists
      ref="versionLists"
      @edit-api="editApiCallback"
      @on-close="closeVersionDialogCallback"
    />
  </div>
</template>

<script>
import API from '@/api/api'
import GenerateApi from './generate-api'
import AddBusinessPorcess from './add-business-porcess'
import AddApiGroup from './add-api-group'
import VersionLists from './version-lists'
import tableConfiguration from '@/configuration/table/data-service-api'

import formOption from '@/configuration/form/data-service-api'
import crud from '@/mixins/crud'
import { debounce } from 'lodash'
import noGroupImg from '@/assets/images/bg-no-group.png'
import { isUndef } from '@/utils'

export default {
  mixins: [crud],

  components: {
    GenerateApi,
    AddApiGroup,
    AddBusinessPorcess,
    VersionLists
  },

  data() {
    this.fetchTreeDataByName = debounce(this.fetchTreeDataByName, 500)
    return {
      noGroupImg,
      timeId: null,
      opType: '',
      selectValue: '',
      searchLoading: false,
      addPopovervisible: false,
      isShowContentmenu: false,
      isRefreshTreeData: false,
      isTreeLoading: false,
      isTooltipDisabled: false,
      isInterfaceFirstCalling: true,
      isChilInterfaceFirstCalling: true,
      isFirstNodeHasChild: false,
      isTreeLazy: true,
      defalutExpandKey: [],
      selectOptions: [],
      currentTreeNodeId: null,
      formOption,
      tableConfiguration: tableConfiguration(this),
      mixinSearchFormConfig: {
        models: { name: '', type: '', path: '', publishStatus: '', time: [] }
      },
      fetchConfig: { retrieve: { url: '/apiBase/list', method: 'post' } },
      oldTreeData: [],
      treeData: [],
      treeProps: {
        isLeaf: 'leaf'
      },
      contentMenuStyle: {
        top: 0,
        left: '125px'
      },
      contentmenuCommand: '',
      curEditName: '',
      curNode: {},
      curNodeData: {}
    }
  },

  computed: {
    style() {
      return {
        height: window.__POWERED_BY_QIANKUN__ ? 'calc(100vh - 126px)' : '100vh'
      }
    }
  },

  watch: {
    isShowContentmenu(nVal) {
      const { addEventListener, removeEventListener } = document.body
      nVal
        ? addEventListener('click', this.handleCloseContentMenuClick)
        : removeEventListener('click', this.handleCloseContentMenuClick)
    }
  },

  methods: {
    handleAuthorizeClick() {},

    // 点击-历史版本
    handleVersionClick(options) {
      const { row } = options ?? {}
      this.$refs.versionLists.open(row)
    },

    // 点击-选中当前节点
    handleNodeClick(curData, curNode) {
      console.log('curData, curNode=', curData, curNode)
    },

    // 点击-左侧树顶部按钮-新增流程或分组
    handleCommandClick(command) {
      this.currentTreeNodeId = this.$refs?.tree.getCurrentKey()
      const { workId } = this.$refs?.tree.getCurrentNode() || {}
      const options = { currentTreeNodeId: this.currentTreeNodeId, workId }
      switch (command) {
        case 'process':
          this.$refs.addProcessDialog.open(options)
          break

        case 'group':
          this.$refs.addGroupDialog.open(options)
          break
      }
    },

    // 点击-双击左侧树的label进行编辑
    handleTreeDoubleClick(data) {
      console.log(data, 'data')
    },

    // 点击-当前高亮树节点发生更改
    handleNodeChangeClick(data, node) {
      const { id } = data
      const { level, childNodes } = node

      if (level === 1) {
        const { length } = childNodes
        this.isFirstNodeHasChild = length
      } else {
        this.setHighlightCurrentNode(id)
        this.isFirstNodeHasChild = level > 1
        this.$nextTick(() => {
          this.isFirstNodeHasChild &&
            this.$refs.crud &&
            this.mixinHandleReset(false)
          this.mixinRetrieveTableData()
        })
      }
    },

    handleNodeExpandClick() {
      this.handleCloseContentMenuClick()
    },

    // 点击-右键
    handleNodeContextmenuClick(evt, data, node) {
      // 默认API分组无右键操作
      if (data.id === '000') return
      const { left, top } = this.getNextContextMenuPostion(evt)
      this.contentMenuStyle.top = `${top}px`
      this.contentMenuStyle.left = `${left}px`
      this.isShowContentmenu = !this.isShowContentmenu
      this.curNode = node
      this.curNodeData = data
    },

    // 获取-右键菜单栏弹出位置
    getNextContextMenuPostion(evt) {
      const { clientX: x, clientY: y } = evt

      let html = document.documentElement,
        vx = html.clientWidth,
        vy = html.clientHeight,
        mw = 100,
        mh = 100

      return {
        left: x + mw > vx ? vx - mw : x,
        top: y + mh > vy ? vy - mh : y
      }
    },

    // 点击-右键菜单选项选中
    handleContentmenuCommandClick(evt) {
      const { command } = evt.target?.dataset ?? {}
      this.contentmenuCommand = command

      switch (command) {
        case 'edit':
          this.$refs[
            this.curNodeData.workId ? 'addGroupDialog' : 'addProcessDialog'
          ].open({
            ...this.curNodeData,
            opType: 'edit'
          })
          break

        case 'view':
          this.$refs[
            this.curNodeData.workId ? 'addGroupDialog' : 'addProcessDialog'
          ].open({
            ...this.curNodeData,
            opType: 'view'
          })
          break

        case 'delete':
          this.handleTreeNodeDelete()
          break

        case 'addGroup':
          this.$refs.addGroupDialog.open({
            workId: this.curNodeData.id,
            opType: 'addGroup'
          })
          break
      }
    },

    // 点击-节点删除
    handleTreeNodeDelete() {
      const { level } = this.curNode
      const { id, workId } = this.curNodeData
      const paramsMapping = {
        1: {
          method: 'deleteProcessItem',
          message: '业务流程删除成功！',
          tip: '删除该业务流程后，其下所有分组以及API都将全部删除，请确认是否删除该业务流程？'
        },
        2: {
          method: 'deleteApiGroupItem',
          message: 'API分组删除成功！',
          tip: '删除该分组后，其下API都将全部删除，请确认是否删除该分组？'
        }
      }

      const { method, message, tip } = paramsMapping[level]
      this.$confirm(tip, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          API[method]({ id }).then(({ success, data }) => {
            if (success && data) {
              const { apiGroupId, workId: wid } = data
              this.$notify.success({
                title: '操作结果',
                message,
                duration: 1500
              })

              this.$refs.tree.remove(this.curNodeData)
              if (level === 2 && this.currentTreeNodeId === id) {
                // 删除某个流程下的某个API分组以及回显相应的状态
                apiGroupId
                  ? this.setHighlightCurrentNode(apiGroupId, true)
                  : this.setHighlightCurrentNode(workId)
                this.isFirstNodeHasChild = apiGroupId
              } else if (level === 1) {
                // 删除某个业务流程节点
                const { workId } = this.$refs.tree.getCurrentNode() ?? {}
                const isSameHighlightNode =
                  workId === id || this.currentTreeNodeId === id
                isSameHighlightNode && this.setHighlightCurrentNode(wid)
              }
            }
          })
        })
        .catch(() => {})
    },

    // 失焦-保存编辑名称
    handleTreeNodeInputBlur(data, node) {
      const { parent, level } = node
      const { id, workId, name } = data
      const { childNodes } = parent
      console.log(level, data, node, ';ooo')
      level === 2
        ? this.setGroupNodeName({ id, newName: name, workId, childNodes })
        : this.setProcessNodeName(id, name)
    },

    // 聚焦-分组名称或业务流程名称
    handleTreeNodeInputFocus() {
      const { level, parent } = this.curNode
      const { id, workId } = this.curNodeData
      const { childNodes } = parent
      level === 2
        ? this.setCurEditTreeData(id, workId, childNodes, true)
        : Object.assign(this.curNode.data, { isRename: true })
    },

    // 右键-业务流程重命名
    setProcessNodeName(id, newName) {
      API.editProcessName({ id, newName })
        .then(({ success, data }) => {
          if (success && data) {
            this.$notify.success({
              title: '操作结果',
              message: '修改成功！',
              duration: 2000
            })
            Object.assign(this.curNode.data, { isRename: false })
          }
        })
        .catch(() => {})
    },

    // 右键-API分组重命名
    setGroupNodeName(options) {
      const { id, newName, workId, childNodes } = options
      API.editApiGroupName({ id, newName })
        .then(({ success, data }) => {
          if (success && data) {
            this.$notify.success({
              title: '操作结果',
              message: '修改成功！',
              duration: 2000
            })
            this.setCurEditTreeData(id, workId, childNodes, false)
          }
        })
        .catch(() => {})
    },

    // 设置-重命名当前节点名称
    setCurEditTreeData(id, workId, childNodes, isEdit) {
      const children = childNodes.map(({ data }) => {
        return {
          ...data,
          isRename: data.id === id && isEdit
        }
      })

      this.$refs.tree.updateKeyChildren(workId, children)
    },

    handleCloseContentMenuClick() {
      this.isShowContentmenu = false
    },

    // 鼠标-移入判断是否需要显示tootltip
    handleMouseEnterClick(evt) {
      const { clientWidth, scrollWidth } = evt.target
      this.isTooltipDisabled = clientWidth >= scrollWidth
    },

    // 点击-打开数据源生成界面
    handleAddDataServiceApi() {
      this.opType = 'add'
      const { opType, currentTreeNodeId } = this
      const { data, parent } = this.$refs?.tree.getNode(currentTreeNodeId) ?? {}
      const { id: pid, name } = parent?.data ?? {}
      const cascaderOptions = [
        {
          value: pid,
          label: name,
          children: [{ label: data.name, value: data.id }]
        }
      ]
      const options = { opType, currentTreeNodeId, cascaderOptions }

      this.$nextTick(() => {
        this.$refs.datasourceGenerate.open(options)
      })
    },

    // 点击-刷新左侧树数据
    handleRefreshClick() {
      this.isRefreshTreeData = true
      clearTimeout(this.timeId)
      this.fetchBusinessProcessList()
    },

    // 设置-进入界面高亮左侧树第一个节点或第一个子节点
    setHighlightNode(treeData, nodeType) {
      const [{ id }] = treeData
      if (id) {
        this.currentTreeNodeId = id
        this.defalutExpandKey = [id]
        this.$refs?.tree?.setCurrentKey(id)
      }
      nodeType === 'child' && this.mixinRetrieveTableData()
    },

    // 设置-左侧树需要被高亮的节点
    setHighlightCurrentNode(id, isCallTableData) {
      this.defalutExpandKey = [id]
      this.currentTreeNodeId = id
      this.$nextTick(() => {
        const { tree } = this.$refs
        tree && tree.setCurrentKey(id)

        // true->则调用右侧表格列表接口
        isCallTableData && this.mixinRetrieveTableData()
      })
    },

    // 回调-新增数据源生成页面切换
    jumpCallback(opType) {
      this.opType = ''
      this.mixinRetrieveTableData()
      if (opType === 'edit') {
        this.handleVersionClick()
      }
    },

    // 回调-新增API
    saveCallback(saveType) {
      if (saveType === 1) {
        this.opType = ''
        saveType === 1 && this.mixinRetrieveTableData()
      }
    },

    // 回调-版本历史列表
    editApiCallback(options) {
      console.log(options, 'dejji')
      this.opType = 'edit'
      this.$refs.versionLists.close()
      this.$nextTick(() =>
        this.$refs.datasourceGenerate.open({ ...options, opType: this.opType })
      )
    },

    // 回调-API历史版本弹窗关闭状态
    closeVersionDialogCallback(visible) {
      !visible && this.mixinRetrieveTableData()
    },

    // 回调-新增业务流程或API分组
    async closeDialogCallback(options) {
      const { currentTreeNodeId, command, workId } = options
      this.currentTreeNodeId = currentTreeNodeId
      this.defalutExpandKey = workId
        ? [workId, currentTreeNodeId]
        : [currentTreeNodeId]
      const isFinish = await this.fetchBusinessProcessList()
      if (isFinish) {
        this.selectValue = ''
        command === 'process' && this.setHighlightCurrentNode(workId)

        // 新增分组左侧树高亮节点
        command === 'group' && this.setHighlightCurrentNode(workId)
      }
    },

    // 获取-某个分组下的api列表
    fetchApiGroupList() {
      const params = {}
      this.isTreeLoading = true
      API.getApiGroupList(params)
        .then(({ success, data }) => {
          if ((success, data)) {
            this.mixinTableData = data
          }
        })
        .finally(() => {
          this.isTreeLoading = false
        })
    },

    // 获取-符合输入的分组以及流程数据
    fetchTreeDataByName() {
      if (this.selectValue === '' || isUndef(this.selectValue)) {
        this.isInterfaceFirstCalling = true
        this.fetchBusinessProcessList()
        return
      }

      this.isTreeLoading = true
      this.isTreeLazy = true
      API.searchProcessOrGroup({ name: this.selectValue })
        .then(({ success, data }) => {
          if (success && data) {
            this.treeData = data.map(
              ({ apiGroup: children, workFlowId: id, workFlowName: name }) => {
                return {
                  id,
                  name,
                  icon: 'process',
                  leaf: false,
                  children: children?.map(item => {
                    return {
                      ...item,
                      icon: 'group',
                      leaf: true
                    }
                  })
                }
              }
            )

            this.isTreeLazy = false
          }
        })
        .finally(() => {
          this.isTreeLoading = false
        })
    },

    // 获取-左侧树懒加载
    fetchTreeData(node, resolve) {
      const {
        level,
        data: { id }
      } = node
      switch (level) {
        case 0:
          this.fetchBusinessProcessList(resolve)
          break

        case 1:
          this.setHighlightCurrentNode(id)
          this.fetchBusinessProcessChildList(id, resolve)
          break

        default:
          return resolve([])
      }
    },

    // 获取-左侧树一级节点数据
    fetchBusinessProcessList(resolve) {
      this.isTreeLoading = true
      return API.getBusinessProcess()
        .then(({ success, data }) => {
          if (success) {
            const treeData = data.map(item => {
              return {
                ...item,
                leaf: false,
                icon: 'process',
                isRename: false,
                oldName: item.name
              }
            })
            this.oldTreeData = treeData
            resolve ? resolve(treeData) : (this.treeData = treeData)

            // 接口首次调用则默认高亮第一个节点
            if (this.isInterfaceFirstCalling) {
              this.isInterfaceFirstCalling = false
              this.setHighlightNode(treeData, 'parent')
            }

            return true
          }
        })
        .catch(() => {
          this.isRefreshTreeData = false
          resolve([])
          return false
        })
        .finally(() => {
          this.isTreeLoading = false
          this.timeId = setTimeout(() => {
            this.isRefreshTreeData = false
          }, 1600)
        })
    },

    // 获取-左侧树二级节点数据
    fetchBusinessProcessChildList(id, resolve) {
      API.getBusinessProcessChild({ workId: id })
        .then(({ success, data: children }) => {
          if (success && children) {
            const data = children
              ?.map(item => {
                return {
                  ...item,
                  leaf: true,
                  icon: 'group',
                  isRename: false,
                  oldName: item.name
                }
              })
              .filter(({ name }) =>
                this.isTreeLazy ? true : name.includes(this.selectValue)
              )

            resolve(data)
            this.isFirstNodeHasChild = !!data.length
            children?.length && this.setHighlightCurrentNode(data[0].id, true)

            // 首次加载
            if (this.isChilInterfaceFirstCalling) {
              this.isChilInterfaceFirstCalling = false
              this.setHighlightNode(data, 'child')
            }
          }
        })
        .catch(() => {
          return resolve([])
        })
    },

    // 拦截-表格请求接口参数拦截
    interceptorsRequestRetrieve(params) {
      const apiGroupId = this.currentTreeNodeId
      const { time, ...restParams } = params
      const publishTimeStart = time?.length ? time[0] : ''
      const publishTimeEnd = time?.length
        ? time[1] + 24 * 60 * 60 * 1000 - 1
        : ''

      return {
        apiGroupId,
        publishTimeStart,
        publishTimeEnd,
        ...restParams
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.data-service {
  @include flex(center, flex-start);
  background: #fff;
  // position: relative;

  &-aside {
    @include flex(flex-start, flex-start, column);
    width: 240px;
    height: 100%;
    overflow-x: hidden;

    &__header {
      @include flex(space-between);
      width: 100%;
      padding: 10px;
      box-sizing: border-box;

      .title {
        height: 25px;
        font-size: 18px;
        font-family: PingFangSC, PingFangSC-Medium;
        font-weight: 500;
        color: #262626;
        line-height: 25px;
      }

      .header-icon {
        font-size: 18px;
        color: #1890ff;
        margin-left: 12px;
        cursor: pointer;
        transform: scale(1);
        transition: transform 0.3s ease-in-out;

        &:hover {
          transform: scale(1.2);
        }
      }

      .is-refresh {
        animation: refreshIcon 1s linear infinite;

        @keyframes refreshIcon {
          0% {
            transform: rotate(0);
          }
          50% {
            transform: rotate(180deg);
          }
          100% {
            transform: rotate(360deg);
          }
        }
      }
    }

    .search-select {
      @include flex;
      position: relative;
      width: 210px;
      height: 32px;
      padding: 0 5px;
      border: 1px solid rgba(0, 0, 0, 0.15);
      background: rgba(255, 255, 255, 0.04);
      border-radius: 4px;
      margin-left: 10px;
      margin-bottom: 10px;

      .search {
        color: #1890ff;
        font-size: 15px;
        cursor: pointer;
      }

      .text-select {
        width: 210px;
      }

      ::v-deep {
        .el-input--mini .el-input__inner {
          border: none;
          padding: 0 5px;
        }

        .el-input__suffix {
          line-height: 28px;
        }
      }
    }

    .tree {
      width: 100%;
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: auto;

      .custom-tree-node {
        @include flex(flex-start, center);
        width: 203px;
        padding-right: 8px;
        font-size: 14px;
        color: #262626;
        overflow-x: hidden;

        box-sizing: border-box;
        cursor: pointer;

        .jsvg-icon {
          width: 15px;
          height: 15px;
          margin: 0 5px;

          .circle {
            width: 6px;
            height: 6px;
            background: #52c41a;
            border-radius: 50%;
            margin-right: 5px;
          }
        }

        .node-label {
          text-align: left;
          width: calc(100% - 15px);
          text-overflow: ellipsis;
          overflow: hidden;
        }

        .right {
          display: none;

          .icon {
            transform: rotate(90deg);
          }
        }

        &:hover > .right {
          display: block;
          &:hover {
            cursor: pointer;
          }
        }
      }

      .is-disabled {
        color: #c0c4cc;
        cursor: not-allowed;
        background-image: none;
        background-color: #fff;
        border-color: #ebeef5;
      }

      ::v-deep {
        .el-tree-node {
          margin-bottom: 5px;
        }

        .el-tree-node.is-current > .el-tree-node__content {
          font-size: 14px;
          background-color: #f0f5ff;

          .custom-tree-node {
            color: #1890ff;
          }

          .right {
            display: block;
          }
        }

        .el-tree-node__children .el-tree-node__expand-icon {
          visibility: hidden;
        }

        .el-input--mini .el-input__inner {
          border: none;
          width: 100%;
        }
      }
    }
  }

  &-main {
    @include flex(flex-start);
    position: relative;
    // flex: 1;
    width: calc(100% - 240px);
    height: 100%;
    border-left: 1px solid #d9d9d9;
    overflow: hidden;
  }

  &-empty {
    @include flex(center, center, column);
    flex: 1;
    height: 100%;
    border-left: 1px solid #d9d9d9;
    box-sizing: border-box;

    .img {
      width: 300px;
      height: 300px;
      margin-top: -50px;
    }

    .text {
      margin-top: 45px;
      font-size: 20px;
      font-family: PingFangSC, PingFangSC-Medium;
      font-weight: 500;
      color: #333;
    }
  }

  .contentmenu {
    @include flex(center, center, column);
    position: absolute;
    width: 100px;
    min-height: 50px;
    max-height: 100px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0px 9px 28px 8px rgba(0, 0, 0, 0.05),
      0px 6px 16px 0px rgba(0, 0, 0, 0.08), 0px 3px 6px -4px rgba(0, 0, 0, 0.12);

    &-label {
      width: 100%;
      cursor: pointer;
      font-size: 14px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.65);
      line-height: 34px;
      text-align: center;
      &:hover {
        color: #1890ff;
        background: #ecf5ff;
      }
    }
  }
}

// .data-service-entry-active,
// .data-service-leave-active {
//   transition: all 0.35s ease-in-out;
// }

// .data-service-enter,
// .data-service-leave-to {
//   transform: translateY(30px);
//   opacity: 0;
// }
</style>
