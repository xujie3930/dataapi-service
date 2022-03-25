<!--
 * @Description: 新增API分组
 * @Date: 2022-02-21
-->
<template>
  <Dialog
    ref="baseDialog"
    class="datasource-dialog"
    width="600px"
    :title="title"
    footer-placement="center"
    :hide-footer="isHideFooter"
    :close-on-click-modal="isHideFooter"
    @on-change="change"
    @on-confirm="saveApiGroup"
  >
    <el-form
      ref="apiGroupForm"
      class="group-form"
      :model="apiGroupForm"
      :rules="options.opType === 'view' ? {} : rules"
      label-width="120px"
      v-loading="pageLoading"
      :disabled="options.opType === 'view'"
    >
      <el-form-item label="业务流程" prop="workId">
        <el-select
          filterable
          v-model="apiGroupForm.workId"
          style="width: 100%"
          placeholder="请选择业务流程"
        >
          <el-option
            v-for="item in processOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="API分组名称" prop="name">
        <el-input
          type="textarea"
          maxlength="50"
          :autosize="{ maxRows: 2 }"
          v-model="apiGroupForm.name"
          placeholder="请输入API分组名称"
        ></el-input>

        <p class="group-form-tip">
          API分组名称，必须唯一，支持汉字、英文字母、数字、英文格式的下划线，必须以英文字母或者汉字开头，2~50个字
        </p>
      </el-form-item>

      <el-form-item label="分组描述" prop="desc">
        <el-input
          type="textarea"
          v-model="apiGroupForm.desc"
          placeholder="请输入分组描述"
          show-word-limit
          maxlength="200"
        ></el-input>
      </el-form-item>
    </el-form>
  </Dialog>
</template>

<script>
import API from '@/api/api'
import { verifySpecialString, strExcludeBlank } from '@/utils/validate'

export default {
  data() {
    return {
      opType: '',
      title: '',
      pageLoading: false,
      isHideFooter: false,
      options: {},
      processOptions: [],
      apiGroupForm: { id: '', workId: '0', name: '', desc: '' },
      rules: {
        workId: [
          { required: true, message: '必填项不能为空', trigger: 'change' }
        ],
        name: [
          {
            required: true,
            message: '请输入API分组名称',
            trigger: 'blur'
          },
          { min: 2, message: '请至少输入2个字符' },
          { max: 50, message: '最多只能输入50个字符' },
          { validator: this.verifyApiGroupName, trigger: 'blur' }
        ]
      }
    }
  },

  methods: {
    open(options) {
      const { opType, id, desc, name, workId } = options
      this.title =
        opType === 'edit'
          ? '编辑API分组'
          : opType === 'view'
          ? '查看API分组'
          : '新增API分组'
      this.isHideFooter = opType === 'view'
      this.options = options
      this.$refs.baseDialog.open()
      this.fetchBusinessProcessList()
      if (opType === 'addGroup') {
        this.$nextTick(() => {
          this.apiGroupForm.workId = workId
        })
      }

      if (opType === 'edit' || opType === 'view') {
        this.$nextTick(() => {
          this.apiGroupForm.id = id
          this.apiGroupForm.name = name
          this.apiGroupForm.desc = desc
          this.apiGroupForm.workId = workId
        })
      }
    },

    close() {
      const options = { command: 'group', ...this.options }
      this.$refs.apiGroupForm.resetFields()
      this.$refs.baseDialog.close()
      this.$emit('on-close', options)
    },

    change(visible) {
      !visible && this.reset()
    },

    reset() {
      this.$refs.apiGroupForm.resetFields()
    },

    // 校验
    verifyApiGroupName(rule, value, cb) {
      this.apiGroupForm.name = strExcludeBlank(value)

      if (cb) {
        verifySpecialString(value.replaceAll('_', ''))
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : isNaN(parseInt(value)) && !value.startsWith('_')
          ? cb()
          : cb(new Error('请输入以中文或英文开头的名称'))
      }
    },

    //获取-业务流程
    fetchBusinessProcessList() {
      this.pageLoading = true
      API.getBusinessProcess()
        .then(({ success, data }) => {
          if (success && data) {
            const defalutProcess = data.filter(({ id }) => id === '0')
            const restProcess = data.filter(({ id }) => id !== '0')
            this.processOptions = [...defalutProcess, ...restProcess]
          }
        })
        .finally(() => {
          this.pageLoading = false
        })
    },

    // 点击-保存分组表单
    saveApiGroup() {
      this.$refs?.apiGroupForm.validate(valid => {
        !valid
          ? this.$refs.baseDialog.setButtonLoading(false)
          : API.addApiGroup(this.apiGroupForm)
              .then(({ success, data }) => {
                if ((success, data)) {
                  const { apiGroupId, workId } = data
                  this.$notify.success({
                    title: '操作结果',
                    message: `${
                      this.options.opType === 'edit' ? '编辑' : '新增'
                    }API分组成功！`,
                    duration: 1500
                  })
                  this.options.workId = workId
                  this.options.currentTreeNodeId = apiGroupId
                  this.close()
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

<style lang="scss" scoped>
.group-form-tip {
  font-size: 12px;
  font-family: PingFangSC, PingFangSC-Regular;
  font-weight: 400;
  text-align: left;
  color: #999;
  line-height: 2;
}
</style>
