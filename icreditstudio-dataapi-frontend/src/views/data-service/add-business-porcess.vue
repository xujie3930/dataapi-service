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
    :hide-footer="false"
    @on-close="close"
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
          v-model="processForm.name"
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
      processForm: { name: '', desc: '' },
      rules: {
        name: [
          { required: true, message: '请输入自定义数据源名称', trigger: 'blur' }
        ]
      }
    }
  },

  methods: {
    open() {
      this.$refs.baseDialog.open()
    },

    close(opType) {
      this.$refs.processForm.resetFields()
      this.$refs.baseDialog.close()
      this.$emit('on-close', opType)
    },

    // 保存
    saveBusinessProcess() {
      this.$refs?.processForm.validate(valid => {
        if (valid) {
          API.addBusinessProcess(this.processForm)
            .then(({ success, data }) => {
              if ((success, data)) {
                console.log(success, data)
                this.$notify.success({
                  title: '操作结果',
                  message: '新增业务流程成功！',
                  duration: 1500
                })
                this.close('save')
              }
            })
            .finally(() => {})
        }
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
