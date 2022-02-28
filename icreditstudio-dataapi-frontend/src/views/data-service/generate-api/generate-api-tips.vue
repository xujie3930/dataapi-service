<!--
 * @Author: lizheng
 * @Description: SQL编写提示
 * @Date: 2022-02-25
-->
<template>
  <Dialog
    ref="baseDialog"
    class="tips-dialog"
    width="735px"
    title="SQL编写提示"
    :hide-footer="true"
  >
    <h3 class="tips-dialog-title">SQL编写实例：</h3>
    <section
      class="tips-dialog-section"
      v-for="(item, key) in example"
      :key="key"
    >
      <div class="tips-dialog-key tips-dialog-row">{{ key }}</div>
      <div class="tips-dialog-row" v-for="list in item" :key="list.sql">
        <span class="tips-dialog-field" :style="{ color: list.color }">{{
          list.sql
        }}</span>
        <span class="tips-dailog-msg">{{ list.message }}</span>
      </div>
    </section>

    <h3 class="tips-dialog-title">SQL生成API小贴士：</h3>
    <section class="tips-dialog-section">
      <div
        class="tips-dialog-msg"
        v-for="tip in tips"
        :key="tip"
        v-html="tip"
      ></div>
    </section>
  </Dialog>
</template>

<script>
export default {
  data() {
    return {
      example: {
        SELECT: [
          { sql: 'name， ', message: 'SELECT查询的字段即为API返回参数' },
          {
            sql: 'id as uid， ',
            message: '如果定义了字段别名，则返回参数名为字段别名'
          },
          { sql: 'sum(num) as tatal_num， ', message: '支持SQL函数' }
        ],

        FROM: [{ sql: 'table_name， ', message: '表名称', color: '#52C41A' }],
        WHERE: [
          {
            sql: 'userid = ${uid}； ',
            message: '支持SQL函数',
            color: '#faad14'
          }
        ]
      },
      tips: [
        '1、请先选择数据源后在编写SQL语句；',
        '2、只支持输入一条完整的SQL语句；',
        '3、支持同一数据源下的多张表的关联查询；',
        '4、编写好SQL后可点击测试进行参数设置，保证SQL查询语句正确以便API调用者成功调用； ',
        '5、不能输入<span style="color: #ff4d4f"> SELECT * </span>的SQL语句进行查询。'
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.tips-dialog {
  font-family: PingFangSC, PingFangSC-Medium;
  text-align: left;

  &-row {
    margin-left: 15px;
  }

  &-section {
    margin-bottom: 20px;
  }

  &-field {
    color: #1890ff;
    line-height: 22px;
  }

  &-title {
    font-size: 14px;
    text-align: left;
    color: rgba(0, 0, 0, 0.85);
    line-height: 2;
    font-weight: 500;
  }

  &-key,
  &-msg {
    font-size: 14px;
    font-family: PingFangSC, PingFangSC-Regular;
    font-weight: 500;
    text-align: left;
    color: rgba(0, 0, 0, 0.85);
    line-height: 24px;
  }
}
</style>
