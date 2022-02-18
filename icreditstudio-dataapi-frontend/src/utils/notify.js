/*
 * @Description: 对ELEMENT的Notification优化
 */
import { Notification } from 'element-ui'

const typeArr = ['error', 'success', 'info', 'warning']
let notifyInstance = null
const resetNotify = options => {
  if (notifyInstance) {
    notifyInstance.close()
  }
  notifyInstance = Notification(options)
}

typeArr.forEach(type => {
  resetNotify[type] = options => {
    let optionObj = options
    if (typeof options === 'string') {
      optionObj = {
        message: options
      }
    }
    optionObj.type = type
    return resetNotify(optionObj)
  }
})
export default resetNotify
