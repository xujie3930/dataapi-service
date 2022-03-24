<!--
 * @Description: 新增业务流程
 * @Date: 2022-02-21
-->
<template>
  <Dialog
    ref="baseDialog"
    class="datasource-dialog"
    width="600px"
    footer-placement="center"
    :title="title"
    :close-on-click-modal="false"
    @on-close="close"
    @on-change="change"
    @on-confirm="saveBusinessProcess"
  >
    <el-form
      ref="processForm"
      class="process-form"
      :model="processForm"
      :rules="rules"
      label-width="120px"
    >
      <el-form-item label="业务流程名称" prop="name">
        <el-input
          type="textarea"
          v-model="processForm.name"
          maxlength="50"
          :autosize="{ maxRows: 2 }"
          placeholder="请输入业务流程名称"
        ></el-input>

        <p class="process-form-tip">
          业务流程名称，必须唯一，支持汉字、英文字母、数字、英文格式的下划线，必须以英文字母或者汉字开头，2~50个字
        </p>
      </el-form-item>
      <el-form-item label="业务描述" prop="desc">
        <el-input
          type="textarea"
          v-model="processForm.desc"
          maxlength="250"
          show-word-limit
          placeholder="请输入业务描述"
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
      title: '',
      options: {},
      processForm: { id: '', name: '', desc: '' },
      rules: {
        name: [
          { required: true, message: '请输入业务流程名称', trigger: 'blur' },
          { min: 2, message: '请至少输入2个字符' },
          { max: 50, message: '最多只能输入50个字符' },
          { validator: this.verifyBusinessName, trigger: 'blur' }
        ]
      }
    }
  },

  methods: {
    open(options) {
      const { opType } = options
      this.options = options
      this.title = opType === 'edit' ? '编辑业务流程' : '新建业务流程'
      this.$refs.baseDialog.open()

      // 编辑回显
      if (opType === 'edit') {
        const { id, name, desc } = options
        this.processForm.id = id
        this.processForm.name = name
        this.processForm.desc = desc
      }
    },

    close() {
      const options = { command: 'process', ...this.options }
      this.$refs.processForm.resetFields()
      this.$refs.baseDialog.close()
      this.$emit('on-close', options)
    },

    reset() {
      this.$refs.processForm.resetFields()
    },

    change(visible) {
      !visible && this.reset()
    },

    // 校验
    verifyBusinessName(rule, value, cb) {
      this.processForm.name = strExcludeBlank(value)

      if (cb) {
        verifySpecialString(value.replaceAll('_', ''))
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : isNaN(parseInt(value)) && !value.startsWith('_')
          ? cb()
          : cb(new Error('请输入以中文或英文开头的名称'))
      }
    },

    // 保存
    saveBusinessProcess() {
      const { opType } = this.options
      this.$refs?.processForm.validate(valid => {
        !valid
          ? this.$refs?.baseDialog.setButtonLoading(false)
          : API.addBusinessProcess(this.processForm)
              .then(({ success, data }) => {
                if ((success, data)) {
                  this.$notify.success({
                    title: '操作结果',
                    message:
                      opType === 'edit'
                        ? '编辑业务流程成功！'
                        : '新增业务流程成功！',
                    duration: 1500
                  })
                  this.options.workId = data
                  this.close()
                }
              })
              .finally(() => {
                this.$refs?.baseDialog.setButtonLoading(false)
              })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.process-form-tip {
  font-size: 12px;
  font-family: PingFangSC, PingFangSC-Regular;
  font-weight: 400;
  text-align: left;
  color: #999;
  line-height: 2;
}
</style>
