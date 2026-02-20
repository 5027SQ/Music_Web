import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/user/login', data)
}