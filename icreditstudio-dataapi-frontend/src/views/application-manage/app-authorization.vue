<!--
 * @Description: 授权
 * @Date: 2022-03-02
-->

<template>
  <Dialog
    ref="baseDialog"
    width="735px"
    footer-placement="center"
    :title="options.title"
    :close-on-click-modal="false"
    @on-change="change"
    @on-confirm="addApiAuthorization"
  >
    <el-form
      ref="authorizeForm"
      class="icredit-form"
      label-width="120px"
      :model="authorizeForm"
      :rules="rules"
      v-loading="loading"
    >
      <el-form-item>
        <div slot="label" class="icredit-form--title">选择授权应用</div>
      </el-form-item>

      <el-form-item label="应用名称">
        <span>{{ authorizeForm.name }}</span>
      </el-form-item>

      <el-form-item label="选择API" prop="apiId">
        <el-cascader
          filterable
          style="width: 500px"
          placeholder="请选择API"
          collapse-tags
          :options="apiOptions"
          :props="cascaderProps"
          v-model="authorizeForm.apiId"
          clearable
        ></el-cascader>
      </el-form-item>

      <template v-if="!!authorizeForm.apiId.length">
        <el-form-item>
          <div slot="label" class="icredit-form--title">设置授权时间</div>
        </el-form-item>

        <el-form-item label="授权有效期" prop="authPeriod">
          <el-radio-group v-model="authorizeForm.authPeriod">
            <el-radio :label="0">短期</el-radio>
            <el-radio :label="1">永久</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="!authorizeForm.authPeriod"
          label="选择日期时间"
          prop="validTime"
        >
          <el-date-picker
            style="width: 500px"
            v-model="authorizeForm.validTime"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd HH:mm"
            value-format="timestamp"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="可调用次数" prop="callType">
          <el-radio-group v-model="authorizeForm.callType">
            <el-radio :label="0">有限次</el-radio>
            <el-radio :label="1">无限次</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="allowCall" v-if="!authorizeForm.callType">
          <span slot="label"></span>
          <el-input
            style="width: 500px"
            clearable
            show-word-limit
            controls-position="right"
            v-model.number.trim="authorizeForm.allowCall"
            placeholder="请输入调用次数"
          >
            <template slot="append">次</template>
          </el-input>
        </el-form-item>
      </template>
    </el-form>
  </Dialog>
</template>

<script>
import API from '@/api/api'
import { cloneDeep } from 'lodash'

export default {
  data() {
    return {
      timerId: null,
      options: {},
      cascaderProps: {
        multiple: true,
        lazy: true,
        lazyLoad: this.lazyLoad,
        label: 'name',
        value: 'id'
      },
      apiOptions: [],
      loading: false,
      veifyNameLoading: false,
      oldGroupName: '',
      authorizeForm: {
        generateId: '',
        name: '',
        desc: '',
        apiId: [],
        appId: '',
        allowCall: '',
        callType: 0,
        authPeriod: 0,
        validTime: []
      },
      rules: {
        validTime: [
          { required: true, message: '请选择授权有效时间', trigger: 'blur' }
        ],
        allowCall: [
          { required: true, message: '请输入可调用次数', trigger: 'blur' },
          { type: 'number', message: '请输入整数' }
        ],
        callType: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        authPeriod: [
          { required: true, message: '必填项不能为空', trigger: 'blur' }
        ],
        apiId: [
          {
            required: true,
            type: 'array',
            message: '必填项不能为空',
            trigger: 'change'
          }
        ]
      }
    }
  },

  methods: {
    async open(options) {
      const { row } = options
      this.options = options
      this.authorizeForm.name = row?.name
      this.authorizeForm.appId = row?.id
      const isFinish = await this.fetchApiAuthDetail(row?.id)
      isFinish && this.fetchBusinessProcessList()
      this.$refs.baseDialog.open()
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
      this.$refs.authorizeForm.resetFields()
    },

    // 点击-新增或编辑API授权
    addApiAuthorization() {
      this.$refs?.authorizeForm.validate(valid => {
        const { appId, allowCall, apiId, callType, validTime } =
          this.authorizeForm
        const params = {
          appId,
          apiId: apiId.map(item => item[2]),
          allowCall: callType ? -1 : allowCall,
          periodBegin: validTime.length ? validTime[0] : -1,
          periodEnd: validTime.length ? validTime[1] : -1
        }

        !valid
          ? this.$refs.baseDialog.setButtonLoading(false)
          : API.updateApiAuthorization(params)
              .then(({ success, data }) => {
                if ((success, data)) {
                  this.$notify.success({
                    title: '操作结果',
                    message: '授权成功！',
                    duration: 1500
                  })
                  this.close()
                }
              })
              .finally(() => {
                this.$refs.baseDialog.setButtonLoading(false)
              })
      })
    },

    // 级联-懒加载
    lazyLoad({ level, value }, resolve) {
      level === 1
        ? this.fetchApiGroupList(value, resolve)
        : level === 2
        ? this.fetchApiList(value, resolve)
        : resolve([])
    },

    // 获取-API节点
    fetchApiList(id, resolve) {
      API.getApiInfoList({ apiGroupIds: [id] })
        .then(({ success, data }) => {
          if (success && data) {
            const apiList = data.map(({ id, name }) => {
              return {
                id,
                name,
                children: [],
                leaf: true
              }
            })
            resolve(apiList)
          }
        })
        .catch(() => resolve([]))
    },

    // 获取-API分组节点
    fetchApiGroupList(id, resolve) {
      API.getGroupList({ workIds: [id] })
        .then(({ success, data }) => {
          if (success && data) {
            const apiGroupList = data.map(({ id, name }) => {
              return {
                id,
                name,
                children: [],
                leaf: false
              }
            })
            resolve(apiGroupList)
          }
        })
        .catch(() => resolve([]))
    },

    // 获取-业务流程
    fetchBusinessProcessList() {
      const processIds = cloneDeep(this.apiOptions).map(({ id }) => id)

      API.getBusinessPcoessList().then(({ success, data }) => {
        if (success) {
          const newApiOptions = data
            .filter(item => !processIds.includes(item.id))
            .map(({ id, name }) => {
              return {
                id,
                name,
                leaf: false
              }
            })

          this.apiOptions = cloneDeep([...newApiOptions, ...this.apiOptions])
        }
      })
    },

    // 获取-API授权详情
    fetchApiAuthDetail(appId) {
      return API.getAppAuthDetail({ appId })
        .then(({ success, data }) => {
          if (success && data) {
            const {
              apiCascadeInfoStrList,
              authResult: {
                allowCall,
                authEffectiveTime,
                callCountType,
                periodBegin,
                periodEnd
              }
            } = data

            // 级联回显
            console.log(this.apiOptions, 'apoopop')
            this.apiOptions = apiCascadeInfoStrList
            this.authorizeForm.apiId = []

            apiCascadeInfoStrList?.forEach(({ id: pid, children: groups }) => {
              groups?.forEach(({ id: gid, children: apis }) => {
                apis?.forEach(({ id }) =>
                  this.authorizeForm.apiId.push([pid, gid, id])
                )
              })
            })

            this.authorizeForm.allowCall = allowCall
            this.authorizeForm.callType = callCountType
            this.authorizeForm.authPeriod = authEffectiveTime

            if (periodBegin > -1 && periodEnd > -1) {
              this.authorizeForm.validTime = [periodBegin, periodEnd]
            }

            return true
          }
        })
        .catch(() => {
          return true
        })
    }
  }
}
</script>
