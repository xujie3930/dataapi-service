<!--
 * @Description: 操作提示弹窗
 * @Date: 2021-08-18
-->

<template>
  <div>
    <el-dialog
      class="icredit-dialog"
      :width="width"
      :top="top"
      :visible.sync="dialogVisible"
      :close-on-click-modal="closeOnClickModal"
      @close="close"
    >
      <div class="icredit-dialog-title" slot="title">
        <span v-if="beforeTitleName" style="color: #1890ff">
          {{ beforeTitleName }}
        </span>
        {{ title }}
        <span v-if="afterTitleName" style="color: #1890ff">
          {{ afterTitleName }}
        </span>
      </div>
      <div
        :class="[
          'icredit-dialog-content',
          icon ? 'icredit-dialog-content-icon' : ''
        ]"
      >
        <i
          v-if="icon"
          :class="[iconName, 'icon']"
          :style="{ color: iconColor }"
        ></i>
        <slot />
      </div>

      <footer v-if="!hideFooter" slot="footer" class="dialog-footer">
        <slot v-if="footer" name="customFooter" />
        <div v-else>
          <el-button size="mini" @click="close">取 消</el-button>
          <el-button
            size="mini"
            type="primary"
            :loading="btnLoading"
            @click="confirm"
          >
            确 定
          </el-button>
        </div>
      </footer>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Dialog',

  data() {
    return { dialogVisible: false, btnLoading: false }
  },

  props: {
    top: {
      type: String
      // default: '25vh'
    },

    opType: {
      type: String,
      default: 'view'
    },

    width: {
      type: String
    },

    title: {
      type: String,
      default: 'dialog title'
    },

    beforeTitleName: {
      type: String,
      default: ''
    },

    afterTitleName: {
      type: String,
      default: ''
    },

    footer: {
      type: Boolean,
      default: false
    },

    hideFooter: {
      type: Boolean,
      default: false
    },

    icon: {
      type: Boolean,
      default: false
    },

    iconName: {
      type: String,
      default: 'el-icon-warning'
    },

    iconColor: {
      type: String,
      default: '#e6a23c'
    },

    closeOnClickModal: {
      type: Boolean,
      default: true
    }
  },

  methods: {
    open() {
      this.dialogVisible = true
    },

    close() {
      this.dialogVisible = false
      this.btnLoading = false
      this.$emit('on-close')
    },

    btnLoadingClose() {
      this.btnLoading = false
    },

    confirm() {
      this.btnLoading = true
      this.$emit('on-confirm')
    }
  }
}
</script>

<style lang="scss" scoped>
.icredit-dialog {
  ::v-deep {
    .el-dialog__header {
      padding: 16px 0;
    }

    .el-dialog__body {
      max-height: 70vh;
      overflow-y: auto;
    }
  }

  &-title {
    padding-bottom: 16px;
    padding-left: 20px;
    font-size: 16px;
    font-weight: 500;
    font-family: PingFangSC, PingFangSC-Medium;
    text-align: left;
    color: rgba(0, 0, 0, 0.85);
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  }

  &-content {
    margin-top: -26px;
  }

  &-content-icon {
    @include flex(center, flex-start);
    .icon {
      color: #e6a23c;
      font-size: 25px;
      margin-right: 10px;
    }
  }
}
</style>
