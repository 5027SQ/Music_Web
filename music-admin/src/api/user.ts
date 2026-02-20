import http from './http';

export const userPage = (params: { page: number; size: number; keyword?: string }) =>
  http.get('/admin/user/page', { params });

export const userUpdateStatus = (id: number, status: number) =>
  http.put(`/admin/user/${id}/status`, { status });

export const userDelete = (id: number) =>
  http.delete(`/admin/user/${id}`);