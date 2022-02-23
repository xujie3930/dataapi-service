import { cloneDeep } from 'lodash'
import { upperCaseFirst } from '@/utils'
import { getAction, importFile } from '@/api/index'
import axios from '@/utils/request'
import fileDownload from 'js-file-download'

export default {
  data() {
    return {
      fetchConfig: {},
      lazyFirstInit: true, // lazy模式下首次请求标志
      permissions: [], // 当前路由的权限
      isFilterTableOperationByPermission: true, // 是否通过权限过滤表册操作按钮

      /* ================ 搜索表单相关 ================ ↓ */
      mixinSearchFormItems: [], // 搜索的配置
      mixinSearchFormConfig: { models: {}, refName: 'searchForm' },
      mixinSearchFormFunc: [
        { btnText: '重置', btnEmitName: 'mixinReset' },
        { btnText: '查询', btnEmitName: 'mixinSearch', type: 'primary' }
      ],

      /* ================ 表格相关 ================ ↓ */
      mixinTablePagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
        pagerCount: 5,
        handleSizeChange: this.mixinHandleSizeChange,
        handleCurrentChange: this.mixinHandleCurrentChange
      },
      mixinTableLoading: false,
      mixinTableData: [],
      mixinSelectedData: [], // 表格选中项数据

      /* ================ dialog form 相关 ================ ↓ */
      mixinDialogType: 0, // 0 新增 1 编辑 2详情
      mixinDialogTitle: '',
      mixinDialog: false,
      mixinDialogFormItems: [], // 新增 编辑 options
      mixinDialogFormFunc: [
        {
          btnText: '保存',
          btnEmitName: 'mixinSave',
          loading: false,
          type: 'primary'
        },
        { btnText: '取消', btnEmitName: 'mixinCancel' }
      ],
      mixinDialogFormConfig: { models: {}, refName: 'submitForm' },
      mixinUpdate: {} // 编辑时的行数据
    }
  },

  watch: {
    mixinDialog() {
      // 模态框状态改变时，初始化form表单的校验结果，避免上次的失败校验影响到本次
      this.$nextTick(() => {
        const refDom =
          this.$refs.crud.$refs.form.$refs[this.mixinDialogFormConfig.refName]
        if (refDom) {
          refDom.clearValidate()
        }
      })
    },
    'mixinTablePagination.currentPage': {
      handler(nVal, oVal) {
        nVal !== oVal && this.mixinRetrieveTableData()
      }
    }
  },

  created() {
    // 存储当前路由权限
    this.permissions = Object.freeze(this.$route.meta.permissionList || [])

    // 如果表格配置 则通过权限过滤表格操作按钮
    // if (this.tableConfiguration && this.isFilterTableOperationByPermission) {
    //  this.mixinFilterTableOperationByPermission(this.tableConfiguration.group)
    // }

    if (Array.isArray(this.formOption)) {
      this.mixinSearchFormItems = cloneDeep(this.formOption).filter(
        e => e.isSearch
      )
    }
  },

  methods: {
    /**
     * 处理table勾选项
     */
    toggleTableSelection(rows) {
      if (this.$refs.crud) {
        const refDom =
          this.$refs.crud.$refs?.table?.$refs[this.tableConfiguration.refName]
        if (refDom) {
          if (rows) {
            rows.forEach(row => {
              refDom.toggleRowSelection(row)
            })
          } else {
            refDom.clearSelection()
          }
        }
      }
    },
    /**
     * 通过权限过滤操作按钮 TODO 优化 纯函数
     * @param data
     */
    mixinFilterTableOperationByPermission(data) {
      const _operation = data.find(e => e.type === 'operation')
      if (!_operation) return false // 如果没有操作列
      // 有则通过权限过滤
      _operation.operationList = _operation.operationList.filter(e =>
        this.permissions.includes(e.key)
      )
    },
    /* 获取表格数据 */
    mixinRetrieveTableData() {
      this.toggleTableSelection() // 刷新列表时重置勾选项
      const request = this._request('retrieve')
      if (!request) return
      this.mixinTableLoading = true
      request
        .then(res => {
          if (res.success) {
            const { total, pageNum, pageCount } = res.data
            const _data = this.tableConfiguration?.hasPage
              ? res.data?.list
              : res.data
            this.mixinTableData = this.interceptorsResponseTableData(
              _data || [],
              res
            )
            this.mixinTablePagination.total = total || 0
            this.mixinTablePagination.currentPage =
              pageNum > pageCount ? (pageCount === 0 ? 1 : pageCount) : pageNum
          }
        })
        .catch(err => {
          console.log(err)
        })
        .finally(() => {
          this.mixinTableLoading = false
        })
    },
    /* 懒加载获取表格数据 */
    mixinLazyTableData(row, resolve) {
      const _lazyParams = {
        lazy: true,
        parentId: row.id || '',
        userId: this.userInfo.id || ''
      }
      const request = this._request('lazy', _lazyParams)
      if (!request) return
      this.mixinTableLoading = true
      request
        .then(res => {
          if (res.success) {
            const _data = this.tableConfiguration?.hasPage
              ? res.data?.list
              : res.data
            resolve(this.interceptorsResponseLazyTableData(_data || []))
          }
        })
        .catch(err => {
          console.log(err)
        })
        .finally(() => {
          this.mixinTableLoading = false
        })
    },
    /* 懒加载获取级联选择器数据并渲染节点 */
    mixinLazyTreeData(node, resolve) {
      const { value } = node
      const _lazyParams = {
        lazy: true,
        parentId: value || '',
        userId: this.userInfo.id || ''
      }
      const request = this._request('lazy', _lazyParams)
      if (!request) return
      this.mixinTableLoading = true
      request
        .then(res => {
          if (res.success) {
            const _data = res.data?.list || res.data
            resolve(this.interceptorsResponseLazyTreeData(_data || []))
          }
        })
        .catch(err => {
          console.log(err)
        })
        .finally(() => {
          this.mixinTableLoading = false
        })
    },
    /* 校验表单填写规范 */
    validForm(formName) {
      let validFlag = true
      if (formName) {
        const refDom = this.$refs.crud.$refs.form.$refs[formName]
        if (refDom) {
          refDom.validate(valid => {
            validFlag = valid
          })
        }
      }
      return validFlag
    },
    /* 保存 0 新增 1 编辑 */
    mixinHandleCreateOrUpdate(row) {
      if (this.validForm(this.mixinDialogFormConfig.refName)) {
        const _type = this.mixinDialogType ? 'update' : 'create'
        const request = this._request(_type, row || this.mixinUpdate)
        if (!request) return
        this.mixinDialogFormFunc[0].loading = true
        request
          .then(res => {
            if (res.success) {
              if (
                this[`interceptorsResponse${upperCaseFirst(_type)}`](res.data)
              )
                return
              this.$notify.success({
                title: '成功',
                message: `${this.mixinDialogType ? '修改' : '新增'}成功`
              })
              this.mixinUpdate = {}
              this.mixinHandleReset()
              this.mixinDialog = false
            }
          })
          .catch(err => {
            console.log(err)
          })
          .finally(() => {
            this.mixinDialogFormFunc[0].loading = false
          })
      }
    },
    /* 删除 */
    mixinHandleDelete({ row }, confirmMsg = '确定删除此条数据?') {
      if (row.deleteFlag === 'N') {
        return this.$notify.warning('不可删除已启用项')
      }
      this.$confirm(confirmMsg, '询问', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const request = this._request('delete', row)
          if (!request) return
          request
            .then(res => {
              if (res.success) {
                // 拦截
                if (this.interceptorsResponseDelete()) return
                this.$notify.success({
                  title: '成功',
                  message: '删除成功'
                })
                this.mixinHandleReset()
              }
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    /* 批量删除 */
    mixinHandleMultipleDelete({ validBeforeEvent }) {
      const { mixinSelectedData } = this
      if (!validBeforeEvent || validBeforeEvent(mixinSelectedData)) {
        // 自定义的删除前置校验拦截
        this.$confirm('确定删除选中数据?', '询问', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            const request = this._request('multipleDelete', mixinSelectedData)
            if (!request) return
            request
              .then(res => {
                if (res.success) {
                  // 拦截
                  if (this.interceptorsResponseMultipleDelete()) return
                  this.$notify.success({
                    title: '成功',
                    message: '删除成功'
                  })
                  this.mixinHandleReset()
                }
              })
              .catch(err => {
                console.log(err)
              })
          })
          .catch(() => {})
      }
    },
    /**
     * 每页条数改变的回调
     * @param { Number } size 每页条数
     */
    mixinHandleSizeChange(size) {
      this.mixinTablePagination.pageSize = size
      this.mixinRetrieveTableData()
    },
    /**
     * 页码change的回调
     * @param { Number } current 当前页
     */
    mixinHandleCurrentChange(current) {
      this.mixinTablePagination.currentPage = current
      this.mixinRetrieveTableData()
    },
    /**
     * 当选择项发生变化时的回调
     * @param { Array } selection 选中项
     */
    mixinHandleSelectChange(selection) {
      this.mixinSelectedData = selection
    },
    /* 表格搜索 */
    mixinHandleSearch() {
      this.mixinTablePagination.currentPage = 1
      this.mixinRetrieveTableData()
    },
    /* 表格重置 */
    mixinHandleReset(autoRefreshTable = true) {
      this.$refs.crud
        ? this.$refs.crud.$refs.searchForm.reset()
        : this.$refs.searchForm.reset()
      autoRefreshTable && this.mixinRetrieveTableData()
    },
    mixinHandleLazyTable({ row, resolve }) {
      this.mixinLazyTableData(row, resolve)
    },
    mixinHandleLazyTree({ node, resolve }) {
      this.mixinLazyTreeData(node, resolve)
    },
    mixinHandleAdd() {
      this.mixinUpdate = {}
      this.mixinDialogFormItems = this.interceptorsBeforeAdd(
        cloneDeep(this.formOption)
          .filter(e => !e.addHide)
          .map(e => {
            return Object.assign(e, {
              disabled: e.addDisabled
            })
          })
      )
      this.$refs.crud.updateModels()
      this.mixinDialogType = 0
      this.mixinDialog = true
    },
    mixinHandleImport(file) {
      const newFile = this.interceptorsRequestImport(file)
      if (newFile) {
        // newFile.forEach((value, key) => {
        //   console.log('key %s: value %s', key, value);
        // })
        importFile(this.fetchConfig.import?.url, newFile).then(res => {
          if (res.data.errorCount === 0) {
            this.$message.success('导入成功!')
          } else {
            let errorHtml = ''
            res.data.noPassList.forEach(item => {
              errorHtml += `<p>${item.errorMsg}</p>`
            })
            this.$notify({
              title: `导入完成，已成功${res.data.successCount}条，失败${res.data.errorCount}条`,
              message: errorHtml,
              dangerouslyUseHTMLString: true,
              type: 'warning'
            })
          }
          setTimeout(() => {
            this.mixinRetrieveTableData()
          }, 1500)
        })
      }
    },

    mixinHandleExport() {
      const params = this.interceptorsRequestExport()
      getAction(this.fetchConfig.export?.url, params, {
        responseType: 'blob'
      }).then(res => {
        fileDownload(res, '列表数据导出.xlsx')
      })
    },

    mixinHandleView({ row }, title) {
      Promise.resolve(
        this.interceptorsBeforeView(
          cloneDeep(this.formOption)
            .filter(e => !e.viewHide)
            .map(e => {
              const _currentVal = row[e.ruleProp]
              return Object.assign(e, {
                model: this._isUndefinedOrNull(_currentVal)
                  ? e.model
                  : _currentVal,
                disabled: true
              })
            })
        )
      ).then(_data => {
        this.mixinDialogFormItems = _data
        this.$refs.crud.updateModels(this.mixinDialogFormItems)
        this.mixinDialogFormFunc[0].hide = true
        this.mixinDialogType = 2
        this.mixinDialog = true
        this.mixinDialogTitle = title
      })
    },

    mixinHandleEdit({ row }) {
      this.mixinUpdate = row
      Promise.resolve(
        this.interceptorsBeforeEdit(
          cloneDeep(this.formOption)
            .filter(e => !e.editHide)
            .map(e => {
              const _currentVal = row[e.ruleProp]
              return Object.assign(e, {
                model: this._isUndefinedOrNull(_currentVal)
                  ? e.model
                  : _currentVal,
                disabled: e.editDisabled
              })
            }),
          row
        )
      ).then(_data => {
        this.mixinDialogFormItems = _data
        this.$refs.crud.updateModels(this.mixinDialogFormItems)
        this.mixinDialogType = 1
        this.mixinDialog = true
      })
    },
    mixinHandleCancel() {
      this.mixinDialog = false
      this.mixinDialogFormFunc[0].hide = false // 恢复默认显示下次弹框中的确认按钮
    },

    /**
     * 组合请求参数
     * @param method { 'post' | 'get' | 'put' | 'delete'}
     * @param type { 'retrieve' | 'lazy' | 'create' | 'update' | 'delete'| 'multipleDelete'}
     * @param row {Object}
     * @private
     * @return {Object} 请求参数
     */
    _generateRequestParameters(method, type, row) {
      let _params = {}
      // 是否需要分页
      const _pages = this.tableConfiguration?.hasPage
        ? {
            pageSize: this.mixinTablePagination.pageSize,
            pageNum: this.mixinTablePagination.currentPage
          }
        : {}
      switch (type) {
        case 'retrieve':
          _params = Object.assign(
            {},
            this.mixinSearchFormConfig.retrieveModels || {},
            this.mixinSearchFormConfig.models,
            _pages
          )
          break
        case 'lazy':
          _params = Object.assign({}, row)
          break
        case 'create':
          _params = this.mixinDialogFormConfig.models
          break
        case 'update':
          // eslint-disable-next-line no-case-declarations
          const _key = this.fetchConfig?.update.id || 'id'
          _params = {
            ...this.mixinDialogFormConfig.models,
            id: row[_key]
          }
          break
        case 'delete':
          _params = row
          break
        case 'multipleDelete':
          _params = row
          break
        default:
          _params = {}
      }
      return _params
      // return {
      //   [method === 'get' ? 'params' : 'data']: _params
      // }
    },
    /**
     * 请求
     * @param type
     * @param row {Object} 行数据
     * @return {boolean|AxiosPromise}
     * @private
     */
    _request(type, row) {
      if (!(this.fetchConfig[type] && this.fetchConfig[type].url)) {
        console.warn(`----- fetchConfig 没有配置 ${type} -> url -----`)
        return false
      }
      const { method } = this.fetchConfig[type]
      const _params = this._generateRequestParameters(method, type, row)
      const _config = Object.assign(
        {},
        this.fetchConfig[type],
        {
          [method === 'get' ? 'params' : 'data']: this[
            `interceptorsRequest${upperCaseFirst(type)}`
          ](_params, row)
        } // 调用暴露给外部实例的重写方法
      )
      return axios(_config)
    },
    /**
     * 请求参数拦截 来 修改查询参数
     * @param params
     * @return {*}
     */
    interceptorsRequestRetrieve(params) {
      return params
    },
    /**
     * 请求参数拦截 来 修改lazy模式查询参数
     * @param params
     * @return {*}
     */
    interceptorsRequestLazy(params) {
      return params
    },
    /**
     * 请求参数拦截 来 修改新增参数
     * @param params
     * @return {*}
     */
    interceptorsRequestCreate(params) {
      return params
    },
    /**
     * 请求参数拦截 来 修改 修改 参数
     * @param params
     * @return {*}
     */
    interceptorsRequestUpdate(params) {
      return params
    },
    /**
     * 请求参数拦截 来 修改删除参数
     * @param params
     * @return {*}
     */
    interceptorsRequestDelete(params) {
      const _key = this.fetchConfig?.delete.id || 'id'
      return { ids: [params[_key]] }
    },
    /**
     * 请求参数拦截 来 修改导入参数
     * @param params
     * @return {*}
     */
    interceptorsRequestImport(file) {
      return file
    },
    /**
     * 请求参数拦截 来 修改导出参数
     * @param params
     * @return {*}
     */
    interceptorsRequestExport(
      params = { ...this.mixinSearchFormConfig.models }
    ) {
      return params
    },
    /**
     * 请求参数拦截 来 修改批量删除参数
     * @param params
     * @return {*}
     */
    interceptorsRequestMultipleDelete(params) {
      const _key = this.fetchConfig?.multipleDelete.id || 'id'
      return { ids: params.map(e => e[_key]) }
    },
    /**
     * 暴露给实例的 add edit view 关于formItems的前置操作
     * 比如在编辑时需要禁用个别字段
     */
    interceptorsBeforeAdd(params) {
      return params
    },
    interceptorsBeforeEdit(params) {
      return params
    },
    interceptorsBeforeView(params) {
      return params
    },
    /**
     * 返回数据拦截 来 修改接口返回的表格数据
     * @param data
     * @return {*}
     */
    interceptorsResponseTableData(data) {
      return data
    },

    /**
     * 返回数据拦截 来 修改接口返回的懒加载表格数据
     * @param data
     * @return {*}
     */
    interceptorsResponseLazyTableData(data) {
      return data
    },
    /**
     * 返回数据拦截 来 修改接口返回的懒加载树结构数据
     * @param data
     * @return {*}
     */
    interceptorsResponseLazyTreeData(data) {
      return data
    },
    /**
     * 暴露给实例的相关回调
     * 可以通过 return true 终止内部的后续操作
     */
    interceptorsResponseCreate() {},
    interceptorsResponseUpdate() {},
    interceptorsResponseDelete() {},
    interceptorsResponseMultipleDelete() {},
    _isUndefinedOrNull(val) {
      return val === undefined || val === null
    }
  }
}
