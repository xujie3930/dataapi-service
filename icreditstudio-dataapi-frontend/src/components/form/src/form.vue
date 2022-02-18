<template>
  <el-form
    :ref="formConfig.refName"
    :validate-on-rule-change="false"
    :model="formConfig.models || {}"
    :rules="formConfig.rule"
    :class="fromClass.form"
    :size="fromClass.formSize"
    :inline="formConfig.inline"
    :data-id="formConfig.refName"
  >
    <el-form-item
      v-for="item in formItems.filter(e => !e.isHide)"
      :key="item.ruleProp"
      :prop="item.ruleProp"
      :class="fromClass.formItem"
    >
      <div class="iframe-label">
        <label
          :class="[
            'el-form-item__label',
            fromClass.label,
            { 'iframe-label__required': _isRequired(item.ruleProp) }
          ]"
          :title="item.label"
          >{{ item.label }}</label
        >
      </div>
      <!-- 文本 -->
      <div v-if="item.type === 'text'" :data-id="item.ruleProp + '-form-item'">
        <el-input
          v-model="item.model"
          :name="item.name"
          :title="item.model"
          :placeholder="item.placeholder || `请输入${item.label}`"
          :class="fromClass.itemClass"
          :clearable="item.clearable"
          :disabled="item.disabled"
          :readonly="item.readonly"
          :maxlength="item.maxlength || textMaxLength"
          :show-word-limit="item.showWordLimit"
          :suffix-icon="item.endIcon"
          :prefix-icon="item.startIcon"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
          @input="e => handleFunc(e, item.input)"
          @clear="e => handleFunc(e, item.clear)"
          @click.native="e => handleFunc(e, item.click)"
        >
          <!-- 输入框头部内容 -->
          <template #prefix>
            <slot :name="`${item.slotName || item.ruleProp}Prefix`" />
          </template>
          <!-- 输入框尾部内容 -->
          <template #suffix>
            <slot :name="`${item.slotName || item.ruleProp}Suffix`" />
          </template>
          <!-- 输入框前置内容 -->
          <template #prepend>
            <slot :name="`${item.slotName || item.ruleProp}Prepend`" />
          </template>
          <!-- 输入框后置内容 -->
          <template #append>
            <slot :name="`${item.slotName || item.ruleProp}Append`" />
          </template>
        </el-input>
      </div>
      <!-- 带输入建议的文本框 -->
      <el-autocomplete
        v-else-if="item.type === 'autocomplete'"
        v-model="item.model"
        :data-id="item.ruleProp + '-form-item'"
        :placeholder="item.placeholder || `请输入${item.label}`"
        :name="item.name"
        :disabled="item.disabled"
        :debounce="item.debounce || 300"
        :value-key="item.valueKey || 'value'"
        :trigger-on-focus="item.triggerOnFocus"
        :suffix-icon="item.endIcon"
        :prefix-icon="item.startIcon"
        :fetch-suggestions="
          (queryString, cb) => {
            querySearch(
              queryString,
              cb,
              item.isFetch ? item.fetch : item.allData,
              item.isFetch ? item.fetchKey : item.filterKey
            )
          }
        "
        @select="
          selectedObj => {
            handleAutoComplete(selectedObj, item.select)
          }
        "
      >
        <template #default="scope">
          <span>{{ scope.item[item.labelKey] }}</span>
        </template>
      </el-autocomplete>
      <!--密码输入-->
      <el-input
        v-else-if="item.type === 'password'"
        v-model="item.model"
        type="password"
        :name="item.name"
        :placeholder="item.placeholder || `请输入${item.label}`"
        :class="fromClass.itemClass"
        :disabled="item.disabled"
        :maxlength="item.maxlength"
        :suffix-icon="item.endIcon"
        :prefix-icon="item.startIcon"
        :data-id="item.ruleProp + '-form-item'"
        @blur="e => handleFunc(e, item.blur)"
        @focus="e => handleFunc(e, item.focus)"
        @change="e => handleFunc(e, item.change)"
        @input="e => handleFunc(e, item.input)"
        @clear="e => handleFunc(e, item.clear)"
      />
      <!--加提示框的密码输入-->
      <el-tooltip
        v-else-if="item.type === 'passwordTip'"
        class="item"
        effect="dark"
        placement="top"
        :content="item.tipContent"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-input
          v-model="item.model"
          type="password"
          :name="item.name"
          :placeholder="item.placeholder || `请输入${item.label}`"
          :class="fromClass.itemClass"
          :disabled="item.disabled"
          :maxlength="item.maxlength"
          :suffix-icon="item.endIcon"
          :prefix-icon="item.startIcon"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
          @input="e => handleFunc(e, item.input)"
          @clear="e => handleFunc(e, item.clear)"
        />
      </el-tooltip>
      <!--文本域textarea，带插槽-->
      <div
        v-else-if="item.type === 'textarea'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-input
          v-model="item.model"
          type="textarea"
          :class="fromClass.itemClass"
          :name="item.name"
          :rows="item.rows || 2"
          :maxlength="item.maxlength || textareaMaxLength"
          :show-word-limit="item.showWordLimit"
          :placeholder="item.placeholder || `请输入${item.label}`"
          :autosize="item.autosize || false"
          :resize="item.resize || 'none'"
          :disabled="item.disabled"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
          @input="e => handleFunc(e, item.input)"
          @clear="e => handleFunc(e, item.clear)"
        />
      </div>
      <!-- 颜色选择框 -->
      <el-color-picker
        v-else-if="item.type === 'color'"
        v-model="item.model"
        class="iframe-form-item__color"
        :data-id="item.ruleProp + '-form-item'"
        :predefine="colorPredefine"
        :class="fromClass.itemClass"
        :disabled="item.disabled"
        @change="e => handleFunc(e, item.change)"
        @activeChange="e => handleFunc(e, item.activeChange)"
      />
      <!-- select -->
      <div
        v-else-if="item.type === 'select'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-select
          v-model="item.model"
          :placeholder="item.placeholder || `请选择${item.label}`"
          :name="item.name"
          :class="fromClass.itemClass"
          :multiple="item.multiple"
          :multiple-limit="item.multipleLimit || 0"
          :collapse-tags="item.collapseTags"
          :disabled="item.disabled"
          :clearable="item.clearable === false ? false : true"
          :filterable="item.filterable"
          :allow-create="item.allowCreate"
          @clear="e => handleFunc(e, item.clear)"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
        >
          <el-option
            v-for="option in transToOptionsByEnums(item)"
            :key="option[(item.props && item.props.value) || 'value']"
            :label="option[(item.props && item.props.label) || 'label']"
            :value="option[(item.props && item.props.value) || 'value']"
            :disabled="option.disabled"
          />
        </el-select>
      </div>
      <!--日期选择-->
      <div
        v-else-if="datePickerTypes.includes(item.type)"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-date-picker
          v-model="item.model"
          :default-value="item.defaultValue || new Date()"
          :name="item.name"
          :type="item.type"
          :placeholder="item.placeholder || `请选择${item.label}`"
          :class="fromClass.itemClass"
          :format="item.format || 'yyyy-MM-dd HH:mm:ss'"
          :readonly="item.readonly"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :editable="item.editable"
          :value-format="item.valueFormat"
          :picker-options="item.pickerOptions"
          :range-separator="item.rangeSeparator"
          :start-placeholder="item.startPlaceholder"
          :end-placeholder="item.endPlaceholder"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
        />
        <slot v-if="item.isSolt" :name="item.soltName" />
      </div>
      <!--时间选择-->
      <div
        v-else-if="item.type === 'time'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-time-picker
          v-model="item.model"
          :class="fromClass.itemClass"
          :name="item.name"
          :default-value="item.defaultValue || new Date()"
          :placeholder="item.placeholder || `请选择${item.label}`"
          :is-range="item.isRange"
          :format="item.format || 'HH:mm:ss'"
          :arrow-control="item.arrowControl"
          :readonly="item.readonly"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :value-format="item.valueFormat"
          :picker-options="item.pickerOptions"
          :range-separator="item.rangeSeparator"
          :start-placeholder="item.startPlaceholder"
          :end-placeholder="item.endPlaceholder"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
        />
      </div>
      <!-- 单选框 -->
      <div
        v-else-if="item.type === 'radio'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-radio-group
          v-model="item.model"
          :class="fromClass.itemClass"
          :disabled="item.disabled"
          @change="e => handleFunc(e, item.change)"
        >
          <el-radio
            v-for="option in item.options"
            :key="option[(item.props && item.props.value) || 'value']"
            :label="option[(item.props && item.props.value) || 'value']"
            :name="option[(item.props && item.props.name) || 'name']"
            :disabled="option.disabled"
            >{{ option[(item.props && item.props.label) || 'label'] }}
          </el-radio>
        </el-radio-group>
      </div>
      <!-- 多选框 -->
      <div
        v-else-if="item.type === 'checkbox'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-checkbox-group
          v-model="item.model"
          :class="['iframe-checkbox', fromClass.itemClass]"
          :min="item.min"
          :max="item.max"
          :disabled="item.disabled"
          @change="e => handleFunc(e, item.change)"
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
      </div>
      <!-- select tree -->
      <div
        v-else-if="item.type === 'selectTree'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <j-select-tree
          ref="selectTree"
          v-model="item.model"
          :props="item.treeProps"
          :options="item.treeData"
          :multiple="item.multiple"
          :clearable="item.clearable"
          :disabled="item.disabled"
          :accordion="item.accordion"
          :placeholder="item.placeholder || `请选择${item.label}`"
          :input-placeholder="item.inputPlaceholder"
          :default-expand-all="item.defaultExpandAll"
          @nodeClick="e => handleFunc(e, item.nodeClick)"
        />
      </div>
      <!-- 数字加减 -->
      <div
        v-else-if="item.type === 'inputNum'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-input-number
          v-model="item.model"
          class="inputNumer"
          :class="['iframe-input-number', fromClass.itemClass]"
          :precision="item.precision"
          :name="item.name"
          :min="item.min"
          :max="item.max"
          :placeholder="item.placeholder || `请选择${item.label}`"
          :readonly="item.readonly"
          :disabled="item.disabled"
        />
      </div>
      <!-- icon -->
      <div
        v-else-if="item.type === 'icon'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-select
          v-model="item.model"
          :placeholder="item.placeholder || `请选择${item.label}`"
          :name="item.name"
          :class="fromClass.itemClass"
          :multiple="item.multiple"
          :multiple-limit="item.multipleLimit || 0"
          :collapse-tags="item.collapseTags"
          :disabled="item.disabled"
          :clearable="item.clearable === false ? false : true"
          :filterable="item.filterable"
          :allow-create="item.allowCreate"
          @clear="e => handleFunc(e, item.clear)"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @change="e => handleFunc(e, item.change)"
        >
          <el-row class="j-icon-wrap">
            <el-col v-for="icon in item.options" :key="icon" :span="3">
              <el-option class="j-icon-item" :value="icon">
                <i :class="[item.baseClassName, icon]" />
              </el-option>
            </el-col>
          </el-row>
        </el-select>
      </div>
      <!-- upload -->
      <div
        v-else-if="item.type === 'upload'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-upload
          :class="item.class"
          :action="item.action"
          :headers="item.headers"
          :multiple="item.multiple"
          :data="item.data"
          :name="item.name || 'file'"
          :with-credentials="item.withCredentials || false"
          :show-file-list="
            item.showFileList === undefined ? true : item.showFileList
          "
          :drag="item.drag || false"
          :accept="item.accept"
          :on-preview="e => handleFunc(e, item.onPreview)"
          :on-remove="
            (file, fileList) => handleFunc({ file, fileList }, item.onRemove)
          "
          :on-success="
            (response, file, fileList) =>
              handleFunc({ response, file, fileList }, item.onSuccess)
          "
          :on-error="
            (err, file, fileList) =>
              handleFunc({ err, file, fileList }, item.onError)
          "
          :on-progress="
            (event, file, fileList) =>
              handleFunc({ event, file, fileList }, item.onProgress)
          "
          :on-change="
            (file, fileList) => handleFunc({ file, fileList }, item.onChange)
          "
          :on-exceed="
            (files, fileList) => handleFunc({ files, fileList }, item.onExceed)
          "
          :before-upload="file => handleFunc(file, item.beforeUpload)"
          :before-remove="
            (file, fileList) =>
              handleFunc({ file, fileList }, item.beforeRemove)
          "
          :list-type="item.listType || 'text'"
          :auto-upload="item.autoUpload || true"
          :file-list="item.fileList || []"
          :http-request="_ => handleFunc({}, item.httpRequest)"
          :disabled="item.disabled || false"
          :limit="item.limit"
        >
          <template #default>
            <slot :name="`${item.ruleProp}UploadDefault`"></slot>
          </template>
          <template #trigger>
            <slot :name="`${item.ruleProp}UploadTrigger`"></slot>
          </template>
          <template #tip>
            <slot :name="`${item.ruleProp}UploadTip`"></slot>
          </template>
          <template #file>
            <slot :name="`${item.ruleProp}UploadFile`"></slot>
          </template>
        </el-upload>
      </div>
      <!-- cascader -->
      <div
        v-else-if="item.type === 'cascader'"
        :data-id="item.ruleProp + '-form-item'"
      >
        <el-cascader
          v-model="item.model"
          :class="fromClass.itemClass"
          :options="item.options"
          :props="item.props"
          :size="item.size"
          :disabled="item.disabled"
          :clearable="item.clearable === false ? false : true"
          :show-all-levels="
            item.showAllLevels === undefined ? true : item.showAllLevels
          "
          :collapse-tags="item.collapseTags || false"
          :separator="item.separator || '/'"
          :filterable="item.filterable"
          :filter-method="
            (node, keyword) => handleFunc({ node, keyword }, item.filterMethod)
          "
          :before-filter="e => handleFunc(e, item.beforeFilter)"
          :debounce="item.debounce || 300"
          :popper-class="item.popperClass"
          :placeholder="item.placeholder || `请选择${item.label}`"
          @change="e => handleFunc(e, item.change)"
          @expand-change="e => handleFunc(e, item.expandChange)"
          @blur="e => handleFunc(e, item.blur)"
          @focus="e => handleFunc(e, item.focus)"
          @visible-change="e => handleFunc(e, item.visibleChange)"
          @remove-tag="e => handleFunc(e, item.removeTag)"
        >
          <template #empty>
            <slot :name="`${item.ruleProp}CascaderEmpty`"></slot>
          </template>
        </el-cascader>
      </div>
      <slot
        v-else-if="item.type === 'slot'"
        :data-id="item.ruleProp + '-form-item'"
        :name="`${item.ruleProp}FormItem`"
        :config="item"
      ></slot>
    </el-form-item>
    <el-form-item :class="fromClass.buttonClass">
      <el-button
        v-for="btn in formFunc.filter(e => !e.hide)"
        :key="btn.btnEmitName"
        :type="btn.type"
        :size="btn.size"
        :loading="btn.loading"
        :disabled="btn.disabled"
        @click="$emit(btn.btnEmitName)"
        >{{ btn.btnText }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'JForm',
  props: {
    formItems: {
      required: false
    },
    formFunc: {
      type: Array,
      default() {
        return [
          {
            btnText: '搜索',
            btnEmitName: 'primary',
            type: 'primary'
          },
          {
            btnText: '清空',
            btnEmitName: 'normal'
          }
        ]
      }
    },
    formConfig: {
      required: false
    },
    fromClass: {
      type: Object,
      default() {
        return {
          form: 'iframe-form',
          formItem: 'iframe-form-item',
          label: 'iframe-form-label',
          itemClass: 'iframe-form-input',
          buttonClass: 'iframe-form-btn'
        }
      }
    }
  },
  data() {
    return {
      textMaxLength: 50, // 查询框最多输入字符数量
      textareaMaxLength: 500, // 文本域最多输入字符数量
      datePickerTypes: [
        'datetime',
        'date',
        'week',
        'month',
        'year',
        'daterange',
        'datetimerange'
      ], // el-data-picker 的条件
      flag: true,
      colorPredefine: [
        // 颜色框的预定义
        '#ff4500',
        '#ff8c00',
        '#ffd700',
        '#90ee90',
        '#00ced1',
        '#1e90ff',
        '#c71585',
        '#2D15C7'
      ],
      resetIptNum: 0, // 需要重置的加过滤特殊字符指令的输入框数量
      filterRule: 'all', // 过滤特殊字符规则（过滤所有）
      checkedNodes: [],
      isShow: false,
      timeDefaultShow: '',
      defaultPickerOptions: {
        // 默认当天之后的日期不可选
        // disabledDate: (time) => {
        //   return time.getTime() > Date.now()
        // }
      }
    }
  },
  watch: {
    formItems: {
      handler(oldValue, newValue) {
        for (let k = 0; k < newValue.length; k++) {
          if (newValue[k].type !== 'treeInput') {
            this.$props.formConfig.models[newValue[k].ruleProp] =
              newValue[k].model
          }
        }
      },
      deep: true
    }
  },
  methods: {
    /**
     * 是否必填项
     * @param prop {String}
     */
    _isRequired(prop) {
      const _rules = this.formConfig.rule
      if (!_rules || !_rules[prop]) return false
      return !!_rules[prop].filter(e => e.required).length
    },
    /**
     * 校验方法是否存在
     * @param e
     * @param func
     */
    handleFunc(e, func) {
      if (typeof func === 'function') {
        return func(e)
      }
    },
    /**
     * 返回输入建议的方法
     * @param {string} queryString 用户输入值
     * @param {function} cb 回调
     * @param {array | function} dataOrInterface 所有数据 或者 请求接口
     * @param {string} filterKeyOrQueryKey 过滤的键 或者 请求的key
     */
    querySearch(queryString, cb, dataOrInterface, filterKeyOrQueryKey) {
      // 判断是接口还是数据
      if (typeof dataOrInterface === 'function') {
        dataOrInterface({
          [filterKeyOrQueryKey]: queryString
        })
          .then(res => {
            // 调用 callback 返回建议列表的数据
            cb(res.data)
          })
          .catch(() => {
            cb([])
          })
      } else {
        const restaurants = dataOrInterface
        // 调用 callback 返回建议列表的数据
        const restaurantsFilter = restaurants.filter(
          this.createFilter(queryString, filterKeyOrQueryKey)
        )
        cb(queryString ? restaurantsFilter : restaurants)
      }
    },
    /**
     * 创建filter
     * @param queryString 对比值
     * @param filterKey 对象要取的键
     * @returns {function(*): boolean}
     */
    createFilter(queryString, filterKey) {
      return restaurant => {
        return (
          restaurant[filterKey]
            .toLowerCase()
            .indexOf(queryString.toLowerCase()) === 0
        )
      }
    },
    /**
     * 点击选中建议项时触发
     * @param item 选中项
     * @param func 回调
     */
    handleAutoComplete(item, func) {
      func && func(item)
    },
    /**
     * 搜索| 确认
     * @param func
     */
    query(func) {
      /* 是否配置校验规则 */
      if (Object.keys(this.$props.formConfig.rule).length) {
        this.$refs[this.$props.formConfig.refName].validate(valid => {
          if (valid) {
            func()
          }
        })
      } else {
        func()
      }
    },
    /**
     * 重置
     * @param fn
     */
    reset(fn) {
      for (let i = 0; i < this.$props.formItems.length; i++) {
        const _option = this.$props.formItems[i]
        if (_option.type === 'select' && _option.multiple) {
          _option.model = []
        } else if (_option.type === 'checkbox') {
          _option.model = []
          _option.options.forEach(e => {
            // e.checked = false
            Object.assign(e, { checked: false })
          })
        } else if (_option.type === 'selectTree') {
          this.$refs.selectTree[0].handleClear()
          _option.model = ''
        } else {
          _option.model = ''
        }
      }
      this.$refs[this.$props.formConfig.refName].resetFields()
      if (fn) {
        fn()
      }
    },
    none() {
      return false
    },
    transToOptionsByEnums({ options, enums }) {
      return !enums
        ? options
        : Object.entries(enums).map(([value, label]) => ({ label, value }))
    }
  }
}
</script>

<style lang="scss">
@import '~@/assets/scss/form.scss';
</style>
