/*
 * @Author: lizheng
 * @Description: 主应用于子应用进行通信
 * @Date: 2022-01-22
 */

class Shared {}

class SharedModule {
  static shared = new Shared()

  // 重载 shared
  static overloadShared(shared) {
    SharedModule.shared = shared
  }

  // 获取 shared 实例
  static getShared() {
    return SharedModule.shared
  }
}

export default SharedModule
