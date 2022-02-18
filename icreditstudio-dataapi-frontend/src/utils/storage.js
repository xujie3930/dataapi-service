/**
 * 判断是否可以使用localStorage
 * @returns {boolean}
 */
export function islocalStorage() {
  return !!window.localStorage
}

/**
 * 获取某个localStorage
 * @param key
 * @returns {string|any}
 */
export function getStore(key) {
  if (islocalStorage) {
    if (!key) {
      throw new Error('getStore参数不存在')
    }
    return deserialize(window.localStorage.getItem(key))
  } else {
    console.log('浏览器不支持localStorage(getStore)')
  }
}

/**
 * 设置某个localStorage
 * @param key
 * @param value
 */
export function setStore(key, value) {
  if (islocalStorage) {
    if (key && !isJSON(key)) {
      window.localStorage.setItem(key, stringify(value))
    } else if (key && isJSON(key) && !value) {
      for (const a in key) {
        if (Object.prototype.hasOwnProperty.call(key, a)) {
          setStore(a, key[a])
        }
      }
    }
  } else {
    console.log('浏览器不支持localStorage(setStore)')
  }
}

/**
 * 删除某个localStorage
 * @param key
 */
export function delStore(key) {
  if (islocalStorage) {
    if (!key) {
      throw new Error('delStore参数不存在')
    }
    window.localStorage.removeItem(key)
  } else {
    console.log('浏览器不支持localStorage(getStore)')
  }
}

/**
 * 序列化
 * @param val
 * @returns {string}
 */
export function stringify(val) {
  return val === undefined || typeof val === 'function'
    ? `${val}`
    : JSON.stringify(val)
}

/**
 * 反序列化
 * @param value
 * @returns {string|undefined|any|undefined}
 */
export function deserialize(value) {
  if (typeof value !== 'string') {
    return undefined
  }
  try {
    return JSON.parse(value)
  } catch (e) {
    return value || undefined
  }
}

/**
 * 是否JSON
 * @param obj
 * @returns {boolean}
 */
export function isJSON(obj) {
  return (
    typeof obj === 'object' &&
    Object.prototype.toString.call(obj).toLowerCase() === '[object object]' &&
    !obj.length
  )
}
