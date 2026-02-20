import http from './http';

export const artistPage = (params: { page: number; size: number }) =>
  http.get('/artist/page', { params });

export const artistCreate = (payload: any) => http.post('/artist/create', payload);
export const artistUpdate = (id: number, payload: any) => http.put(`/artist/${id}`, payload);
export const artistDelete = (id: number) => http.delete(`/artist/${id}`);
export const artistSongs = (id: number, params: { page: number; size: number }) =>
  http.get(`/artist/${id}/songs`, { params });