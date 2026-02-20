import http from './http';

export const loginApi = (payload: { username: string; password: string }) =>
  http.post('/user/login', payload);

export const meApi = () => http.get('/user/me');

export const logoutApi = () => http.post('/user/logout');