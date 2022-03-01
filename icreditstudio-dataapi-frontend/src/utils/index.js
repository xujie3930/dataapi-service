import { sm4Config } from '@/config/index'

/**
 * 时间格式化
 * @param { Date | String } date 时间
 * @param { String } fmt 时间格式 默认值 yyyy-MM-dd hh:mm:ss
 * @return { String | Object }
 */
export const dateFormat = (date, fmt = 'yyyy-MM-dd hh:mm:ss') => {
  const _date = new Date(parseInt(date))
  if (isNaN(_date)) return date
  const o = {
    'M+': _date.getMonth() + 1,
    'd+': _date.getDate(),
    'h+': _date.getHours(),
    'm+': _date.getMinutes(),
    's+': _date.getSeconds(),
    'q+': Math.floor((_date.getMonth() + 3) / 3),
    S: _date.getMilliseconds()
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(
      RegExp.$1,
      `${_date.getFullYear()}`.substr(4 - RegExp.$1.length)
    )
  }
  for (const k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length == 1 ? o[k] : `00${o[k]}`.substr(`${o[k]}`.length)
      )
    }
  }
  return fmt
}

/**
 * 产生任意长度随机字母数字组合
 * @param {Boolean} random-是否任意长度
 * @param {Number} min-是否任意长度或者固定位数
 * @param {Number} max-任意长度最大位
 */
export const UUStr = (random = false, min = 16, max = 32) => {
  let str = '',
    range = min,
    arr = [
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z'
    ]

  // 随机产生
  if (random) {
    range = Math.round(Math.random() * (max - min)) + min
  }

  for (var i = 0; i < range; i++) {
    const pos = Math.round(Math.random() * (arr.length - 1))
    str += arr[pos]
  }

  return str
}

export const isDef = v => v !== undefined && v !== null

export const isUndef = v => v === undefined || v === null

export const imageToBase64 = img => {
  const canvas = document.createElement('canvas')
  canvas.width = img.width
  canvas.height = img.height
  const ctx = canvas.getContext('2d')
  ctx.drawImage(img, 0, 0, img.width, img.height)
  const ext = img.src.substring(img.src.lastIndexOf('.') + 1).toLowerCase()
  const dataURL = canvas.toDataURL('image/' + ext)
  return dataURL
}

/**
 * 首字母大写
 * @param str
 * @return {string}
 */
export const upperCaseFirst = str => str.charAt(0).toUpperCase() + str.slice(1)

// 数据库连接URI切割
export const uriSplit = (uri, dataSource) => {
  let ds = {}
  const { type } = dataSource

  switch (type) {
    case 1:
      ds = mysqlUriSplit(uri, dataSource)
      break
    case 2:
      ds = oracleUriSplit(uri, dataSource)
      break
    default:
      ds = dataSource
      break
  }

  return ds
}

// mySql数据库连接URI切割
export const mysqlUriSplit = (uri, dataSource) => {
  const paramsObj = {}
  const [beforeStr, afterStr] = uri.split('?')

  // 处理查询参数
  const newAfterStr = afterStr.replaceAll('|', '&')
  newAfterStr.split('&').forEach(item => {
    if (item.includes('password=')) {
      paramsObj.password = decrypt(item.split('password=')[1])
    } else {
      const [key, val] = item.split('=')
      paramsObj[key] = val
    }
  })

  // 处理uri类型以及IP以及端口号
  const [databaseType, ipPort, databaseName] = beforeStr
    .split('/')
    .filter(item => item && item.trim())

  const [ip, port] = ipPort.split(':')
  const newDataSource = {
    databaseType,
    ip,
    port,
    databaseName,
    ...dataSource,
    ...paramsObj
  }

  return newDataSource
}

// oracle数据库连接URI切割
export const oracleUriSplit = (uri, dataSource) => {
  const paramsObj = {}
  const [baseStr, nameStr, pwdStr] = uri.split('|')
  const [, databaseType, , ip, port, databaseName] = baseStr.split(':')
  const [username, name] = nameStr.split('=')
  const [, pwd] = pwdStr.split('password=')

  paramsObj[username] = name
  paramsObj.password = decrypt(pwd)

  return {
    ...dataSource,
    ...paramsObj,
    port,
    databaseType,
    databaseName,
    ip: ip.split('@')[1]
  }
}

// 数组去重
export const unique = arr => {
  const obj = {}
  return arr.filter(item => {
    // eslint-disable-next-line no-prototype-builtins
    const flag = obj.hasOwnProperty(typeof item + item)
      ? false
      : (obj[typeof item + item] = true)

    return flag
  })
}

const SM4 = require('gm-crypt').sm4
// 解密
export const encrypt = str => {
  const sm4 = new SM4(sm4Config)
  return sm4.encrypt(str)
}
// 解密
export const decrypt = str => {
  const sm4 = new SM4(sm4Config)
  const plianStr = str ? sm4.decrypt(str) : null
  return plianStr
}

// 前端a标签下载
export const download = options => {
  const { filename, url, target = '_slef', open = false } = options
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.target = target
  document.body.appendChild(link)
  open ? window.open(link.href) : link.click()
  document.body.removeChild(link)
}

// 讲对象转换成数据对象
export const objectConvertToArray = sourceObj => {
  const targetArray = []
  for (const [key, value] of Object.entries(sourceObj ?? {})) {
    targetArray.push({ label: value, value: parseInt(key) })
  }

  return targetArray
}
