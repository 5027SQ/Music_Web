<template>
  <div>
    <h2 style="margin-top: 0;">🎼 热门歌曲</h2>
    <el-table :data="hotSongs" style="margin-bottom: 32px;">
      <el-table-column prop="title" label="歌名" />
      <el-table-column prop="artist" label="歌手" />
      <el-table-column prop="playCount" label="播放次数" />
    </el-table>
    <h2>🌟 推荐歌手</h2>
    <el-row :gutter="16">
      <el-col v-for="item in hotArtists" :key="item.id" :span="6">
        <el-card :body-style="{padding:'14px'}">
          <div style="font-weight: bold;">{{ item.name }}</div>
          <div style="color:#888;">{{ item.intro }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../api/request'

const hotSongs = ref([])
const hotArtists = ref([])

onMounted(async () => {
  // 假设有 /song/hot 和 /artist/page（取前几条即可）
  const res1 = await request.get('/song/hot')
  hotSongs.value = res1.data?.records || res1.data || []
  const res2 = await request.get('/artist/page', { params: { page: 1, pageSize: 4 } })
  hotArtists.value = res2.data?.records || res2.data || []
})
</script>