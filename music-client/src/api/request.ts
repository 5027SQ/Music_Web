import axios from 'axios'

const service = axios.create({
  baseURL: '', // 保持空，直接写 /api 统一走代理
  timeout: 10000
})

service.interceptors.response.use(
  res => res.data,
  err => Promise.reject(err)
)

export default service