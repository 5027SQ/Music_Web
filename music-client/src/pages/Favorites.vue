<template>
  <div>
    <h2>我的收藏</h2>
    <el-table :data="favSongs" v-loading="loading">
      <el-table-column prop="title" label="歌名" />
      <el-table-column prop="artist" label="歌手" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="danger" @click="unfav(scope.row.id)">取消收藏</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'
import { usePlayerStore } from '../store/player'

const player = usePlayerStore()
const router = useRouter()
const favSongs = ref([])
const loading = ref(false)

function play(song: any) {
  player.play(song)
}

async function loadFavorites() {
  loading.value = true
  try {
    const res = await request.get('/song/star/page', { params: { page: 1, pageSize: 100 } })
    favSongs.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

async function unfav(id: number) {
  await request.post(`/song/unstar/${id}`)
  await loadFavorites()
}

onMounted(async () => {
  // 检查登录逻辑可加在这里
  await loadFavorites()
})
</script>