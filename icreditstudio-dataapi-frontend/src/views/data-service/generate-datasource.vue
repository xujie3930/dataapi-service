<!--
 * @Description: 数据源生成
 * @Date: 2022-02-19
-->

<template>
  <div class="source-generate">
    <header class="source-header">
      <div class="source-header-icon">
        <JSvg
          class="icon"
          svg-name="back"
          @click.native="handleJumpBackClick"
        />
        <div class="icon-right" v-loading="isSaveBtnLoading">
          <JSvg
            class="icon"
            svg-name="save"
            @click.native="handleSaveFormClick(0)"
          />
        </div>
      </div>
      <div class="source-header-btn">
        <el-button class="jui-button--default">预览</el-button>
        <el-button
          :disabled="true"
          :loading="isTestBtnLoading"
          class="jui-button--default"
        >
          测试
        </el-button>
        <el-button
          :loading="isPublishBtnLoading"
          class="jui-button--parimary"
          type="primary"
          @click="handleSaveFormClick(1)"
          >提交并发布
        </el-button>
      </div>
    </header>

    <main class="source-main" v-loading="pageLoading">
      <el-form
        class="source-main-form"
        ref="form"
        :model="form"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item>
          <div slot="label" class="source-main-form--title">API基础信息</div>
        </el-form-item>

        <el-row type="flex" justify="space-between" class="form-row-item">
          <el-col :lg="{ span: 11 }">
            <el-form-item label="API类型" prop="type">
              <el-select
                style="width: 100%"
                v-model="form.type"
                placeholder="选择API类型"
              >
                <el-option label="注册API" :value="0"></el-option>
                <el-option label="数据源生成API" :value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :lg="{ span: 11 }">
            <el-form-item label="API名称" prop="name">
              <el-input
                show-word-limit
                maxlength="50"
                style="width: 100%"
                v-model="form.name"
                placeholder="请输入API名称"
              ></el-input>
              <p class="form-row-item--tip">
                API名称必须唯一，支持中文、英文、数字、下划线(_)，2~50个字
              </p>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" class="form-row-item" justify="space-between">
          <el-col :lg="{ span: 11 }">
            <el-form-item label="API模式" prop="apiGenerateSaveRequest.model">
              <el-select
                style="width: 100%"
                v-model="form.apiGenerateSaveRequest.model"
                placeholder="选择API类型"
              >
                <el-option label="单表模式生成API" :value="0"></el-option>
                <el-option label="SQL模式生成API" :value="1"></el-option>
                <!-- <el-option label="生成链上API" :value="2"></el-option> -->
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :lg="{ span: 11 }">
            <el-form-item label="API Path" prop="path">
              <el-input
                :maxlength="16"
                show-word-limit
                style="width: 100%"
                v-model="form.path"
                placeholder="请输入API名称"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" class="form-row-item" justify="space-between">
          <el-col :lg="{ span: 11 }">
            <el-form-item label="请求方式" prop="requestType">
              <el-select
                style="width: 100%"
                v-model="form.requestType"
                placeholder="请选择请求方式"
              >
                <el-option label="GET" value="GET"></el-option>
                <el-option label="POST" value="POST"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :lg="{ span: 11 }">
            <el-form-item label="返回类型" prop="responseType">
              <el-select
                disabled
                style="width: 100%"
                v-model="form.responseType"
                placeholder="请选择返回类型"
              >
                <el-option label="JSON" value="JSON"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" class="form-row-item" justify="space-between">
        </el-row>

        <el-row type="flex" justify="space-between" class="form-row-item">
          <el-col :span="11">
            <el-form-item label="所属分组" prop="apiGroupId">
              <el-cascader
                style="width: 100%"
                :disabled="options.opType === 'add'"
                v-model="form.apiGroupId"
                :props="{ expandTrigger: 'hover' }"
                :options="cascaderOptions"
              ></el-cascader>
            </el-form-item>
          </el-col>
          <el-col :lg="{ span: 11 }">
            <el-form-item label="描述">
              <el-input
                type="textarea"
                placeholder="请输入接口描述"
                v-model="form.desc"
                style="width: 100%"
                show-word-limit
                :maxlength="500"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <div slot="label" class="source-main-form--title">选择表</div>
        </el-form-item>

        <el-row type="flex" class="form-row-item" justify="space-between">
          <el-col :span="11">
            <el-form-item label="数据库类型" prop="databaseTye">
              <el-select
                readonly
                disabled
                style="width: 100%"
                v-model="form.databaseTye"
                placeholder="请选择数据库类型"
              >
                <el-option label="MySQL" :value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" class="form-row-item" justify="space-between">
          <el-col :span="11">
            <el-form-item
              label="数据源名称"
              prop="apiGenerateSaveRequest.datasourceId"
            >
              <el-select
                filterable
                style="width: 100%"
                v-model="form.apiGenerateSaveRequest.datasourceId"
                placeholder="请选择数据源名称"
                @change="handleDatasourceNameChange"
              >
                <el-option
                  :label="item.databaseName"
                  :value="item.id"
                  :key="item.id"
                  v-for="item in datasourceOptions"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="11">
            <el-form-item
              label="数据表名称"
              prop="apiGenerateSaveRequest.tableName"
            >
              <el-select
                filterable
                style="width: 100%"
                :disabled="!form.apiGenerateSaveRequest.datasourceId"
                v-model="form.apiGenerateSaveRequest.tableName"
                :placeholder="
                  form.apiGenerateSaveRequest.datasourceId
                    ? '请选择数据表名称'
                    : '请先选择数据源'
                "
                @change="fetchTableFields"
              >
                <el-option
                  :label="item | filterTableName"
                  :value="item.tableName"
                  :key="item.tableName"
                  v-for="item in dataNameOptions"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <div slot="label" class="source-main-form--title">选择参数</div>
        </el-form-item>

        <el-row type="flex" class="form-row-item" justify="end">
          <el-col :span="11">
            <el-form-item>
              <el-input
                clearable
                placeholder="请输入字段名称"
                v-model.trim="selectTableName"
                @input="handleTableNameInput"
                @clear="form.apiParamSaveRequestList = oldTableData"
              >
                <i slot="suffix" class="el-input__icon el-icon-search"></i>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="form-row-item form-row-table">
          <JTable
            ref="selectParamTable"
            :table-loading="tableLoading"
            :table-data="form.apiParamSaveRequestList"
            :table-configuration="tableConfiguration"
          >
            <!-- 返回参数 -->
            <template #isResponse="{ row }">
              <el-checkbox
                :true-label="0"
                :false-label="1"
                v-model="row.isResponse"
              ></el-checkbox>
            </template>

            <!-- 请求参数 -->
            <template #isRequest="{ row }">
              <el-checkbox
                :true-label="0"
                :false-label="1"
                v-model="row.isRequest"
                @change="handleRequestFieldChange($event, row)"
              ></el-checkbox
            ></template>

            <!-- 是否必填 -->
            <template #required="{ row }">
              <el-checkbox
                :true-label="0"
                :false-label="1"
                v-model="row.required"
              ></el-checkbox
            ></template>
          </JTable>
        </el-row>
      </el-form>
    </main>
  </div>
</template>

<script>
import API from '@/api/api'
import { dataServiceParamTableConfig } from '@/configuration/table'

export default {
  data() {
    return {
      timerId: null,
      tableLoading: false,
      pageLoading: false,
      isSaveBtnLoading: false,
      isTestBtnLoading: false,
      isPublishBtnLoading: false,
      oldTableData: [],
      tableConfiguration: dataServiceParamTableConfig,

      selectTableName: '',
      dataNameOptions: [],
      datasourceOptions: [],
      options: {},
      cascaderOptions: [],
      form: {
        id: '',
        type: 0,
        path: '',
        name: '',
        databaseTye: 1,
        apiGroupId: null,
        requestType: 'GET',
        responseType: 'JSON',
        desc: '',
        apiGenerateSaveRequest: {
          id: '',
          model: 0,
          datasourceId: '',
          tableName: ''
        },
        apiParamSaveRequestList: []
      },
      formRules: {
        type: [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        name: [
          { required: true, message: 'API名称不能为空', trigger: 'blur' },
          { min: 2, message: '至少输入2个字符' }
        ],
        databaseTye: [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        apiGroupId: [
          { required: true, message: '所属分组不能为空', trigger: 'change' }
        ],
        path: [
          {
            required: true,
            message: '接口地址不能为空',
            trigger: ['blur', 'change']
          }
        ],
        requestType: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        responseType: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        'apiGenerateSaveRequest.model': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        'apiGenerateSaveRequest.datasourceId': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        'apiGenerateSaveRequest.tableName': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ]
      }
    }
  },

  props: {
    operate: { type: String, default: '' }
  },

  filters: {
    filterTableName(item) {
      const { tableName, comment } = item
      return comment ? `${tableName}（${comment}）` : tableName
    }
  },

  methods: {
    open(options) {
      const { opType, cascaderOptions } = options
      this.options = options
      this.pageLoading = true

      if (opType === 'add') {
        this.cascaderOptions = cascaderOptions
        this.form.apiGroupId = cascaderOptions[0].children[0].value
        this.fetchDataApiPath()
      }

      this.fetchSelectOptionsByKey({
        key: 'datasourceOptions',
        methodName: 'getDatasourceOptions'
      })
    },

    handleJumpBackClick() {
      this.$emit('on-jump')
    },

    // 点击-保存表单
    handleSaveFormClick(saveType) {
      const messageMapping = {
        0: { type: '保存', loading: 'isSaveBtnLoading' },
        1: { type: '发布', loading: 'isPublishBtnLoading' }
      }
      this.$refs.form.validate(valid => {
        if (valid) {
          // eslint-disable-next-line no-unused-vars
          const params = {
            saveType,
            ...this.form
          }

          this[messageMapping[saveType].loading] = true

          API.addApiInfo(params)
            .then(({ success, data }) => {
              if (success) {
                const {
                  id,
                  apiGenerateSaveRequest: gen,
                  apiParamSaveRequestList: param
                } = data

                this.$notify.success({
                  title: '操作结果',
                  message: `${messageMapping[saveType].type}成功！`,
                  duration: 2000
                })

                this.form.id = id
                this.form.apiGenerateSaveRequest = gen
                this.form.apiParamSaveRequestList = param
                this.oldTableData = param

                this.$emit('on-save', saveType)
              }
            })
            .finally(() => {
              this.isSaveBtnLoading = false
              this.isTestBtnLoading = false
              this.isPublishBtnLoading = false
            })
        }
      })
    },

    // 切换-是否设置为请求参数
    handleRequestFieldChange(value, row) {
      row.required = value
    },

    // 切换-数据源名称发生更改
    handleDatasourceNameChange() {
      this.form.apiParamSaveRequestList = []
      this.form.apiGenerateSaveRequest.tableName = undefined
      this.selectTableName = ''

      this.fetchSelectOptionsByKey({
        key: 'dataNameOptions',
        methodName: 'getDataTableOptions'
      })
    },

    // 输入-通过字段名称来获取该张表的数据
    handleTableNameInput() {
      console.log(11111111111111)
      this.tableLoading = true
      this.timerId && clearTimeout(this.timerId)

      this.form.apiParamSaveRequestList = this.oldTableData.filter(
        ({ fieldName }) => {
          const inputNameLowerCase = this.selectTableName?.toLocaleLowerCase()
          const fieldNameLowerCase = fieldName
            ? fieldName.toLocaleLowerCase()
            : fieldName
          return fieldNameLowerCase?.includes(inputNameLowerCase)
        }
      )

      this.timerId = setTimeout(() => {
        this.tableLoading = false
      }, 300)
    },

    // 获取-新增数据源生成时的APIPath
    fetchDataApiPath() {
      API.getDataApiPath()
        .then(({ success, data }) => {
          if (success) {
            this.form.path = data
          }
        })
        .finally(() => {
          this.pageLoading = false
        })
    },

    // 获取-通过数据源ID以及表名称来获取表字段
    fetchTableFields() {
      const { datasourceId, tableName } = this.form.apiGenerateSaveRequest
      const params = { datasourceId, tableName }

      this.selectTableName = ''
      this.tableLoading = true
      API.getDataTableFields(params)
        .then(({ success, data }) => {
          if (success && data) {
            this.form.apiParamSaveRequestList = data
            this.oldTableData = data
          }
        })
        .catch(() => {
          this.apiParamSaveRequestList = []
          this.oldTableData = []
        })
        .finally(() => {
          this.tableLoading = false
        })
    },

    // 获取-数据表名称列表
    fetchSelectOptionsByKey({ methodName, key }) {
      const { databaseTye: type, apiGenerateSaveRequest: r } = this.form
      const params = {
        dataNameOptions: { id: r.datasourceId },
        datasourceOptions: { type }
      }

      API[methodName](params[key])
        .then(({ success, data }) => {
          if (success && data) {
            this[key] = data
          }
        })
        .finally(() => {
          this.pageLoading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '~@/assets/scss/button';
@import '~@/assets/scss/form';

.source-generate {
  @include flex(flex-start, flex-start, column);
  flex: 1;
  width: 100%;
  height: 100%;
  border-left: 1px solid #d9d9d9;
  overflow: hidden;
}

.source-header {
  @include flex(space-between);
  padding: 20px 30px;
  width: 100%;
  box-sizing: border-box;

  &-icon {
    @include flex(space-between);
    width: 100px;
    height: 34px;
    .icon {
      width: 18px;
      height: 18px;
      font-size: 20px;
      cursor: pointer;
      color: #1890ff;
      transform: scale(1);
      transition: transform 0.3s ease-in-out;

      &:hover {
        transform: scale(1.2);
      }
    }

    .icon-right {
      @include flex;
      width: 60px;
      height: 34px;
      text-align: center;
      background: #f4f4f4;
      line-height: 34px;
      border-radius: 17px;
    }
  }
}

.source-main {
  @include flex(flex-start, flex-start);
  flex: 1;
  width: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  box-sizing: border-box;

  &-form {
    @include flex(flex-start, flex-start, column);
    width: 100%;
    padding-right: 29px;
    padding-bottom: 30px;

    &--title {
      position: relative;
      width: 78px;
      height: 20px;
      font-size: 14px;
      font-weight: 400;
      text-align: left;
      color: #262626;
      line-height: 20px;
      padding-left: 15px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        width: 4px;
        height: 18px;
        background: #1890ff;
        border-radius: 0px 2px 2px 0px;
      }
    }

    .form-row-item {
      width: 100%;

      &--tip {
        line-height: 32px;
        height: 32px;
        font-size: 12px;
        font-family: PingFangSC, PingFangSC-Regular;
        font-weight: 400;
        text-align: left;
        color: #999;
      }
    }

    .form-row-table {
      position: relative;
      width: calc(100% - 20px);
      box-sizing: border-box;
      margin-left: 20px;
    }
  }
}
</style>
