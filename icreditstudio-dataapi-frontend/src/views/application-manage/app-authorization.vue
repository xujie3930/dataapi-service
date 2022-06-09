<!--
 * @Description: 授权
 * @Date: 2022-03-02
-->

<template>
  <Dialog
    v-if="isShowDialog"
    ref="baseDialog"
    width="735px"
    footer-placement="center"
    :title="options.title"
    :close-on-click-modal="false"
    @on-change="change"
    @on-confirm="addApiAuthorization"
  >
    <el-form
      v-loading="loading"
      ref="authorizeForm"
      class="icredit-form"
      label-width="120px"
      :model="authorizeForm"
      :rules="rules"
    >
      <template v-if="options.opType === 'add'">
        <el-form-item>
          <div slot="label" class="icredit-form--title">选择授权API</div>
        </el-form-item>

        <!-- <el-form-item label="应用名称">
        <span>{{ authorizeForm.name }}</span>
      </el-form-item> -->

        <el-form-item label="选择API" prop="apiId">
          <el-cascader
            ref="cascader"
            clearable
            filterable
            style="width: 500px"
            placeholder="请选择API"
            :collapse-tags="true"
            :options="apiOptions"
            :props="cascaderProps"
            v-model="authorizeForm.apiId"
            @change="handleCascaderChange"
          ></el-cascader>
          <!-- <JTransferTree
          ref="transferTree"
          :props="{ key: 'id', label: 'name' }"
          :left-tree-data="leftTreeData"
          :right-tree-data="rightTreeData"
          @transfer-data="transferDataCallback"
        /> -->
        </el-form-item>
      </template>

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

        <el-form-item label="可调用次数" prop="durationType">
          <el-radio-group v-model="authorizeForm.durationType">
            <el-radio :label="0">有限次</el-radio>
            <el-radio :label="1">无限次</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="allowCall" v-if="!authorizeForm.durationType">
          <span slot="label"></span>
          <el-input
            style="width: 500px"
            clearable
            show-word-limit
            controls-position="right"
            v-model.number="authorizeForm.allowCall"
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
// import JTransferTree from '@/components/transfer-tree'
import { flattern } from '@/utils'

export default {
  // components: { JTransferTree },

  data() {
    return {
      leftTreeData: [],
      rightTreeData: [],

      timerId: null,
      oldApiId: [],
      options: {},
      cascaderProps: {
        multiple: true,
        // lazy: true,
        // lazyLoad: this.lazyLoad,
        label: 'name',
        value: 'id'
      },
      apiOptions: [],
      loading: false,
      isShowDialog: false,
      veifyNameLoading: false,
      oldGroupName: '',
      authorizeForm: {
        generateId: '',
        name: '',
        desc: '',
        apiId: [],
        appId: '',
        allowCall: '',
        durationType: 0,
        authPeriod: 0,
        validTime: []
      },
      rules: {
        validTime: [
          { required: true, message: '请选择授权有效时间', trigger: 'blur' }
        ],
        allowCall: [
          { required: true, message: '请输入可调用次数', trigger: 'blur' },
          { type: 'number', message: '请输入整数' },
          { min: 0, message: '最小值为0，请重新输入', type: 'number' },
          {
            max: 2147483647,
            message: '最大值为2147483647，请重新输入',
            type: 'number'
          }
        ],
        durationType: [
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
            trigger: ['blur']
          }
        ]
      }
    }
  },

  methods: {
    open(options) {
      const { row } = options
      this.options = options
      this.authorizeForm.name = row?.name
      this.authorizeForm.appId = row?.id
      this.loading = true
      this.fetchApiAuthDetail(row?.id)
      this.isShowDialog = true
      this.$nextTick(() => {
        this.$refs.baseDialog.open()
      })
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
      this.leftTreeData = []
      this.rightTreeData = []
      this.isShowDialog = false
      this.$refs.authorizeForm.resetFields()
    },

    // 点击-新增或编辑API授权
    addApiAuthorization() {
      const { opType, apiIds, row } = this.options
      this.$refs?.authorizeForm.validate(valid => {
        const { appId, allowCall, apiId, durationType, authPeriod, validTime } =
          this.authorizeForm
        const params = {
          durationType,
          appId: opType === 'add' ? appId : row.id,
          apiId: opType === 'add' ? apiId.map(item => item[2]) : apiIds,
          allowCall: durationType ? -1 : allowCall,
          periodBegin: validTime?.length && !authPeriod ? validTime[0] : -1,
          periodEnd: validTime?.length && !authPeriod ? validTime[1] : -1
        }

        const method =
          opType === 'add' ? 'updateApiAuthorization' : 'deployAuthApi'

        !valid
          ? this.$refs.baseDialog.setButtonLoading(false)
          : API[method](params)
              .then(({ success, data }) => {
                if ((success, data)) {
                  this.$notify.success({
                    title: '操作结果',
                    message: '授权配置成功！',
                    duration: 2000
                  })
                  this.$refs.baseDialog.setButtonLoading(false)
                  this.close()
                }
              })
              .catch(() => {
                this.$refs.baseDialog.setButtonLoading(false)
              })
      })
    },

    // 切换-选在API
    handleCascaderChange(value) {
      console.log(value, 'ooo')
      // value.length && (this.oldApiId = value)
    },

    transferDataCallback(data, targetTree) {
      if (targetTree === 'right') {
        const firstLevelNodeIds = [
          ...new Set(data.map(({ grandParentId: gid }) => gid))
        ]
        const secondLevelNodeIds = [...new Set(data.map(item => item.parentId))]
        const threeLevelNodeIds = [...new Set(data.map(item => item.id))]

        const getIds = level =>
          flattern(this.rightTreeData)
            .filter(item => item.level === level)
            .map(item => item.id)
        const selectedFirstLevelNodeIds = getIds(1)
        const selectedSecondLevelNodeIds = getIds(2)
        const selectedThreeLevelNodeIds = getIds(3)

        const firstShowNodeIds = [
          ...new Set([...firstLevelNodeIds, selectedFirstLevelNodeIds])
        ]

        //更新右侧树数据 (want to die ~_~)
        firstShowNodeIds.forEach(id => {
          const node = this.leftTreeData.find(item => item.id === id)
          if (selectedFirstLevelNodeIds.includes(id)) {
            const { children, ...rest } = node
            const idx = this.rightTreeData.findIndex(item => item.id === id)
            const secondNodeShowIds = [
              ...new Set([...selectedSecondLevelNodeIds, ...secondLevelNodeIds])
            ]
            const threeNodeShowIds = [
              ...new Set([...selectedThreeLevelNodeIds, ...threeLevelNodeIds])
            ]

            const child = children
              .filter(item => secondNodeShowIds.includes(item.id))
              .map(item => {
                const { children: secondChild, ...restData } = item

                const threeChild = secondChild
                  .filter(item => threeNodeShowIds.includes(item.id))
                  .map(item => ({ ...item, disabled: false }))

                return {
                  ...restData,
                  disabled: false,
                  children: threeChild
                }
              })

            this.rightTreeData.splice(idx, 1)
            this.rightTreeData.splice(idx, 1, {
              ...rest,
              disabled: false,
              children: child
            })
          } else if (node) {
            const { children, ...rest } = node
            const child = children
              .filter(item => secondLevelNodeIds.includes(item.id))
              .map(item => {
                const { children: c, ...restData } = item
                return {
                  ...restData,
                  children: c.filter(item =>
                    threeLevelNodeIds.includes(item.id)
                  )
                }
              })
            this.rightTreeData.push({ ...rest, children: child })
          }
        })

        // 禁用已从左侧树节点添加到右侧树的节点
        this.setLeftTreeNodeStatus()
      }

      if (targetTree === 'left') {
        data?.forEach(({ grandParentId, id, parentId }) => {
          const gidx = this.rightTreeData.findIndex(
            item => item.id === grandParentId
          )
          if (gidx < 0) return
          const { children: gChild } = this.rightTreeData[gidx] ?? {}
          const pidx = gChild.findIndex(item => item.id === parentId)
          const { children: child } = gChild.find(item => item.id === parentId)
          if (pidx < 0) return
          const idx = child.findIndex(item => item.id === id)
          if (idx < 0) return
          this.rightTreeData[gidx].children[pidx].children.splice(idx, 1)
        })

        this.removeRightTreeNode()

        this.setLeftTreeNodeStatus()
      }
    },

    // 设置左侧树节点禁用状态
    setLeftTreeNodeStatus() {
      const selectedIds = flattern(cloneDeep(this.rightTreeData)).map(
        item => item.id
      )
      const getChildrenData = ({ children, id, ...restData }) => {
        const baseParams = {
          id,
          ...restData,
          disabled: selectedIds.includes(id)
        }

        return children
          ? {
              ...baseParams,
              children: children.map(item => getChildrenData(item))
            }
          : baseParams
      }

      // 左侧树禁用已经选的节点
      this.leftTreeData = cloneDeep(this.leftTreeData).map(item =>
        getChildrenData(item)
      )
    },

    // 移除右侧节点
    removeRightTreeNode() {
      cloneDeep(this.rightTreeData).forEach((item, index) => {
        if (!item?.children || !item?.children.length) {
          this.rightTreeData.splice(index, 1)
        } else {
          item.children.forEach((list, listIndex) => {
            if (!list.children || !list.children.length) {
              this.rightTreeData[index]?.children.splice(listIndex, 1)
              if (!this.rightTreeData[index]?.length) this.removeRightTreeNode()
            }
          })
        }
      })
    },

    // 级联-懒加载
    lazyLoad({ level, value, data }, resolve) {
      level === 1
        ? this.fetchApiGroupList(value, resolve, data)
        : level === 2
        ? this.fetchApiList(value, resolve, data)
        : resolve([])
    },

    // 获取-三级 API节点
    fetchApiList(id, resolve, options) {
      API.getApiInfoList({ apiGroupIds: [id] })
        .then(({ success, data }) => {
          if (success && data) {
            const { children } = options
            const curApiIds = children?.map(({ id }) => id) ?? []
            const apiList = data
              .filter(item => !curApiIds.includes(item.id))
              .map(({ id, name }) => {
                return {
                  id,
                  name,
                  children: [],
                  leaf: true
                }
              })
            resolve(apiList)
            // this.authorizeForm.apiId = this.oldApiId
          }
        })
        .catch(() => resolve([]))
    },

    // 获取-二级 API分组节点
    fetchApiGroupList(id, resolve, options) {
      API.getGroupList({ workIds: [id] })
        .then(({ success, data }) => {
          if (success && data) {
            const { children } = options
            const curGroupIds = children?.map(({ id }) => id) ?? []
            const apiGroupList = data
              .filter(item => !curGroupIds.includes(item.id))
              .map(({ id, name }) => {
                return {
                  id,
                  name,
                  children: [],
                  leaf: false
                }
              })

            resolve(apiGroupList)
            // this.authorizeForm.apiId = this.oldApiId
          }
        })
        .catch(() => resolve([]))
    },

    // 获取-一级 业务流程
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
      this.authorizeForm.validTime = []
      this.authorizeForm.allowCall = undefined

      API.getAppAuthDetail({ appId })
        .then(({ success, data }) => {
          if (success && data) {
            const { noApiCascadeInfoStrList, apiCascadeInfoStrList } = data

            const {
              allowCall,
              authEffectiveTime,
              callCountType,
              periodBegin,
              periodEnd
            } = data.authResult ?? {}

            // 级联回显
            this.apiOptions = noApiCascadeInfoStrList ?? []
            this.authorizeForm.apiId = []
            this.oldApiId = []

            this.leftTreeData = []
            this.leftTreeData = noApiCascadeInfoStrList

            apiCascadeInfoStrList?.forEach(({ id: pid, children: groups }) => {
              groups?.forEach(({ id: gid, children: apis }) => {
                apis?.forEach(({ id }) =>
                  this.authorizeForm.apiId.push([pid, gid, id])
                )
              })
            })

            this.authorizeForm.allowCall = allowCall < 0 ? undefined : allowCall
            this.authorizeForm.durationType = callCountType
            this.authorizeForm.authPeriod = authEffectiveTime
            this.oldApiId = this.authorizeForm.apiId

            if (periodBegin > -1 && periodEnd > -1) {
              this.authorizeForm.validTime = [periodBegin, periodEnd]
            }
          }
        })
        .finally(() => {
          this.loading = false
          // this.fetchBusinessProcessList()
        })
    }
  }
}
</script>
