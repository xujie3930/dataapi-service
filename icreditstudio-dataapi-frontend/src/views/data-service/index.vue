<!--
 * @Author: lizheng
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
          filterable
          clearable
          remote
          placeholder="请输入关键词搜索"
          size="mini"
          :loading="searchLoading"
          @change="handleSelectChange"
          v-model="selectValue"
        >
          <i
            slot="suffix"
            class="search el-icon-search"
            @click="handleSelectChange"
          ></i>
        </el-input>
      </div>

      <el-tree
        class="tree"
        ref="tree"
        node-key="id"
        lazy
        v-loading="isTreeLoading"
        :prop="{ isLeaf: 'leaf' }"
        :data="treeData"
        :load="fetchTreeData"
        :expand-on-click-node="true"
        :default-expanded-keys="defalutExpandKey"
        @node-click="handleNodeClick"
      >
        <div
          slot-scope="{ node, data }"
          :id="node.id"
          :draggable="node.level === 2"
          :class="['custom-tree-node']"
        >
          <div class="left">
            <JSvg class="jsvg-icon" :svg-name="data.icon"></JSvg>
            <span>{{ data.name }} </span>
          </div>
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
      >
        <template #content>
          <JTable
            v-loading="mixinTableLoading"
            :table-configuration="tableConfiguration"
            :table-pagination="mixinTablePagination"
            :table-data="mixinTableData"
            @handleSizeChange="mixinHandleSizeChange"
            @handleCurrentChange="mixinHandleCurrentChange"
          >
            <!-- 是否启用 -->
            <template #status="{ row }">
              <span :style="{ color: row.status ? '#ff4d4f' : '#52c41a' }">
                {{ row.status ? '否' : '是' }}
              </span>
            </template>
          </JTable>
        </template>
      </Crud>

      <DatasourceGenerate v-else @on-jump="jumpCallback" />
    </transition>

    <AddBusinessPorcess
      ref="addProcessDialog"
      @on-close="closeProcessCallBack"
    />
  </div>
</template>

<script>
import API from '@/api/api'
import DatasourceGenerate from './source'
import AddBusinessPorcess from './add-business-porcess.vue'
import tableConfiguration from '@/configuration/table/data-service-api'
import formOption from '@/configuration/form/data-service-api'
import crud from '@/mixins/crud'
import { cloneDeep } from 'lodash'

export default {
  mixins: [crud],

  components: { DatasourceGenerate, AddBusinessPorcess },

  data() {
    return {
      opType: '',
      selectValue: '',
      searchLoading: false,
      addPopovervisible: false,
      isRefreshTreeData: false,
      isTreeLoading: false,
      defalutExpandKey: [],
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
    // 点击选中当前节点
    handleNodeClick(curData, curNode) {
      console.log('curData, curNode=', curData, curNode)
    },

    handleCommandClick(clickType) {
      console.log(clickType, 'type')
      this.$refs.addProcessDialog.open()
    },

    // 点击-筛选分组
    handleSelectChange() {
      const name = this.selectValue
      const filterTreeData = []
      console.log(name, 'lllll')

      // API节点筛选符合用户输入值的节点
      const filterChildrenData = child => {
        console.log(child, 'redred')
        return child?.filter(item => item.name?.includes(name))
      }

      cloneDeep(this.oldTreeData).forEach(node => {
        console.log(node.name?.includes(name), 'lllpp')
        node.name?.includes(name)
          ? filterTreeData.push(node)
          : filterChildrenData(node.children)
      })

      console.log(filterTreeData, 'sss')

      this.treeData = filterTreeData
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

    closeProcessCallBack(opType) {
      opType === 'save' && this.fetchBusinessProcessList()
    },

    // 获取-左侧树懒加载
    fetchTreeData(node, resolve) {
      const { level } = node
      switch (level) {
        case 0:
          this.fetchBusinessProcessList(resolve)
          break

        case 1:
          this.fetchBusinessProcessChildList(resolve)
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
        .finally(() => {
          this.isTreeLoading = false
        })
    },

    // 获取-左侧树二级节点数据
    fetchBusinessProcessChildList(resolve) {
      const workId = this.$refs?.tree.getCurrentKey()
      API.getBusinessProcessChild({ workId })
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
        .finally(() => {})
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

    &:hover {
      overflow-y: auto;
    }

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

      .el-tree-node {
        margin-bottom: 10px;
      }

      .custom-tree-node {
        @include flex(row, space-between);
        flex: 1;
        cursor: pointer;
        padding-right: 8px;
        font-size: 14px;
        color: #262626;

        .left {
          @include flex;

          .jsvg-icon {
            width: 16px;
            height: 16px;
            margin: 0 5px;
          }

          .circle {
            width: 6px;
            height: 6px;
            background: #52c41a;
            border-radius: 50%;
            margin-right: 5px;
          }
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
    @include flex;
    position: relative;
    flex: 1;
    width: 100%;
    height: 100%;
    border-left: 1px solid #d9d9d9;
    overflow: hidden;
    padding-top: 36px;
  }
}

.data-service-entry-active,
.data-service-leave-active {
  transition: all 0.35s ease-in-out;
}

.data-service-enter,
.data-service-leave-to {
  transform: translateY(50%);
  opacity: 0;
}
</style>
