<!--
 * @Description: 新增或编辑应用分组
 * @Date: 2022-03-01
-->

<template>
  <Dialog
    ref="baseDialog"
    class="datasource-dialog"
    width="735px"
    footer-placement="center"
    :title="options.title"
    :close-on-click-modal="false"
    @on-change="change"
    @on-confirm="addAppGroup"
  >
    <el-form
      ref="appGroupForm"
      class="group-form"
      label-width="80px"
      :model="appGroupForm"
      :rules="rules"
      v-loading="loading"
    >
      <el-form-item label="分组ID" prop="generateId">
        <el-input
          disabled
          style="width: 100%"
          v-model="appGroupForm.generateId"
        >
        </el-input>
      </el-form-item>

      <el-form-item label="分组名称" prop="name">
        <el-input
          maxlength="50"
          show-word-limit
          v-model.trim="appGroupForm.name"
          placeholder="请输入应用分组名称"
        >
          <i v-if="veifyNameLoading" slot="suffix" class="el-icon-loading"></i>
        </el-input>
      </el-form-item>

      <el-form-item label="备注" prop="desc">
        <el-input
          type="textarea"
          v-model="appGroupForm.desc"
          placeholder="请输入分组描述"
          show-word-limit
          maxlength="250"
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
      timerId: null,
      options: {},
      loading: false,
      veifyNameLoading: false,
      oldGroupName: '',
      appGroupForm: { generateId: '', name: '', desc: '' },
      rules: {
        generateId: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '分组名称不能为空', trigger: 'blur' },
          { validator: this.verifyAppGroupName, trigger: 'blur' }
        ]
      }
    }
  },

  methods: {
    open(options) {
      this.options = options
      this.$refs.baseDialog.open()
      this.fetchAppGroupId()
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
      this.$refs.appGroupForm.resetFields()
    },

    // 校验-分组名称填写校验
    verifyAppGroupName(rule, value, cb) {
      this.appGroupForm.name = strExcludeBlank(value)
      if (cb) {
        verifySpecialString(value)
          ? cb(new Error('该名称中包含不规范字符，请重新输入'))
          : isNaN(parseInt(value))
          ? this.verifyGroupNameUnique(rule, value, cb)
          : cb('请输入以中文或英文开头的名称')
      }
    },

    // 校验-分组名称是否唯一
    verifyGroupNameUnique(rule, value, cb) {
      this.timerId = null
      const { id } = this.options
      const { name } = this.appGroupForm

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

    // 获取-应用分组ID
    fetchAppGroupId() {
      this.loading = true
      API.getAppUniqueId()
        .then(({ success, data }) => {
          if (success && data) {
            this.appGroupForm.generateId = data
          }
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 点击-新增或编辑分组表单
    addAppGroup() {
      this.$refs?.appGroupForm.validate(valid => {
        !valid
          ? this.$refs.baseDialog.setButtonLoading(false)
          : API.addAppGroup(this.appGroupForm)
              .then(({ success, data }) => {
                if ((success, data)) {
                  const { apiGroupId, workId } = data
                  this.$notify.success({
                    title: '操作结果',
                    message: '新增应用分组成功！',
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
