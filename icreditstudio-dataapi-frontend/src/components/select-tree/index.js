// 导入组件
import SelectTree from './src/select-tree'
// 为组件提供 install 安装方法，供按需引入
SelectTree.install = Vue => {
  Vue.component(SelectTree.name, SelectTree)
}
// 默认导出组件
export default SelectTree
