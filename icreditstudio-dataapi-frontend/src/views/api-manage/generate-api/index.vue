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
        <!-- <el-button class="jui-button--default" :disabled="true">预览</el-button>
        <el-button
          :disabled="true"
          :loading="isTestBtnLoading"
          class="jui-button--default"
        >
          测试
        </el-button> -->
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
                :disabled="options.opType === 'edit'"
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
          <el-col :lg="{ span: 11 }" v-if="form.type">
            <el-form-item label="API模式" prop="apiGenerateSaveRequest.model">
              <el-select
                :disabled="options.opType === 'edit'"
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
                :disabled="options.opType === 'edit'"
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
                disabled
                style="width: 100%"
                v-model="form.requestType"
                placeholder="请选择请求方式"
              >
                <el-option label="GET" value="GET"></el-option>
                <!-- <el-option label="POST" value="POST"></el-option> -->
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
                v-if="isShowCascader"
                style="width: 100%"
                :disabled="true"
                v-model="form.apiGroupId"
                :props="cascaderProps"
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

        <!-- API类型为注册API-->
        <div v-if="form.type === API_TYPE_MAPPING.REGISTER" style="width: 100%">
          <el-form-item>
            <div slot="label" class="source-main-form--title">后端服务定义</div>
          </el-form-item>

          <el-row type="flex" class="form-row-item" justify="space-between">
            <el-col :span="11">
              <el-form-item label="后台服务Host" prop="reqHost">
                <el-input
                  clearable
                  style="width: 100%"
                  v-model.trim="form.reqHost"
                  placeholder="请输入后台服务Host"
                >
                </el-input>
                <p class="form-row-item--tip">
                  以http://开头，后台服务的IP地址、端口
                </p>
              </el-form-item>
            </el-col>

            <el-col :span="11">
              <el-form-item label="后台Path" prop="reqPath">
                <el-input
                  clearable
                  style="width: 100%"
                  v-model.trim="form.reqPath"
                  placeholder="请输入后台Path"
                >
                </el-input>
                <p class="form-row-item--tip">
                  支持英文、数字、下划线、连字符（-）、斜杠（/），请手动输入
                </p>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <div slot="label" class="source-main-form--title">请求参数定义</div>
          </el-form-item>

          <el-row class="form-row-item form-row-table">
            <JTable
              ref="selectParamTable"
              :table-loading="tableLoading"
              :table-data="form.registerRequestParamSaveRequestList"
              :table-configuration="tableRequestConfiguration"
            >
              <!-- 是否必填 -->
              <template #required="{ row }">
                <el-checkbox
                  :true-label="0"
                  :false-label="1"
                  v-model="row.required"
                ></el-checkbox
              ></template>

              <!--  空数据 -->
              <div
                class="custom-table-empty"
                slot="empty"
                @click="handleAddParamRowClick('Request')"
              >
                <JSvg class="add-icon" svg-name="add" />
                <span class="add-label">新增参数</span>
              </div>

              <!-- append -->
              <div
                v-if="form.registerRequestParamSaveRequestList.length"
                class="custom-table-empty custom-table-append"
                slot="append"
                @click="handleAddParamRowClick('Request')"
              >
                <JSvg class="add-icon" svg-name="add" />
                <span class="add-label">新增一行</span>
              </div>
            </JTable>
          </el-row>

          <el-form-item>
            <div slot="label" class="source-main-form--title">返回参数定义</div>
          </el-form-item>

          <el-row class="form-row-item form-row-table">
            <JTable
              ref="selectParamTable"
              :table-loading="tableLoading"
              :table-data="form.registerResponseParamSaveRequestList"
              :table-configuration="tableResponseConfiguration"
            >
              <!--  空数据 -->
              <div
                class="custom-table-empty"
                slot="empty"
                @click="handleAddParamRowClick('Response')"
              >
                <JSvg class="add-icon" svg-name="add" />
                <span class="add-label">新增参数</span>
              </div>

              <!-- append -->
              <div
                v-if="form.registerResponseParamSaveRequestList.length"
                class="custom-table-empty custom-table-append"
                slot="append"
                @click="handleAddParamRowClick('Response')"
              >
                <JSvg class="add-icon" svg-name="add" />
                <span class="add-label">新增一行</span>
              </div>
            </JTable>
          </el-row>
        </div>

        <!-- API类型为数据源生成-->
        <template v-else>
          <el-form-item>
            <div slot="label" class="source-main-form--title">选择表</div>
          </el-form-item>

          <!-- API模式为单表模式生成API -->
          <template
            v-if="form.apiGenerateSaveRequest.model === API_MODE_MAPPING.TABLE"
          >
            <el-row type="flex" class="form-row-item" justify="space-between">
              <el-col :span="11">
                <el-form-item
                  label="数据库类型"
                  prop="apiGenerateSaveRequest.databaseType"
                >
                  <el-select
                    filterable
                    style="width: 100%"
                    placeholder="请选择数据库类型"
                    :disabled="options.opType === 'edit'"
                    v-model="form.apiGenerateSaveRequest.databaseType"
                    @change="handleDatabaseTypeChange"
                  >
                    <el-option label="MySQL" :value="1"></el-option>
                    <el-option label="Postgresql" :value="3"></el-option>
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
                    :disabled="options.opType === 'edit'"
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
                    :disabled="
                      !form.apiGenerateSaveRequest.datasourceId ||
                      options.opType === 'edit'
                    "
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
                    @change="handleRequireFieldChange($event, row)"
                  ></el-checkbox
                ></template>
              </JTable>
            </el-row>
          </template>

          <!-- API模式为SQL模式生成 -->
          <template
            v-else-if="
              form.apiGenerateSaveRequest.model === API_MODE_MAPPING.SQL
            "
          >
            <el-row type="flex" class="form-row-item" justify="space-between">
              <el-col :span="11">
                <el-form-item
                  label="数据库类型"
                  prop="apiGenerateSaveRequest.databaseType"
                >
                  <el-select
                    filterable
                    style="width: 100%"
                    placeholder="请选择数据库类型"
                    v-model="form.apiGenerateSaveRequest.databaseType"
                    @change="handleDatabaseTypeChange"
                  >
                    <el-option label="MySQL" :value="1"></el-option>
                    <el-option label="Postgresql" :value="3"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>

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
            </el-row>

            <el-form-item>
              <div slot="label" class="source-main-form--title">
                编写SQL语句
              </div>
            </el-form-item>

            <el-row type="flex" justify="space-between" class="form-row-item">
              <el-col class="form-row-item--col" style="padding-left: 30px">
                <p class="tip" @click="handleSqlTipsClick">SQL编写提示</p>
                <el-form-item
                  class="sql-form-item"
                  prop="apiGenerateSaveRequest.sql"
                >
                  <el-input
                    type="textarea"
                    :disabled="!form.apiGenerateSaveRequest.datasourceId"
                    :autosize="{ minRows: 4 }"
                    v-model="form.apiGenerateSaveRequest.sql"
                    :placeholder="
                      form.apiGenerateSaveRequest.datasourceId
                        ? '请编写SQL语句'
                        : '请先选择数据源（SQL模式支持多表关联）'
                    "
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </template>
        </template>
      </el-form>
    </main>
    <GenerateApiTips ref="tipsDialog" />
  </div>
</template>

<script>
import GenerateApiTips from './generate-api-tips'
import {
  verifySpecialString,
  verifyOnlyEnString,
  strExcludeBlank,
  validIpAddress,
  verifyIncludeCnString
} from '@/utils/validate'
import API from '@/api/api'
import {
  dataServiceParamTableConfig,
  tableRequestConfiguration,
  tableResponseConfiguration
} from '@/configuration/table'

import { cloneDeep } from 'lodash'

// API模式
const API_MODE_MAPPING = { TABLE: 0, SQL: 1, CHAIN: 2 }

// API类型
const API_TYPE_MAPPING = { REGISTER: 0, SOURCE: 1 }

export default {
  components: { GenerateApiTips },

  data() {
    return {
      API_MODE_MAPPING,
      API_TYPE_MAPPING,

      timerId: null,
      tableLoading: false,
      pageLoading: false,
      isSaveBtnLoading: false,
      isTestBtnLoading: false,
      isPublishBtnLoading: false,
      isShowCascader: false,
      oldTableData: [],
      tableConfiguration: dataServiceParamTableConfig,
      tableRequestConfiguration: tableRequestConfiguration(this),
      tableResponseConfiguration: tableResponseConfiguration(this),

      selectTableName: '',
      dataNameOptions: [],
      datasourceOptions: [],
      options: {},
      cascaderOptions: [],
      cascaderProps: {
        // expandTrigger: 'hover',
        lazy: true,
        lazyLoad: this.cascaderLazyLoader
      },
      isDataChange: false,
      oldForm: {},
      form: {
        id: '',
        apiHiId: '',
        type: 0,
        path: '',
        name: '',
        reqPath: '',
        reqHost: '',
        apiGroupId: null,
        requestType: 'GET',
        responseType: 'JSON',
        desc: '',
        apiGenerateSaveRequest: {
          id: '',
          model: 0,
          datasourceId: '',
          tableName: '',
          databaseType: 1,
          sql: ''
        },
        apiParamSaveRequestList: [],
        registerRequestParamSaveRequestList: [],
        registerResponseParamSaveRequestList: []
      },
      formRules: {
        type: [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        name: [
          { required: true, message: 'API名称不能为空', trigger: 'blur' },
          { min: 2, message: '至少输入2个字符', trigger: 'blur' },
          { validator: this.verifyApiName, trigger: 'blur' }
        ],
        apiGroupId: [
          { required: true, message: '所属分组不能为空', trigger: 'change' }
        ],
        path: [
          {
            required: true,
            message: '接口地址不能为空',
            trigger: ['blur', 'change']
          },
          { len: 16, message: '仅支持16位的英文大小写字母组合' },
          { validator: this.verifyPath, trigger: 'blur' }
        ],
        requestType: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        reqHost: [
          { required: true, message: '后台服务Host不能为空', trigger: 'blur' },
          { validator: this.verifyHost, trigger: 'blur' }
        ],
        reqPath: [
          { required: true, message: '后台Path不能为空', trigger: 'blur' },
          { validator: this.verifyReqPath, trigger: 'blur' }
        ],
        responseType: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        'apiGenerateSaveRequest.databaseType': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        'apiGenerateSaveRequest.model': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        'apiGenerateSaveRequest.datasourceId': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        'apiGenerateSaveRequest.tableName': [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        'apiGenerateSaveRequest.sql': [
          { required: true, message: 'SQL语句不能为空', trigger: 'blur' },
          { validator: this.verifySql, trigger: 'blur' }
        ]
      }
    }
  },

  filters: {
    filterTableName(item) {
      const { tableName, comment } = item
      return comment ? `${tableName}（${comment}）` : tableName
    }
  },

  props: {
    opType: {
      type: String
    }
  },

  methods: {
    open(options) {
      const { opType, cascaderOptions } = options
      this.options = options
      this.pageLoading = true

      if (opType === 'add') {
        // this.cascaderOptions = cascaderOptions
        this.isShowCascader = true

        this.form.apiGroupId = [
          cascaderOptions[0].value,
          cascaderOptions[0].children[0].value
        ]
        this.fetchDataApiPath()
        this.fetchSelectOptionsByKey({
          key: 'datasourceOptions',
          methodName: 'getDatasourceOptions'
        })
      }

      if (opType === 'edit') {
        this.fetchDataApiDetail()
      }
    },

    // 校验-API名称
    verifyApiName(rule, value, cb) {
      this.form.name = strExcludeBlank(value)

      if (cb) {
        verifySpecialString(value.replaceAll('_', ''))
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : value.startsWith('_')
          ? cb(new Error('不能以下划线开头，请重新输入'))
          : cb()
      }
    },

    // 校验-后台Host
    verifyHost(rule, value, cb) {
      this.form.reqHost = strExcludeBlank(value)
      const ipStr = value.replaceAll('http://', '')
      const ipStrArr = ipStr.split(':')

      if (cb) {
        !value.startsWith('http://')
          ? cb(new Error('IP地址要以http://开头，请重新输入'))
          : validIpAddress(ipStrArr[0])
          ? /[0-9]$/.test(ipStrArr[1])
            ? cb()
            : cb(new Error('非法端口，请重新输入'))
          : cb(new Error('非法IP地址，请重新输入'))
      }
    },

    // 校验-后台Path
    verifyReqPath(rule, value, cb) {
      this.form.reqPath = strExcludeBlank(value)

      if (cb) {
        const excludeStr = value.replaceAll(/[/_-]+/gi, '', '')
        verifySpecialString(excludeStr)
          ? cb(new Error('Path中包含不规范字符，请重新输入'))
          : verifyIncludeCnString(excludeStr)
          ? cb(new Error('Path中包含中文字符，请重新输入'))
          : cb()
      }
    },

    // 校验-API Path
    verifyPath(rule, value, cb) {
      this.form.path = strExcludeBlank(value)

      if (cb) {
        verifySpecialString(value)
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : verifyOnlyEnString(value)
          ? cb()
          : cb(new Error('仅支持16位的英文大小写字母组合'))
      }
    },

    // 校验-API Path
    verifySql(rule, value, cb) {
      const { datasourceId } = this.form.apiGenerateSaveRequest

      if (cb) {
        API.checkSqlCorrectness({ datasourceId, sql: value })
          .then(({ success, data }) => {
            if (success) {
              data ? cb(new Error(data)) : cb()
            }
          })
          .catch(err => cb(new Error(err)))
      }
    },

    // 判断是否版本覆盖提示
    judgeOverrideToast(param, saveType) {
      this.judeSameVersionData()
      this.isDataChange
        ? this.$confirm(
            '请确认是否生成新的版本？如需生成新的版本请选择“是”，如需保存修改内容到当前版本请选择“否”',
            '提示',
            {
              confirmButtonText: '是',
              cancelButtonText: '否',
              type: 'warning',
              showClose: false,
              closeOnClickModal: false,
              closeOnPressEscape: false
            }
          )
            .then(() => {
              this.saveApiForm({ ...param, override: 0 }, saveType)
            })
            .catch(() => {
              this.saveApiForm({ ...param, override: 1 }, saveType)
            })
        : this.saveApiForm({ ...param, override: 0 }, saveType)
    },

    // 判断新旧两份数据是有变化
    judeSameVersionData() {
      const {
        name,
        desc,
        reqHost,
        reqPath,
        apiGenerateSaveRequest,
        apiParamSaveRequestList,
        registerRequestParamSaveRequestList: reqList,
        registerResponseParamSaveRequestList: resList
      } = this.form

      const {
        paramList,
        name: oldName,
        desc: oldDesc,
        reqHost: oldReqHost,
        reqPath: oldReqPath,
        registerRequestParamSaveRequestList: oldReqList,
        registerResponseParamSaveRequestList: oldResList,
        generateApi: oldApiSaveRequest
      } = this.oldForm

      const { databaseType, sql, datasourceId, tableName } =
        apiGenerateSaveRequest ?? {}

      const {
        databaseType: oldDatabaseType,
        sql: oldSql,
        datasourceId: oldDatasourceId,
        tableName: oldTableName
      } = oldApiSaveRequest ?? {
        datasourceId: '',
        tableName: '',
        databaseType: 1,
        sql: ''
      }

      // 旧数据表单
      const oldForm = {
        name: oldName,
        desc: oldDesc,
        sql: oldSql,
        reqHost: oldReqHost,
        reqPath: oldReqPath,
        datasourceId: oldDatasourceId,
        databaseType: oldDatabaseType,
        tableName: oldTableName,
        apiParamSaveRequestList: paramList.map(
          ({ isRequest, isResponse, required }) => ({
            isRequest,
            isResponse,
            required
          })
        ),
        registerRequestParamSaveRequestList: oldReqList,
        registerResponseParamSaveRequestList: oldResList
      }

      // 新数据表单
      const curForm = {
        name,
        desc,
        sql: sql ?? '',
        reqHost,
        reqPath,
        datasourceId: datasourceId ?? '',
        databaseType: databaseType ?? 1,
        tableName: tableName ?? '',
        apiParamSaveRequestList: apiParamSaveRequestList.map(
          ({ isRequest, isResponse, required }) => ({
            isRequest,
            isResponse,
            required
          })
        ),
        registerRequestParamSaveRequestList: reqList,
        registerResponseParamSaveRequestList: resList
      }

      this.isDataChange = JSON.stringify(oldForm) !== JSON.stringify(curForm)
      console.log(oldForm, curForm, this.isDataChange)
    },

    saveApiForm(params, saveType) {
      const { opType } = this.options
      const messageMapping = {
        0: { type: '保存', loading: 'isSaveBtnLoading' },
        1: { type: '发布', loading: 'isPublishBtnLoading' }
      }

      // 更新旧表单数据
      const fieldArr = [
        'name',
        'desc',
        'sql',
        'reqHost',
        'reqPath',
        'datasourceId',
        'databaseType',
        'tableName',
        'apiParamSaveRequestList',
        'registerRequestParamSaveRequestList',
        'registerResponseParamSaveRequestList'
      ]

      fieldArr.forEach(item => (this.oldForm[item] = cloneDeep(params[item])))

      this[messageMapping[saveType].loading] = true
      API[opType === 'add' ? 'addApiInfo' : 'editApiInfo'](params)
        .then(({ success, data }) => {
          if (success) {
            const {
              id,
              apiHiId,
              apiGenerateSaveRequest: gen,
              apiParamSaveRequestList: param
            } = data

            this.$notify.success({
              title: '操作结果',
              message: `${messageMapping[saveType].type}成功！`,
              duration: 2000
            })

            this.form.id = id
            this.form.apiHiId = apiHiId
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
    },

    // 点击-保存表单
    handleSaveFormClick(saveType) {
      const { opType } = this.options
      const { datasourceId, model } = this.form.apiGenerateSaveRequest

      const {
        type,
        registerRequestParamSaveRequestList: req,
        registerResponseParamSaveRequestList: res
      } = cloneDeep(this.form)

      //  API类型为数据源生成API以及API模式为表单生成
      if (opType === 'edit' && type === 1 && !model && !datasourceId) {
        this.$notify.error({
          title: '接口数据异常',
          message: '数据源异常！',
          duration: 2000
        })
      }

      this.$refs.form.validate(valid => {
        if (valid) {
          this.form.registerRequestParamSaveRequestList = cloneDeep(req).filter(
            ({ fieldName, defaultValue, desc }) =>
              !(fieldName === '' && defaultValue === '' && desc === '')
          )

          this.form.registerResponseParamSaveRequestList = cloneDeep(
            res
          ).filter(
            ({ fieldName, defaultValue, desc }) =>
              !(fieldName === '' && defaultValue === '' && desc === '')
          )

          const { apiGroupId, ...restForm } = this.form
          const params = {
            saveType,
            apiGroupId: apiGroupId[1],
            ...restForm
          }

          // 内容有修改的前提下是否需要覆盖旧版本的弹窗提示
          opType === 'edit'
            ? this.judgeOverrideToast(params, saveType)
            : this.saveApiForm(params, saveType)
        }
      })
    },

    handleJumpBackClick() {
      this.isShowCascader = false
      this.$emit('on-jump', this.opType)
    },

    // 点击-打开SQL编写提示弹窗
    handleSqlTipsClick() {
      this.$refs?.tipsDialog.$refs.baseDialog.open()
    },

    // 切换-是否设置为请求参数
    handleRequestFieldChange(value, row) {
      row.required = value
    },

    // 切换-是否必填
    handleRequireFieldChange(value, row) {
      !value && (row.isRequest = value)
    },

    // 切换-数据库类型发生更改
    handleDatabaseTypeChange() {
      this.form.apiGenerateSaveRequest.datasourceId = ''
      this.form.apiParamSaveRequestList = []
      this.form.apiGenerateSaveRequest.tableName = ''
      this.selectTableName = ''
      this.fetchSelectOptionsByKey({
        key: 'datasourceOptions',
        methodName: 'getDatasourceOptions'
      })
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

    // 点击-新增参数
    handleAddParamRowClick(key) {
      const commonParam = {
        fieldName: '',
        fieldType: 'STRING',
        defaultValue: '',
        desc: ''
      }

      this.form[`register${key}ParamSaveRequestList`].push(
        key === 'Response' ? commonParam : { ...commonParam, required: 1 }
      )
    },

    // 点击-删除当前行
    handleDeleteRowClick(options, key) {
      this.form[`register${key}ParamSaveRequestList`].splice(options.$index, 1)
    },

    // 懒加载
    cascaderLazyLoader(node, resolve) {
      const { level, data } = node
      switch (level) {
        case 0:
          this.fetchBusinessProcessList(resolve)
          break
        case 1:
          this.fetchApiGroup(data.value, resolve)
          break

        default:
          resolve([])
          break
      }
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
      const { opType } = this.options
      const { apiGenerateSaveRequest: r } = this.form
      const params = {
        dataNameOptions: { id: r.datasourceId },
        datasourceOptions: { type: r.databaseType }
      }

      API[methodName](params[key])
        .then(({ success, data }) => {
          if (success && data) {
            this[key] = data

            if (
              opType === 'edit' &&
              key === 'datasourceOptions' &&
              this.form.type === 1
            ) {
              const ids = data.map(item => item.id) ?? []
              const { datasourceId, model } = this.form.apiGenerateSaveRequest
              if (!ids.includes(datasourceId)) {
                this.form.apiGenerateSaveRequest.datasourceId = ''
                !model &&
                  this.$notify.error({
                    title: '接口数据异常',
                    message: '数据源异常！',
                    duration: 1500
                  })
              }
            }
          }
        })
        .finally(() => {
          this.pageLoading = false
        })
    },

    // 获取-API详情
    fetchDataApiDetail() {
      const { apiHiId } = this.options
      this.pageLoading = true
      API.getHistoryApiDetail({ apiHiId })
        .then(({ success, data }) => {
          if (success && data) {
            const {
              type,
              apiPath,
              workFlowId,
              apiGroupId,
              paramList,
              generateApi
            } = data
            const fieldArr = [
              'apiHiId',
              'type',
              'name',
              'desc',
              'reqHost',
              'reqPath',
              'registerRequestParamSaveRequestList',
              'registerResponseParamSaveRequestList'
            ]
            fieldArr.forEach(item => (this.form[item] = data[item]))
            this.form.path = apiPath
            this.form.apiGroupId = [workFlowId, apiGroupId]
            this.oldForm = cloneDeep(data)

            // 数据源生成API
            if (type === 1) {
              const fieldArr = [
                'databaseType',
                'tableName',
                'datasourceId',
                'model',
                'sql'
              ]
              fieldArr.forEach(
                item =>
                  (this.form.apiGenerateSaveRequest[item] = generateApi[item])
              )
              this.form.apiParamSaveRequestList = paramList
              this.fetchSelectOptionsByKey({
                key: 'dataNameOptions',
                methodName: 'getDataTableOptions'
              })
            }

            this.fetchSelectOptionsByKey({
              key: 'datasourceOptions',
              methodName: 'getDatasourceOptions'
            })
          }
        })
        .finally(() => {
          this.pageLoading = false
          this.isShowCascader = true
        })
    },

    // 获取-业务流程
    fetchBusinessProcessList(resolve) {
      API.getBusinessProcess()
        .then(({ success, data }) => {
          if (success && data) {
            const treeData = data.map(({ id, name }) => ({
              label: name,
              value: id,
              leaf: false
            }))
            resolve(treeData)
          }
        })
        .catch(() => {
          resolve([])
        })
    },

    // 获取-某个业务流程下的API分组
    fetchApiGroup(id, resolve) {
      API.getBusinessProcessChild({ workId: id })
        .then(({ success, data }) => {
          if (success && data) {
            const children = data.map(({ id, name }) => ({
              label: name,
              value: id,
              leaf: true
            }))
            resolve(children)
          }
        })
        .catch(() => {
          resolve([])
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
      min-width: 78px;
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

      &--col {
        position: relative;

        .tip {
          position: absolute;
          top: -35px;
          right: 0;
          height: 20px;
          font-size: 14px;
          font-weight: 400;
          text-align: left;
          color: #0d95fb;
          line-height: 20px;
          cursor: pointer;
          transition: transform 0.3s ease-in-out;

          &:hover {
            transform: scale(1.1);
          }
        }
      }
    }

    .form-row-table {
      position: relative;
      width: calc(100% - 20px);
      box-sizing: border-box;
      margin-left: 20px;
      margin-bottom: 50px;

      .custom-table-empty {
        @include flex(center, center, row, inline-flex);

        .add-icon {
          @include flex(center, center, row, inline-flex);
          @include hover-scale;
          width: 15px;
          height: 15px;
          cursor: pointer;
        }

        .add-label {
          font-size: 14px;
          font-family: PingFangSC, PingFangSC-Medium;
          font-weight: 500;
          text-align: center;
          color: #1890ff;
          margin-left: 8px;
          cursor: pointer;
        }
      }

      .custom-table-append {
        height: 44px;
      }
    }

    .sql-form-item {
      ::v-deep {
        .el-form-item__content {
          margin-left: 0 !important;
        }
      }
    }
  }
}
</style>
