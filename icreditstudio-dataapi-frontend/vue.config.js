/*
 * @Author: lizheng
 * @Description: 项目打包构建参数配置
 * @Date: 2022-01-16
 */

const { name } = require('./package')
const path = require('path')

const resolve = dir => path.join(__dirname, dir)

const publicPath = process.env.VUE_APP_DOMAIN

module.exports = {
  publicPath,
  outputDir: 'dist',
  assetsDir: 'static',
  indexPath: 'index.html',
  filenameHashing: true,
  productionSourceMap: false,

  devServer: {
    port: 8099,
    disableHostCheck: false,
    historyApiFallback: true,
    headers: {
      'Access-Control-Allow-Origin': '*'
    },

    proxy: {
      '/api': {
        target: process.env.VUE_APP_PROXY,
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },

  css: {
    loaderOptions: {
      scss: {
        prependData: `
        @import "@/assets/scss/mixin.scss";
        @import "@/assets/scss/common.scss";
        `
      }
    }
  },

  configureWebpack: {
    resolve: { alias: { '@': resolve('src') } },

    output: {
      library: name,
      libraryTarget: 'umd',
      jsonpFunction: `webpackJsonp_${name}`
    }
  },

  chainWebpack: config => {
    // 第一步：让其他svg loader不要对src/assets/icons/svg进行操作
    // config.module.rule('svg').exclude.add(resolve('src/assets/icons/svg')).end()
    config.module.rule('svg').uses.clear().end()
    config.module
      .rule('svg')
      .exclude.add(/node_modules/)
      .end()

    // 第二步：使用svg-sprite-loader 对 src/assets/icons/svg下的svg进行操作
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/assets/icons/svg'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .end()

    // 字体配置解决loader
    config.module
      .rule('fonts')
      .use('url-loader')
      .loader('url-loader')
      .options({
        limit: 4096,
        esModule: false,
        fallback: {
          loader: 'file-loader',
          options: {
            name: 'static/fonts/[name].[hash:8].[ext]',
            publicPath
          }
        }
      })
      .end()

    // 图片打包配置Loader
    config.module
      .rule('images')
      .use('url-loader')
      .loader('url-loader')
      .options({
        limit: 4096,
        esModule: false,
        fallback: {
          loader: 'file-loader',
          options: {
            name: 'static/img/[name].[hash:8].[ext]',
            publicPath
          }
        }
      })
  }
}
