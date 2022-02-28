<!--
 * @Description: 新增API分组
 * @Date: 2022-02-21
-->
<template>
  <Dialog
    ref="baseDialog"
    class="datasource-dialog"
    width="600px"
    title="新建API分组"
    footer-placement="center"
  >
    <el-form
      ref="apiGroupForm"
      class="group-form"
      :model="apiGroupForm"
      :rules="rules"
      label-width="120px"
    >
      <el-form-item label="业务流程" prop="workId">
        <el-select
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

export default {
  data() {
    return {
      opType: '',
      options: {},
      processOptions: [],
      apiGroupForm: { workId: '', name: '', desc: '' },
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
          { max: 50, message: '最多只能输入50个字符' }
        ]
      }
    }
  },

  methods: {
    open(options) {
      this.options = options
      this.$refs.baseDialog.open()
      this.fetchBusinessProcessList()
    },

    close(opType) {
      const options = { opType, command: 'group', ...this.options }
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

    //获取-业务流程
    fetchBusinessProcessList() {
      this.isTreeLoading = true
      API.getBusinessProcess()
        .then(({ success, data }) => {
          if (success) {
            this.processOptions = data
          }
        })
        .finally(() => {
          this.isTreeLoading = false
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
                    message: '新增API分组成功！',
                    duration: 1500
                  })
                  this.options.currentTreeNodeId = apiGroupId
                  this.options.workId = workId
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
