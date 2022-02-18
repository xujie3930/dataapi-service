<!--
 * @Author: lizheng
 * @Description: 加载svg图标
 * @Date: 2022-01-22
-->
<template>
  <svg
    :class="svgClass"
    :width="svgWidth"
    :height="svgHeight"
    :style="svgStyle"
    :ariaHidden="ariaHidden"
    @click="handleClick"
  >
    <use :xlink:href="svgFileName"></use>
  </svg>
</template>

<script>
export default {
  name: 'JSvg',

  data() {
    return {}
  },

  props: {
    svgName: String,

    ariaHidden: { type: Boolean, default: false },

    svgClass: { type: [String, Array, Object], default: 'j-svg' },

    svgWidth: { type: [String, Number], default: '48px' },

    svgHeight: { type: [String, Number], default: '48px' },

    svgStyle: [Object, String]
  },

  computed: {
    svgFileName() {
      return `#${this.svgName}`
    }
  },

  methods: {
    handleClick() {
      this.$emit('on-tap', this.svgName)
    }
  }
}
</script>

<style lang="scss" scoped>
.j-svg {
  overflow: hidden;
  display: inline-block;
}
</style>
