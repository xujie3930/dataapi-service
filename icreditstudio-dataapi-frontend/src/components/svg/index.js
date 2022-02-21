/*
 * @Author: lizheng
 * @Description: 全局注册Svg组件
 * @Date: 2022-01-22
 */

import Vue from 'vue'
import JSvg from './src/svg'

Vue.component(JSvg.name, JSvg)

// 加载svg文件
const svg = require.context('@/assets/icons/svg', false, /\.svg$/)
const requireSvgFile = module => module.keys().map(module)
requireSvgFile(svg)
