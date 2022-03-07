<!--
 * @Description: 数据服务
 * @Date: 2022-02-18
-->
<template>
  <div class="data-service" :style="style">
    <aside class="data-service-aside">
      <header class="data-service-aside__header">
        <h3 class="title">API开发</h3>
        <div>
          <el-dropdown
            trigger="click"
            placement="bottom-start"
            @command="handleCommandClick"
          >
            <i class="header-icon el-icon-circle-plus-outline"></i>
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
          <i slot="suffix" class="search el-icon-search"></i>
        </el-input>
      </div>

      <el-tree
        class="tree"
        ref="tree"
        node-key="id"
        lazy
        draggable
        highlight-current
        v-loading="isTreeLoading"
        :prop="{ isLeaf: 'leaf' }"
        :data="treeData"
        :load="fetchTreeData"
        :expand-on-click-node="true"
        :default-expanded-keys="defalutExpandKey"
        @current-change="handleNodeChangeClick"
      >
        <!-- @node-click="handleNodeExpandClick" -->
        <div slot-scope="{ node, data }" :id="node.id" class="custom-tree-node">
          <JSvg class="jsvg-icon" :svg-name="data.icon"></JSvg>
          <el-tooltip
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

    <transition v-if="isFirstNodeHasChild" name="data-service">
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
        @on-jump="jumpCallback"
        @on-save="saveCallback"
      />
    </transition>

    <div v-else class="data-service-empty">
      <img class="img" :src="noGroupImg" />
      <span class="text">该业务流程下暂无分组</span>
    </div>

    <!-- 新增业务流程 -->
    <AddBusinessPorcess
      ref="addProcessDialog"
      @on-close="closeDialogCallback"
    />

    <!-- 新增API分组 -->
    <AddApiGroup ref="addGroupDialog" @on-close="closeDialogCallback" />

    <VersionLists ref="versionLists" />

    <Detail ref="apiDetail" />
  </div>
</template>

<script>
import API from '@/api/api'
import GenerateApi from './generate-api'
import AddBusinessPorcess from './add-business-porcess'
import AddApiGroup from './add-api-group'
import VersionLists from './version-lists'
import Detail from './version-lists/detail'

import tableConfiguration from '@/configuration/table/data-service-api'

import formOption from '@/configuration/form/data-service-api'
import crud from '@/mixins/crud'
import { debounce } from 'lodash'
import noGroupImg from '@/assets/images/bg-no-group.png'

export default {
  mixins: [crud],

  components: {
    GenerateApi,
    AddApiGroup,
    AddBusinessPorcess,
    VersionLists,
    Detail
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
      isRefreshTreeData: false,
      isTreeLoading: false,
      isTooltipDisabled: false,
      isInterfaceFirstCalling: true,
      isChilInterfaceFirstCalling: true,
      isFirstNodeHasChild: false,
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
      treeData: []
    }
  },

  computed: {
    style() {
      return {
        height: window.__POWERED_BY_QIANKUN__ ? 'calc(100vh - 126px)' : '100vh'
      }
    }
  },

  methods: {
    handleAuthorizeClick() {},

    handleVersionClick({ row }) {
      console.log(row, 'rowrow')
      this.$refs.versionLists.open()
    },

    // 点击-发布或停止发布
    handleUpdateStatusClick({ row }) {
      const { id, publishStatus } = row
      if (publishStatus === 2) {
        this.$confirm(
          '停止发布后，该版本API将不能授权给其他应用，并且已授权的应用也将全部失效，需重新发布该版本API后才能继续被授权的应用调用，请确认是否停止发布该版本API？',
          '停止发布',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
          .then(() => {
            this.handlePublishDataApi(id, 1)
          })
          .catch(() => {})
      } else {
        this.handlePublishDataApi(id, 2)
      }
    },

    handlePublishDataApi(id, publishStatus) {
      API.updateDataApiStatus({ id, publishStatus }).then(
        ({ success, data }) => {
          if (success && data) {
            this.$notify.success({
              title: '操作结果',
              message: `${publishStatus === 2 ? '发布' : '停止发布'}成功！`,
              duration: 1500
            })

            this.mixinRetrieveTableData()
          }
        }
      )
    },

    // 点击-详情
    handleDetailClick({ row }) {
      this.$refs.apiDetail.open(row)
    },

    // 点击-选中当前节点
    handleNodeClick(curData, curNode) {
      console.log('curData, curNode=', curData, curNode)
    },

    // 点击-左侧树顶部按钮
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
    handleNodeChangeClick({ id }) {
      this.setHighlightCurrentNode(id)
      this.mixinRetrieveTableData()
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
    setHighlightCurrentNode(id) {
      this.defalutExpandKey = [id]
      this.currentTreeNodeId = id
      console.log(id, 'ididid')
      this.$nextTick(() => {
        const { tree } = this.$refs
        tree && tree.setCurrentKey(id)
      })
    },

    // 回调-新增数据源生成页面切换
    jumpCallback() {
      this.opType = ''
      this.mixinRetrieveTableData()
    },

    // 回调-新增API
    saveCallback(saveType) {
      if (saveType === 1) {
        this.opType = ''
        saveType === 1 && this.mixinRetrieveTableData()
      }
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
        command === 'process' && this.setHighlightCurrentNode(workId)
        command === 'group' && this.mixinRetrieveTableData()
      }
    },

    // 获取某个分组下的api列表
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
      this.isTreeLoading = true
      API.searchProcessOrGroup({ name: this.selectValue })
        .then(({ success, data }) => {
          if (success) {
            this.treeData = data.map(
              ({ apiGroup: children, workFlowId: id, workFlowName: name }) => {
                return {
                  id,
                  name,
                  icon: 'process',
                  leaf: false,
                  children: children?.map(item => ({
                    ...item,
                    icon: 'group',
                    leaf: true
                  }))
                }
              }
            )
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

    // 获取-左侧树节点数据
    fetchBusinessProcessList(resolve) {
      this.isTreeLoading = true
      return API.getBusinessProcess()
        .then(({ success, data }) => {
          if (success) {
            const treeData = data.map(item => {
              return {
                ...item,
                leaf: false,
                icon: 'process'
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
            const data = children?.map(item => {
              return {
                ...item,
                leaf: true,
                icon: 'group'
              }
            })

            resolve(data)

            this.isFirstNodeHasChild = !!data.length
            children?.length && this.setHighlightCurrentNode(children[0].id)

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

  &-aside {
    @include flex(flex-start, flex-start, column);
    width: 240px;
    height: 100%;

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
      overflow-y: auto;

      .custom-tree-node {
        @include flex(flex-start, center);
        width: 203px;
        padding-right: 8px;
        font-size: 14px;
        color: #262626;

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
      }
    }
  }

  &-main {
    @include flex(flex-start);
    position: relative;
    // flex: 1;
    width: 100%;
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
}

.data-service-entry-active,
.data-service-leave-active {
  transition: all 0.35s ease-in-out;
}

.data-service-enter,
.data-service-leave-to {
  transform: translateY(30px);
  opacity: 0;
}
</style>