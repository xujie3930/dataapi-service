<!--
 * @Description: 新增业务流程
 * @Date: 2022-02-21
-->
<template>
  <Dialog
    ref="baseDialog"
    class="datasource-dialog"
    width="600px"
    title="新建业务流程"
    footer-placement="center"
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
          maxlength="200"
          show-word-limit
          placeholder="请输入业务描述"
        ></el-input>
      </el-form-item>
    </el-form>
  </Dialog>
</template>

<script>
import API from '@/api/api'

export default {
  data() {
    return {
      opType: '',
      options: {},
      processForm: { name: '', desc: '' },
      rules: {
        name: [
          { required: true, message: '请输入业务流程名称', trigger: 'blur' },
          { min: 2, message: '请至少输入2个字符' },
          { max: 50, message: '最多只能输入50个字符' }
        ]
      }
    }
  },

  methods: {
    open(options) {
      this.options = options
      this.$refs.baseDialog.open()
    },

    close(opType) {
      const options = { opType, command: 'process', ...this.options }
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

    // 保存
    saveBusinessProcess() {
      this.$refs?.processForm.validate(valid => {
        !valid
          ? this.$refs?.baseDialog.setButtonLoading(false)
          : API.addBusinessProcess(this.processForm)
              .then(({ success, data }) => {
                if ((success, data)) {
                  this.$notify.success({
                    title: '操作结果',
                    message: '新增业务流程成功！',
                    duration: 1500
                  })
                  this.close('save')
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
