<!--
 * @Description: 数据服务
 * @Date: 2022-02-18
-->
<template>
  <div class="data-service">
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
        v-loading="isTreeLoading"
        :prop="{ isLeaf: 'leaf' }"
        :data="treeData"
        :load="fetchTreeData"
        :expand-on-click-node="false"
        :default-expanded-keys="defalutExpandKey"
      >
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

    <transition name="data-service">
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

      <DatasourceGenerate v-else @on-jump="jumpCallback" />
    </transition>

    <!-- 新增业务流程 -->
    <AddBusinessPorcess
      ref="addProcessDialog"
      @on-close="closeProcessCallBack"
    />

    <!-- 新增API分组 -->
    <AddApiGroup ref="addGroupDialog" @on-close="closeApiGroupCallBack" />
  </div>
</template>

<script>
import API from '@/api/api'
import DatasourceGenerate from './source'
import AddBusinessPorcess from './add-business-porcess.vue'
import AddApiGroup from './add-api-group.vue'
import tableConfiguration from '@/configuration/table/data-service-api'
import formOption from '@/configuration/form/data-service-api'
import crud from '@/mixins/crud'
import { debounce } from 'lodash'

export default {
  mixins: [crud],

  components: { DatasourceGenerate, AddApiGroup, AddBusinessPorcess },

  data() {
    this.fetchTreeDataByName = debounce(this.fetchTreeDataByName, 500)
    return {
      opType: '',
      selectValue: '',
      searchLoading: false,
      addPopovervisible: false,
      isRefreshTreeData: false,
      isTreeLoading: false,
      defalutExpandKey: [],
      isTooltipDisabled: false,
      selectOptions: [],
      formOption,
      tableConfiguration: tableConfiguration(this),
      mixinTableData: [
        { status: 0 },
        { name: '123434', status: 1 },
        { status: 2 }
      ],
      mixinSearchFormConfig: {
        models: { name: '', type: '', path: '', status: '', time: [] }
      },
      oldTreeData: [],
      treeData: [
        {
          name: 'datax_web',
          icon: 'process',
          type: 0,
          id: 1,
          leaf: false,
          disabled: false,
          category: 'database',
          visible: false,
          children: [
            {
              name: 'h_app_sysytem',
              icon: 'group',
              type: 1,
              id: 2,
              leaf: true,
              disabled: false,
              category: 'table',
              visible: true
            }
          ]
        }
      ]
    }
  },

  created() {
    // this.fetchBusinessProcessList()
  },

  methods: {
    // 点击-选中当前节点
    handleNodeClick(curData, curNode) {
      console.log('curData, curNode=', curData, curNode)
    },

    // 点击-左侧树顶部按钮
    handleCommandClick(command) {
      switch (command) {
        case 'process':
          this.$refs.addProcessDialog.open()
          break

        case 'group':
          this.$refs.addGroupDialog.open()
      }
    },

    // 点击-双击左侧树的label进行编辑
    handleTreeDoubleClick(data) {
      console.log(data, 'data')
    },

    // 鼠标-移入判断是否需要显示tootltip
    handleMouseEnterClick(evt) {
      const { clientWidth, scrollWidth } = evt.target
      this.isTooltipDisabled = clientWidth >= scrollWidth
    },

    // 新增API
    handleAddDataServiceApi() {
      this.opType = 'add'
    },

    // 刷新
    handleRefreshClick() {
      this.isRefreshTreeData = !this.isRefreshTreeData
    },

    jumpCallback() {
      this.opType = ''
    },

    // 回调-新增业务流程弹
    closeProcessCallBack(opType) {
      opType === 'save' && this.fetchBusinessProcessList()
    },

    // 回调-新转增API分组
    closeApiGroupCallBack() {
      this.fetchBusinessProcessList()
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
      console.log(node, 'nodenode')
      const { level, data } = node
      switch (level) {
        case 0:
          this.fetchBusinessProcessList(resolve)
          break

        case 1:
          this.fetchBusinessProcessChildList(data.id, resolve)
          break

        default:
          return resolve([])
      }
    },

    // 获取-左侧树节点数据
    fetchBusinessProcessList(resolve) {
      this.isTreeLoading = true
      API.getBusinessProcess()
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
          }
        })
        .catch(() => {
          return resolve([])
        })
        .finally(() => {
          this.isTreeLoading = false
        })
    },

    // 获取-左侧树二级节点数据
    fetchBusinessProcessChildList(id, resolve) {
      API.getBusinessProcessChild({ workId: id })
        .then(({ success, data: children }) => {
          if (success && children) {
            const data = children.map(item => {
              return {
                ...item,
                leaf: true,
                icon: 'group'
              }
            })
            resolve(data)
          }
        })
        .catch(() => {
          return resolve([])
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.data-service {
  @include flex(center, flex-start);
  background: #fff;
  height: calc(100vh - 124px);

  &-aside {
    @include flex(flex-start, flex-start, column);
    width: 240px;
    height: 100%;
    // overflow: hidden;

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
        font-size: 16px;
        color: #1890ff;
        margin-left: 12px;
        cursor: pointer;
      }

      .is-refresh {
        animation: refreshIcon 1.5s linear infinite;

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

      // &:hover {
      //   overflow-y: auto;
      // }

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
    flex: 1;
    width: 100%;
    height: 100%;
    border-left: 1px solid #d9d9d9;
    overflow: hidden;
  }
}

// .data-service-entry-active,
// .data-service-leave-active {
//   transition: all 0.35s ease-in-out;
// }

// .data-service-enter,
// .data-service-leave-to {
//   transform: translateY(50%);
//   opacity: 0;
// }
</style>
