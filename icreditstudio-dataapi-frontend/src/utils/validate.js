/*
 * @Author: lizheng
 * @Description: 校验
 * @Date: 2022-01-22
 */
// 是否包含特殊字符
export const verifySpecialString = str => {
  // 特殊符号
  const regStr =
    /[`~!@#$%^&*()_\-+=<>?:"{}|,./;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘’，。、]/gi
  // 表情包
  const emojiRegStr =
    /[^\u0020-\u007E\u00A0-\u00BE\u2E80-\uA4CF\uF900-\uFAFF\uFE30-\uFE4F\uFF00-\uFFEF\u0080-\u009F\u2000-\u201f\u2026\u2022\u20ac\r\n]/gi
  const isValid = regStr.test(str) || emojiRegStr.test(str)
  return isValid
}

// 是否包含中文
export const verifyIncludeCnString = str => {
  const regExp = /.*[\u4e00-\u9fa5]+.*$/gi
  return regExp.test(str)
}

// 剔除字符串中所有空格
export const strExcludeBlank = str => {
  return str.replace(/\s*/g, '')
}

// 验证IP地址是否合法
export const validIpAddress = ip => {
  const regStr =
    /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
  return regStr.test(ip)
}

// 验证字符串是否输入的是数字和英文
export const verifyStrInputNumberEn = str => /^[\da-zA-Z]+$/i.test(str)

// 当前字符串是否以数字开头
export const verifyStringStartsWithNumber = str => {
  str?.startsWith(typeof parseInt(str))
}
