import http from './http';

export const songPage = (params: { page: number; size: number; keyword?: string }) =>
  http.get('/song/page', { params });

export const songCreate = (payload: any) => http.post('/song/create', payload);
export const songUpdate = (id: number, payload: any) => http.put(`/song/${id}`, payload);
export const songDelete = (id: number) => http.delete(`/song/${id}`);

export const uploadAudio = (file: File) => {
  const form = new FormData();
  form.append('file', file);
  return http.post('/song/upload/audio', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};