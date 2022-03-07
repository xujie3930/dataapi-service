<!--
 * @Description: 新增或编辑应用
 * @Date: 2022-03-01
-->

<template>
  <Dialog
    ref="baseDialog"
    width="735px"
    footer-placement="center"
    :title="options.title"
    :close-on-click-modal="false"
    @on-change="change"
    @on-confirm="addApp"
  >
    <el-form
      ref="appForm"
      class="icredit-form"
      label-width="120px"
      :model="appForm"
      :rules="rules"
      v-loading="loading"
    >
      <el-form-item label="应用ID" prop="generateId">
        <el-input style="width: 500px" disabled v-model="appForm.generateId">
        </el-input>
      </el-form-item>

      <el-row type="flex" justify="space-between">
        <el-col :span="12">
          <el-form-item label="认证方式" prop="certificationType">
            <el-radio-group v-model="appForm.certificationType">
              <el-radio :label="0">密钥认证</el-radio>
              <el-radio disabled :label="1">证书认证</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="是否启用" prop="isEnable">
            <el-radio-group v-model="appForm.isEnable">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="应用名称" prop="name">
        <el-input
          style="width: 500px"
          v-model.trim="appForm.name"
          placeholder="请输入应用名称"
          show-word-limit
          maxlength="50"
        ></el-input>
      </el-form-item>

      <el-form-item label="应用密钥" prop="secretContent">
        <el-input
          style="width: 500px"
          v-model.trim="appForm.secretContent"
          placeholder="请输入应用密钥"
          :show-password="isShowPassword"
          :maxlength="16"
          show-word-limit
        ></el-input>
      </el-form-item>

      <el-form-item label="分组名称" prop="appGroupId">
        <el-select
          v-model="appForm.appGroupId"
          style="width: 500px"
          placeholder="请选择业务流程"
          :disabled="!!options.row"
        >
          <el-option
            v-for="(item, idx) in groupNameOptions"
            :key="`${item.id}-${idx}`"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="备注" prop="desc">
        <el-input
          style="width: 500px"
          type="textarea"
          v-model="appForm.desc"
          placeholder="请输入分组描述"
          show-word-limit
          maxlength="250"
        ></el-input>
      </el-form-item>

      <el-form-item>
        <div slot="label" class="icredit-form--title">token设置</div>
      </el-form-item>

      <el-form-item label="token有效期" prop="tokenType">
        <el-select
          style="width: 500px"
          v-model="appForm.tokenType"
          placeholder="请选择"
        >
          <el-option
            v-for="item in tokenOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item
        label="选择时间"
        prop="period"
        v-if="appForm.tokenType === 2"
      >
        <el-select
          style="width: 500px"
          v-model="appForm.period"
          placeholder="请选择"
        >
          <el-option
            v-for="item in tokenCustomOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <div slot="label" class="icredit-form--title">设置IP白名单</div>
      </el-form-item>

      <el-form-item label="IP地址" prop="allowIp">
        <el-input
          style="width: 500px"
          type="textarea"
          v-model="appForm.allowIp"
          placeholder="请输入IP地址，IP地址之间以英文逗号隔开"
        ></el-input>
      </el-form-item>
    </el-form>
  </Dialog>
</template>

<script>
import API from '@/api/api'
import { TOEKN_PERIOD } from '@/config/constant'
import { objectConvertToArray, UUStr } from '@/utils'
import {
  verifySpecialString,
  verifyStrInputNumberEn,
  validIpAddress,
  strExcludeBlank
} from '@/utils/validate'

const customTokenOptions = () => {
  let obj = {}
  for (let index = 0; index < 24; index++) {
    obj[index + 1] = `${index + 1}小时`
  }

  return obj
}

export default {
  data() {
    return {
      timerId: null,
      options: {},
      loading: false,
      veifyNameLoading: false,
      isShowPassword: false,
      oldGroupName: '',
      tokenOptions: objectConvertToArray(TOEKN_PERIOD),
      tokenCustomOptions: objectConvertToArray(customTokenOptions()),
      groupNameOptions: [],
      appForm: {
        generateId: '',
        appGroupId: '',
        name: '',
        desc: '',
        secretContent: '',
        tokenType: '',
        allowIp: '',
        isEnable: 1,
        certificationType: 0,
        period: ''
      },
      rules: {
        appGroupId: [
          { required: true, message: '分组名称不能为空', trigger: 'blur' }
        ],

        generateId: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],

        name: [
          { required: true, message: '应用名称不能为空', trigger: 'blur' },
          { validator: this.verifyAppName, trigger: 'blur' }
        ],

        certificationType: {
          required: true,
          message: '认证方式不能为空',
          trigger: 'change'
        },

        isEnable: {
          required: true,
          message: '必填项不能为空',
          trigger: 'change'
        },

        secretContent: [
          {
            required: true,
            message: '应用密钥不能为空',
            trigger: 'blur'
          },
          {
            len: 16,
            message: '输入错误，请输入16位英文字母和数字组合的密钥',
            trigger: 'blur'
          },
          {
            validator: this.verifySecretContent,
            trigger: 'blur'
          }
        ],

        tokenType: {
          required: true,
          message: '请选择token自定义有效时间',
          trigger: 'change'
        },

        period: {
          required: true,
          message: '请选择token自定义有效时间',
          trigger: 'change'
        },

        allowIp: {
          validator: this.verifyAllowId
        }
      }
    }
  },

  methods: {
    open(options) {
      const { row, opType } = options
      this.appForm.appGroupId = row?.id
      opType === 'add' && (this.appForm.secretContent = UUStr({}))
      this.options = options
      this.isShowPassword = true

      this.$refs.baseDialog.open()
      this.fetchAppId()
      this.fetchAppGroupNameOptions()
    },

    close() {
      this.reset()
      this.$refs.baseDialog.close()
      this.$emit('on-close', this.options)
    },

    change(visible) {
      !visible && this.reset()
    },

    reset() {
      this.isShowPassword = false
      this.$refs.appForm.resetFields()
    },

    // 校验-分组名称填写校验
    verifyAppName(rule, value, cb) {
      this.appForm.name = strExcludeBlank(value)
      if (cb) {
        verifySpecialString(value)
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : cb()
      }
    },

    // 校验-分组名称是否唯一
    verifyGroupNameUnique(rule, value, cb) {
      this.timerId = null
      const { id } = this.options
      const { name } = this.appForm

      if (id && this.oldGroupName === value) return cb()

      this.veifyNameLoading = true
      API.checkNameUniqueness({ id, name })
        .then(({ success, data }) => {
          success && data ? cb(new Error('该名称已存在，请重新输入')) : cb()
        })
        .finally(() => {
          this.timerId = setTimeout(() => {
            this.veifyNameLoading = false
          }, 300)
        })
    },

    // 校验-应用密钥
    verifySecretContent(rule, value, cb) {
      this.appForm.secretContent = strExcludeBlank(value)
      if (cb) {
        verifySpecialString(value)
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : verifyStrInputNumberEn(value)
          ? cb()
          : cb('输入错误，请输入16位英文字母和数字组合的密钥')
      }
    },

    // 校验-应用密钥
    verifyAllowId(rule, value, cb) {
      this.appForm.allowIp = strExcludeBlank(value)
      if (cb) {
        const verifyIp = val =>
          validIpAddress(val)
            ? cb()
            : cb('请输入正确的IP地址格式，多个IP则用英文逗号分隔')

        if (value === '') return cb()

        value?.includes(',')
          ? value.split(',').forEach(item => {
              item && verifyIp(item)
            })
          : verifyIp(value)
      }
    },

    // 获取-应用分组ID
    fetchAppId() {
      this.loading = true
      API.getAppUniqueId()
        .then(({ success, data }) => {
          if (success && data) {
            this.appForm.generateId = data
          }
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 获取-应用分组Options
    fetchAppGroupNameOptions() {
      API.getAppGroupList({})
        .then(({ success, data }) => {
          if (success && data) {
            this.groupNameOptions = data
          }
        })
        .finally(() => {})
    },

    // 点击-新增或编辑应用
    addApp() {
      this.$refs?.appForm.validate(valid => {
        !valid
          ? this.$refs.baseDialog.setButtonLoading(false)
          : API.addApp(this.appForm)
              .then(({ success, data }) => {
                if ((success, data)) {
                  this.$notify.success({
                    title: '操作结果',
                    message: '新增应用成功！',
                    duration: 1500
                  })
                  this.close('save')
                }
              })
              .finally(() => {
                this.$refs.baseDialog.setButtonLoading(false)
              })
      })
    }
  }
}
</script>
