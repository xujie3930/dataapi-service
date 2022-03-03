/*
 * @Description: 根据接口以及配置项渲染详情具体值
 * @Date: 2022-03-03
 */

import API from '@/api/api'
import { cloneDeep } from 'lodash'

export default {
  data() {
    return {
      detailConfiguration: () => ({})
    }
  },

  methods: {
    mixinFetchDetailData({ row }) {
      this.detailLoading = true
      this.detailConfiguration = cloneDeep(this.detailConfiguration(row))

      API.getLogDetail({ id: row.id })
        .then(({ success, data }) => {
          if (success && data) {
            cloneDeep(this.detailConfiguration.base).forEach((item, idx) => {
              const { key, formatter } = item
              const val = data[key]

              this.detailConfiguration.base[idx].value = formatter
                ? typeof formatter(val) === 'object'
                  ? formatter(val)?.name
                  : formatter(val)
                : val

              if ('color' in item) {
                this.detailConfiguration.base[idx].color = formatter(val)?.color
              }
            })
          }
        })
        .finally(() => {
          this.detailLoading = false
        })
    }
  }
}
