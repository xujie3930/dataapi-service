<template>
  <div class="iframe-layout-basic-container">
    <div class="iframe-layout-basic-header" v-if="$slots.header">
      <slot name="header"></slot>
    </div>

    <div class="iframe-layout-basic-main" v-if="$slots.content">
      <slot v-if="customHeaderButton" name="operation"></slot>

      <div v-else class="iframe-layout-basic-main-top flex-row-sp-center">
        <slot name="title">
          <span>{{ title }}</span>
        </slot>
        <slot name="menu">
          <div v-if="showMenu">
            <el-button
              type="primary"
              v-if="menuAuth.includes('add')"
              @click="$emit('handleAdd')"
              >新增</el-button
            >
            <el-upload
              v-if="menuAuth.includes('import')"
              style="display: inline-block"
              name="file"
              :headers="headers"
              :auto-upload="true"
              :before-upload="beforeAvatarUpload"
              :limit="1"
              action=""
              accept=".xlsx,.xls"
              :show-file-list="false"
            >
              <el-button style="margin: 0 10px" type="primary">导入</el-button>
            </el-upload>
            <el-button
              v-if="menuAuth.includes('export')"
              type="primary"
              @click="$emit('handleExport')"
              >导出</el-button
            >
            <el-button
              v-for="(item, index) in authcustomBtnConfig"
              :key="index"
              :type="item.type || 'primary'"
              :disabled="item.disabled"
              v-on:[item.options.eventType].native="handleCustomMenuEvent(item)"
              >{{ item.label }}</el-button
            >
          </div>
          <div v-if="showCreate">
            <el-button type="primary" @click="$emit('handleCreate')"
              >生成代码</el-button
            >
          </div>
        </slot>
      </div>
      <slot name="content"></slot>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Container',
  props: {
    title: {
      type: String,
      default: '列表'
    },
    showMenu: {
      type: Boolean,
      default: true
    },
    showCreate: {
      type: Boolean,
      default: false
    },
    customHeaderButton: {
      type: Boolean,
      default: false
    },
    customBtnConfig: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    // 表格头按钮权限
    authcustomBtnConfig() {
      // const btnArr = this.customBtnConfig.filter(({ key, isHide }) => {
      //   return isHide
      //     ? !isHide && this.menuAuth.includes(key)
      //     : this.menuAuth.includes(key)
      // })
      return this.customBtnConfig.filter(({ isHide }) => !isHide)
    }
  },
  data() {
    return {
      headers: {
        'Content-Type': 'multipart/form-data',
        dataType: 'file'
      },
      menuAuth: []
    }
  },
  created() {
    this.menuAuth = this.$route.meta.permissionList || []
  },
  methods: {
    // 上传前对文件的大小的判断
    beforeAvatarUpload(file) {
      const extension = file.name.split('.')[1] === 'xls'
      const extension2 = file.name.split('.')[1] === 'xlsx'
      const isLt2M = file.size / 1024 / 1024 < 10
      if (!extension && !extension2) {
        this.$message({
          message: '上传模板只能是 xls、xlsx格式!',
          type: 'error'
        })
        return false
      }
      if (!isLt2M) {
        this.$message.error('上传模板大小不能超过 10MB!')
        return false
      }
      const data = new FormData()
      data.append('file', file)
      this.$emit('handleImport', data)
      return false
    },
    handleCustomMenuEvent(config) {
      this.$emit('handleCustomMenuEvent', config)
    }
  }
}
</script>
<style scoped lang="scss">
.iframe-layout-basic-container {
  // @include flex(flex-start, flex-start, column);
  // min-height: calc(100vh - 126px);
  // padding: 0 16px 16px;
  width: 100%;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
  padding: 30px 16px;
  background-color: #fff;
}

.iframe-layout-basic-header {
  // padding: 24px 0 0;
  border-bottom: 1px dashed #d9d9d9;
}

.iframe-layout-basic-main-top {
  padding: 19px 0 16px;

  span {
    color: #333;
    font-size: 16px;
    font-weight: bolder;
    letter-spacing: 2px;
  }
}
</style>
