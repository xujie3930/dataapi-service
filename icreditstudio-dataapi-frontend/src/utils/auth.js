import Cookies from 'js-cookie'

const TokenKey = 'Authorization'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token, config) {
  return Cookies.set(TokenKey, token, config)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
