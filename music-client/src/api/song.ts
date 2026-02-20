import request from './request'

// 获取歌曲列表
export function fetchSongList(params: { page: number, pageSize: number }) {
  return request.get('/song/page', { params })
}