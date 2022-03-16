<template>
  <div class="ds-table" style="margin: 0" :data-id="tableConfiguration.refName">
    <el-table
      v-loading="tableLoading"
      :id="tableConfiguration.id || tableConfiguration.refName"
      :ref="tableConfiguration.refName"
      :key="tableConfiguration.tableKey || ''"
      :row-key="getAllRowKeys"
      :data="tableData"
      :cell-class-name="tableConfiguration.cellClassName || 'table-column-cell'"
      :header-cell-class-name="
        tableConfiguration.headerCellClassName || 'table-header-cell'
      "
      :max-height="tableConfiguration.maxHeight"
      :border="tableConfiguration.isBorder"
      :stripe="tableConfiguration.isStripe"
      :lazy="tableConfiguration.lazy"
      :load="
        (row, treeNode, resolve) =>
          handleFunc({ row, treeNode, resolve }, tableConfiguration.load)
      "
      :default-expand-all="tableConfiguration.defaultExpandAll"
      :expand-row-keys="tableConfiguration.expandRowKeys"
      :tree-props="tableConfiguration.treeProps"
      :size="tableConfiguration.size"
      :indent="tableConfiguration.indent"
      style="width: 100%"
      @select="handleSelect"
      @select-all="handleSelectAll"
      @selection-change="handleSelectChange"
      @row-dblclick="handleRowDblclick"
      @current-change="handleCurrentChange"
      @sort-change="handleSortChange"
      @expand-change="handleExpandChange"
    >
      <!-- 自定义表格无数据 -->
      <template slot="empty">
        <slot v-if="tableConfiguration.emptySlot" name="empty"></slot>

        <div v-else class="ds-table-empty">
          <img class="img" src="@/assets/images/table-empty.png" />
          <span class="text">暂无数据</span>
        </div>
      </template>

      <!-- 自定义表格底部区域 -->
      <template
        v-if="tableConfiguration.append || tableConfiguration.appendSlot"
        slot="append"
      >
        <slot v-if="tableConfiguration.appendSlot" name="append"></slot>
        <div v-else class="icredit-table-append">
          <span class="text" v-if="tableConfiguration.isMore">加载中</span>
          <span class="text" v-else>没有更多了</span>
        </div>
      </template>

      <!-- 根据入参类型渲染TableColumn -->
      <template v-for="(item, index) in tableConfiguration.group">
        <!-- 下标 -->
        <el-table-column
          v-if="item.type === 'index'"
          :key="index"
          type="index"
          :label="item.label"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :index="indexMethod"
        />
        <!-- img -->
        <el-table-column
          v-else-if="item.type === 'img'"
          :key="`${item.label}-${index}`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="false"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <img
              :src="scope.row[item.prop] | base64UrlFilter(item.imgBase64)"
              :alt="item.alt"
              :style="{ height: item.height, cursor: item.cursor }"
              style="display: inline-block; max-width: 100%"
              @click="item.func ? item.func(scope) : none()"
            />
          </template>
        </el-table-column>
        <!-- link -->
        <el-table-column
          v-if="item.type === 'link'"
          :key="index"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
        >
          <template #default="scope">
            <a
              :href="scope.row[`${item.prop}Link`]"
              :style="scope.row[`${item.prop}Style`]"
              :title="scope.row[`${item.prop}Title`]"
              >{{ scope.row[item.prop] }}</a
            >
          </template>
        </el-table-column>
        <!-- statusText -->
        <el-table-column
          v-if="item.type === 'statusText'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <span :style="{ color: renderStatusTextColor(scope, item.color) }">
              {{ renderStatusText(scope, item) }}
            </span>
          </template>
        </el-table-column>
        <!-- color -->
        <el-table-column
          v-if="item.type === 'color'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <div
              :style="{
                background: scope.row[item.prop],
                height: '24px',
                width: '40px',
                margin: '0 auto'
              }"
            />
          </template>
        </el-table-column>
        <!-- editInput -->
        <el-table-column
          v-if="item.type === 'editInput'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-input
              v-model="scope.row[item.prop]"
              :disabled="item.disabled"
              :placeholder="item.placeholder || '请输入'"
              :maxlength="item.maxlength"
              :show-word-limit="item.showWordLimit"
              :type="item.inputType"
              :resize="item.resize"
              :clearable="item.clearable"
              @clear="
                value => handleFunc({ value, scope, config: item }, item.clear)
              "
              @blur="
                value => handleFunc({ value, scope, config: item }, item.blur)
              "
              @focus="
                value => handleFunc({ value, scope, config: item }, item.focus)
              "
              @change="
                value => handleFunc({ value, scope, config: item }, item.change)
              "
              @input="
                value => handleFunc({ value, scope, config: item }, item.input)
              "
            />
          </template>
        </el-table-column>
        <!-- 进度条 -->
        <el-table-column
          v-if="item.type === 'progress'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-progress
              :type="item.strokeType"
              :show-text="item.showText"
              :text-inside="item.inside"
              :stroke-width="item.strokeWidth"
              :percentage="scope.row[item.prop]"
            />
          </template>
        </el-table-column>
        <!-- 数字编辑 -->
        <el-table-column
          v-if="item.type === 'editNumber'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-input-number
              v-model="scope.row[item.prop]"
              controls-position="right"
            />
          </template>
        </el-table-column>
        <!-- time edit -->
        <el-table-column
          v-if="item.type === 'editTime'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-popover v-model="item.click" trigger="click" placement="top">
              <el-date-picker
                v-model="editModel"
                type="datetime"
                :format="item.format"
              />
              <el-button
                type="primary"
                @click="
                  () => {
                    scope.row[item.prop] = editModel
                    item.click = false
                  }
                "
              >
                <i class="el-icon-edit" />
              </el-button>
              <el-button @click="item.click = false"
                ><i class="el-icon-close"
              /></el-button>
              <div
                slot="reference"
                class="name-wrapper"
                @click="editModel = scope.row[item.prop]"
              >
                {{ scope.row[item.prop] }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <!-- select -->
        <el-table-column
          v-if="item.type === 'editSelect'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-popover v-model="item.click" trigger="click" placement="top">
              <el-select v-model="editModel">
                <el-option
                  v-for="choose in item.option"
                  :key="choose.value"
                  :label="choose.label"
                  :value="choose.value"
                />
              </el-select>
              <el-button
                type="primary"
                @click="
                  () => {
                    scope.row[item.prop] = editModel
                    item.click = false
                  }
                "
              >
                <i class="el-icon-edit" />
              </el-button>
              <el-button @click="item.click = false"
                ><i class="el-icon-close"
              /></el-button>
              <div
                slot="reference"
                class="name-wrapper"
                @click="editModel = scope.row[item.prop]"
              >
                {{ scope.row[item.prop] }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <!-- text -->
        <el-table-column
          v-else-if="item.type === 'text'"
          :key="`${item.label}-${index}`"
          :label="item.label"
          :formatter="item.formatter"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
          :class-name="item.className"
        />
        <!-- date -->
        <el-table-column
          v-else-if="item.type === 'date'"
          :key="`${item.label}-${index}-date`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :formatter="item.formatter"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            {{ dateFormat(scope.row[item.prop], item.format) }}
          </template>
        </el-table-column>
        <!-- selection -->
        <el-table-column
          v-else-if="item.type === 'selection'"
          :key="`${item.label}-${index}-selection`"
          type="selection"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :reserve-selection="true"
        />
        <!-- tooltip -->
        <el-table-column
          v-else-if="item.type === 'tooltip'"
          :key="`${item.label}-${index}-tooltip`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
        >
          <template #default="scope">
            <el-tooltip
              class="item"
              effect="dark"
              :placement="item.position"
              :content="scope.row[item.prop] && scope.row[item.prop].popContent"
            >
              <span
                v-html="scope.row[item.prop] && scope.row[item.prop].content"
              />
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- hover -->
        <el-table-column
          v-else-if="item.type === 'hover'"
          :key="`${item.label}-${index}-hover`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-popover
              trigger="hover"
              :placement="item.position"
              :class="item.class"
            >
              <p
                v-html="scope.row[item.prop] && scope.row[item.prop].popContent"
              />
              <div
                slot="reference"
                v-html="scope.row[item.prop] && scope.row[item.prop].content"
              />
            </el-popover>
          </template>
        </el-table-column>
        <!-- 多选框组 -->
        <el-table-column
          v-if="item.type === 'checkbox'"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-checkbox-group
              v-model="scope.row[item.prop]"
              :min="item.min"
              :max="item.max"
              :disabled="item.disabled"
              @change="
                value => handleFunc({ value, scope, config: item }, item.change)
              "
            >
              <el-checkbox
                v-for="option in item.options"
                :key="option[(item.props && item.props.value) || 'value']"
                :label="option[(item.props && item.props.value) || 'value']"
                :name="option[(item.props && item.props.name) || 'name']"
                :disabled="option.disabled"
                :checked="option.checked"
                >{{ option[(item.props && item.props.label) || 'label'] }}
              </el-checkbox>
            </el-checkbox-group>
          </template>
        </el-table-column>
        <!-- select -->
        <el-table-column
          v-else-if="item.type === 'select'"
          :key="`${item.label}-${index}-select`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-select
              v-model="scope.row[item.prop]"
              :placeholder="item.placeholder || '请选择'"
              :name="item.name"
              :multiple="item.multiple"
              :multiple-limit="item.multipleLimit || 0"
              :collapse-tags="item.collapseTags"
              :disabled="item.disabled"
              :clearable="item.clearable"
              :filterable="item.filterable"
              :allow-create="item.allowCreate"
              @clear="
                value => handleFunc({ value, scope, config: item }, item.clear)
              "
              @blur="
                value => handleFunc({ value, scope, config: item }, item.blur)
              "
              @focus="
                value => handleFunc({ value, scope, config: item }, item.focus)
              "
              @change="
                value => handleFunc({ value, scope, config: item }, item.change)
              "
            >
              <el-option
                v-for="option in item.options"
                :key="option[(item.props && item.props.value) || 'value']"
                :label="option[(item.props && item.props.value) || 'value']"
                :value="option[(item.props && item.props.value) || 'value']"
                :disabled="option.disabled"
              />
            </el-select>
          </template>
        </el-table-column>
        <!-- switch -->
        <el-table-column
          v-else-if="item.type === 'switch'"
          :key="`${item.label}-${index}-switch`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <el-switch
              v-model="scope.row[item.prop]"
              :width="
                getItemConfig(scope.row, item.prop).switchWidth ||
                item.switchWidth ||
                40
              "
              :disabled="
                getItemConfig(scope.row, item.prop).switchDisabled ||
                item.switchDisabled
              "
              :active-value="
                getItemConfig(scope.row, item.prop).activeValue ||
                item.activeValue
              "
              :inactive-value="
                getItemConfig(scope.row, item.prop).inactiveValue ||
                item.inactiveValue
              "
              :active-color="
                getItemConfig(scope.row, item.prop).activeColor ||
                item.activeColor
              "
              :inactive-color="
                getItemConfig(scope.row, item.prop).inactiveColor ||
                item.inactiveColor
              "
              :active-text="
                getItemConfig(scope.row, item.prop).activeText ||
                item.activeText
              "
              :inactive-text="
                getItemConfig(scope.row, item.prop).inactiveText ||
                item.inactiveText
              "
              @change="
                e => handleFunc({ value: e, scope, config: item }, item.change)
              "
            ></el-switch>
          </template>
        </el-table-column>
        <!-- switch -->
        <el-table-column
          v-else-if="item.type === 'icon'"
          :key="`${item.label}-${index}-icon`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="scope">
            <i
              :class="[item.baseClassName, scope.row[item.prop]]"
              @click="handleFunc({ scope, item }, item.click)"
            />
          </template>
        </el-table-column>
        <!-- operation -->
        <el-table-column
          v-else-if="item.type === 'operation'"
          :key="`${item.label}-${index}-operation`"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :fixed="item.fixed"
        >
          <template #default="scope">
            <template
              v-for="(operate, index) in renderOperation(
                scope.row,
                item.operationList
              )"
            >
              <el-button
                type="text"
                :size="operate.size || 'medium'"
                :key="index"
                :data-id="`operation_${index}`"
                :disabled="renderBtnDisabled(scope, operate.disabled)"
                v-if="renderBtnShow(scope, operate.visible)"
                v-html="operate.label"
                @click="_ => handleFunc(scope, operate.func)"
              />
            </template>
          </template>
        </el-table-column>
        <!-- slot -->
        <el-table-column
          v-else-if="item.type === 'slot'"
          :key="`${item.label}-${index}-slot`"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :fixed="item.fixed"
          :header-align="defaultAlign"
          :align="item.align || defaultAlign"
          :render-header="(h, column) => renderHeader(h, column, item)"
          :resizable="resizable"
          :show-overflow-tooltip="true"
          :sortable="item.sortable || false"
        >
          <template #default="{ row, column, $index }">
            <slot
              :name="`${item.prop}`"
              :row="row"
              :column="column"
              :index="$index"
            ></slot>
          </template>
        </el-table-column>
      </template>
    </el-table>

    <!-- 新增表格底部分页器 -->
    <div
      :class="[
        tablePagination.isLeftShowCount ? 'pagination-warp' : 'pagination'
      ]"
      v-if="tableConfiguration.hasPage"
    >
      <div class="left" v-if="tablePagination.isLeftShowCount">
        共计{{ tablePagination.total }}条数据，展示前10000条
      </div>
      <el-pagination
        class="right"
        background
        :current-page="tablePagination.pageNum"
        :page-sizes="tablePagination.pageSizes || [10, 20, 50, 100]"
        :page-size="tablePagination.pageSize"
        :layout="tablePagination.layout || defaultLayout"
        :total="
          tablePagination.total >= 10000 && tablePagination.isLeftShowCount
            ? 10000
            : tablePagination.total
        "
        :pager-count="tablePagination.pagerCount || 7"
        @size-change="e => $emit('handleSizeChange', e)"
        @current-change="e => $emit('handleCurrentChange', e)"
      >
        <span class="page-total">共{{ tablePagination.pageCount }}页</span>
        <span class="page-size">当前显示</span>
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { dateFormat } from '@/utils'

export default {
  name: 'JTable',
  props: {
    tableData: {
      type: Array,
      default() {
        return []
      }
    },
    tableConfiguration: {
      type: Object,
      default() {
        return {
          group: [],
          isBorder: true,
          isStripe: true,
          hasPage: true
        }
      }
    },
    tablePagination: {
      type: Object,
      default() {
        return {}
      }
    },
    tableLoading: {
      type: Boolean,
      default: false
    }
  },

  filters: {
    base64UrlFilter(url, imgBase64) {
      let result = ''
      if (
        !imgBase64 ||
        url.startsWith('blob:') ||
        /^[\s\S]*\/img\//.test(url)
      ) {
        // imgBase64配置不传或为false||blob格式||本地文件，不处理
        result = url
      } else if (url.startsWith('dataimage/')) {
        // base64全路径储存的格式
        const curType = url.split('dataimage/')[1].split('base64')[0]
        const before = `dataimage/${curType}base64`
        const after = `data:image/${curType};base64,`
        result = url.replace(new RegExp(before, 'g'), after)
      } else {
        // base64截取base64后路径储存的格式,统一转成png格式展示
        result = url.startsWith('data:image/')
          ? url
          : `data:image/png;base64,${url}`
      }
      return result
    }
  },

  data() {
    return {
      defaultAlign: 'center',
      defaultLayout: 'total,prev,pager,next,slot,sizes',
      editModel: '',
      selectedData: [],
      playFlag: false,
      resizable: true // 对应列是否可以通过拖动改变宽度
    }
  },

  methods: {
    // 操作列按钮是否显示
    renderBtnShow(scope, visible) {
      // 默认显示按钮
      if (!visible) return true
      return typeof visible === 'function' ? visible(scope) : Boolean(visible)
    },

    // 操作列按钮是否禁用
    renderBtnDisabled(scope, disabled) {
      if (!disabled) return false
      return typeof disabled === 'function'
        ? disabled(scope)
        : Boolean(disabled)
    },

    // statusText类型的column值渲染
    renderStatusText(scope, item) {
      const { row } = scope
      const { formatter, prop } = item
      if (typeof formatter === 'function') return item.formatter(row)
      return row[prop]
    },

    // statusText文案颜色值渲染
    renderStatusTextColor(scope, color) {
      if (typeof color === 'function') return color(scope)
      return color || 'unset'
    },

    /**
     * 校验方法是否存在
     * @param e
     * @param func
     */
    handleFunc(e, func) {
      typeof func === 'function' && func(e)
    },
    getItemConfig(row, prop) {
      return row[`${prop}Config`] || {}
    },
    /**
     * 通过黑名单过滤操作按钮
     * @param blackList
     * @param operationList
     */
    renderOperation({ blackList = [] }, operationList) {
      return operationList.filter(e => !blackList.includes(e.key))
    },
    dateFormat,
    getAllRowKeys(row) {
      return row[this.tableConfiguration.rowKey || 'id']
    },
    indexMethod(index) {
      //    下标
      return index * 1 + 1
    },
    handleSelect(selection, row) {
      this.$emit('select', { selection, row })
    },
    handleSelectAll(selection) {
      this.$emit('select-all', selection)
    },
    handleSelectChange(selection) {
      this.selectedData = selection
      this.$emit('selection-change', this.selectedData)
    },
    handleRowDblclick(row, column, event) {
      this.$emit('row-dblclick', { row, column, event })
    },
    handleSortChange(e) {
      this.$emit('sort-change', e)
    },
    handleCurrentChange(currentRow, oldCurrentRow) {
      this.$emit('current-change', { currentRow, oldCurrentRow })
    },
    handleExpandChange(row, expanded) {
      this.$emit('expand-change', { row, expanded })
    },
    none() {},
    play(id) {
      const dom = document.getElementById(id)
      if (this.$data.playFlag) {
        this.$data.playFlag = false
        dom.pause()
      } else {
        this.$data.playFlag = true
        dom.play()
      }
    },
    renderHeader(h, { column }, item) {
      if (column.label) {
        return h(
          'el-tooltip',
          {
            class: 'item',
            props: {
              effect: 'dark',
              content: item.headerTooltip || column.label,
              placement: 'top'
            }
          },
          [h('span', column.label)]
        )
      }
    },
    handleDropDownCommand(command, dropList, scope) {
      const cur = dropList.find(x => x.key === command)
      cur && cur.func.call(this, scope)
    },
    renderDropOperation(options, scope) {
      return options.reduce((pre, cur) => {
        const isHide =
          typeof cur.isHide === 'function' ? cur.isHide(scope) : cur?.isHide
        !isHide && pre.push(cur)
        return pre
      }, [])
    }
  }
}
</script>

<style lang="scss" scoped>
@import '~@/assets/scss/pagination';
@import '~@/assets/scss/table';
</style>
