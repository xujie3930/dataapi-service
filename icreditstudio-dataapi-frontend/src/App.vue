<!--
 * @Author: lizheng
 * @Description: App enrty component
 * @Date: 2022-01-16
-->
<template>
  <div
    :class="[
      'icreditstudio-dataapi-app',
      'sub-app',
      $store.getters.appEnv === 'openDataPlatForm' ? 'open-data-platform' : ''
    ]"
  >
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',

  data() {
    return {}
  }
}
</script>

<style>
.icreditstudio-dataapi-app {
  font-family: PingFangSC, PingFangSC-Medium;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #262626;
}

.open-data-platform {
  margin-top: 74px;
}
</style>
