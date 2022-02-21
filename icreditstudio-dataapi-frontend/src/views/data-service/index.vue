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
          <i class="header-icon el-icon-circle-plus-outline"></i>
          <i class="header-icon el-icon-refresh"></i>
        </div>
      </header>

      <div class="search-select">
        <el-select
          class="text-select"
          filterable
          clearable
          remote
          placeholder="请输入关键字"
          size="mini"
          :loading="searchLoading"
          @change="handleSelectChange"
          v-model="selectValue"
        >
          <el-option
            v-for="item in selectOptions"
            :key="item.tableName"
            :label="item.tableName"
            :value="item.id"
          >
          </el-option>
        </el-select>
        <i class="search el-icon-search"></i>
      </div>

      <el-tree
        class="tree"
        ref="tree"
        node-key="id"
        :data="treeData"
        :expand-on-click-node="false"
        :default-expanded-keys="defalutExpandKey"
        @node-click="handleNodeClick"
      >
        <div
          slot-scope="{ node, data }"
          :id="node.id"
          :draggable="node.level > 1"
          :class="[
            'custom-tree-node',
            node.parent.disabled || data.disabled ? 'is-disabled' : ''
          ]"
        >
          <div class="left">
            <span v-if="data.type === '3'" class="circle"></span>
            <JSvg class="jsvg-icon" :name="data.icon"></JSvg>
            <span>{{ data.label }}</span>
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
  </div>
</template>

<script>
import DatasourceGenerate from './source'
import tableConfiguration from '@/configuration/table/data-service-api'
import formOption from '@/configuration/form/data-service-api'
import crud from '@/mixins/crud'

export default {
  mixins: [crud],

  components: { DatasourceGenerate },

  data() {
    return {
      opType: '',
      selectValue: '',
      searchLoading: false,
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
      treeData: [
        {
          label: 'datax_web',
          icon: 'database',
          type: 0,
          id: 1,
          disabled: false,
          category: 'database',
          children: [
            {
              label: 'h_app_sysytem',
              icon: 'table',
              type: 1,
              id: 2,
              disabled: false,
              category: 'table'
            },
            {
              label: 'h_data_metadata_code',
              icon: 'table',
              type: 2,
              id: 3,
              disabled: true,
              category: 'table'
            }
          ]
        },
        {
          label: 'datax_web2',
          icon: 'database',
          type: 0,
          id: 6,
          disabled: true,
          category: 'database',
          children: [
            {
              label: 'h_app_sysytem',
              icon: 'table',
              type: 1,
              id: 7,
              category: 'table'
            },
            {
              label: 'h_data_metadata_code',
              icon: 'table',
              type: 2,
              id: 8,
              category: 'table'
            }
          ]
        }
      ]
    }
  },

  methods: {
    // 点击选中当前节点
    handleNodeClick(curData, curNode) {
      console.log('curData, curNode=', curData, curNode)
      const { label, id, ...rest } = curData
      const idx = this.tabsConfig.findIndex(item => item.id === id)
      this.curTabName = id
      this.currentTab = curData
      // tab选项已经存在当前节点
      if (idx > -1) {
        console.log(id)
      } else {
        this.tabsConfig.push({ name: label, id, ...rest })
      }
    },

    // 新增API
    handleAddDataServiceApi() {
      this.opType = 'add'
    },

    handleSelectChange() {},

    jumpCallback() {
      this.opType = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.data-service {
  @include flex;
  width: 100vw;
  height: 100vh;

  &-aside {
    width: 240px;
    height: 100%;
    overflow: hidden;

    &:hover {
      overflow-y: auto;
    }
  }

  &-aside__header {
    @include flex(space-between);
    padding: 10px;

    .title {
      height: 25px;
      font-size: 18px;
      font-family: PingFangSC, PingFangSC-Medium;
      font-weight: 500;
      color: #262626;
      line-height: 25px;
    }

    .header-icon {
      color: #1890ff;
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

    .search {
      color: #1890ff;
      font-size: 15px;
      cursor: pointer;
    }

    .text-select {
      width: 180px;
    }

    ::v-deep {
      .el-input--mini .el-input__inner {
        border: none;
        padding: 0 5px;
      }
    }
  }

  .tree {
    width: 100%;

    .custom-tree-node {
      @include flex(row, space-between);
      flex: 1;
      cursor: pointer;
      padding-right: 8px;

      .left {
        @include flex;

        .jsvg-icon {
          width: 14px;
          height: 14px;
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
        color: #1890ff;
        .right {
          display: block;
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
