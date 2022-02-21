<!--
 * @Description: 数据源生成
 * @Date: 2022-02-19
-->

<template>
  <div class="source-generate">
    <header class="source-header">
      <div class="source-header-icon">
        <i class="icon el-icon-refresh-left" @click="handleJumpBackPage"></i>
        <i class="icon el-icon-video-play"></i>
        <i class="icon el-icon-refresh"></i>
      </div>
      <div class="source-header-btn">
        <el-button>预览</el-button>
        <el-button>测试</el-button>
        <el-button type="primary">提交并发布</el-button>
      </div>
    </header>

    <main class="source-main">1234</main>
  </div>
</template>

<script>
export default {
  methods: {
    handleJumpBackPage() {
      this.$emit('on-jump')
    }
  }
}
</script>

<style lang="scss" scoped>
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
  background: red;

  &-icon {
    @include flex(space-between);
    width: 120px;
    .icon {
      font-size: 20px;
      cursor: pointer;
    }
  }
}

.source-main {
  position: relative;

  flex: 1;
  width: 100%;
  background: green;
}
</style>
