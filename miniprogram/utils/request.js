const { baseUrl } = require('../config/env')

function request(options) {
  const { url, method = 'GET', data, header = {} } = options
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${baseUrl}${url}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...header
      },
      success(res) {
        const body = res.data || {}
        if (res.statusCode >= 200 && res.statusCode < 300 && body.code === 200) {
          resolve(body)
          return
        }
        const message = body.message || `请求失败(${res.statusCode})`
        wx.showToast({ title: message, icon: 'none' })
        reject(new Error(message))
      },
      fail(err) {
        wx.showToast({
          title: '网络异常，请检查后端地址与网络',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

function get(url, data) {
  return request({ url, method: 'GET', data })
}

function post(url, data) {
  return request({ url, method: 'POST', data })
}

/** 把相对封面路径拼成可访问地址 */
function resolveUrl(path) {
  if (!path) return ''
  if (/^https?:\/\//i.test(path)) return path
  if (path.startsWith('//')) return `https:${path}`
  return `${baseUrl}${path.startsWith('/') ? path : `/${path}`}`
}

module.exports = {
  request,
  get,
  post,
  resolveUrl
}
