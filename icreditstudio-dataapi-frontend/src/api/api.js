/*
 * @Description: 数据服务接口API
 * @Date: 2022-02-18
 */
import dataServiceApi from './data-service-api'
import dataServiceApp from './data-service-app'
import dataServiceLog from './data-service-log'

export default {
  ...dataServiceApi,
  ...dataServiceApp,
  ...dataServiceLog
}
