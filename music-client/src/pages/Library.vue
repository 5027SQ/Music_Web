<template>
  <div>
    <el-input v-model="keyword" placeholder="搜索歌名/歌手" style="width: 300px;" @change="onSearch" clearable/>
    <el-table :data="songs" style="margin-top: 20px" v-loading="loading">
      <el-table-column prop="name" label="歌名" />
      <el-table-column prop="artistName" label="歌手" />
      <el-table-column prop="playCount" label="播放次数" />
      <!-- 你有更多字段可以继续加 -->
    </el-table>
    <el-pagination
      v-if="total>0"
      :current-page="page"
      :page-size="pageSize"
      :total="total"
      @current-change="changePage"
      background
      layout="prev, pager, next"
      style="margin-top: 16px"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { fetchSongList } from '../api/song'

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const songs = ref<any[]>([])
const loading = ref(false)
const keyword = ref('')

const getData = async () => {
  loading.value = true
  try {
    const params:any = { page: page.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    const res = await fetchSongList(params)
    songs.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const changePage = (p:number) => {
  page.value = p
  getData()
}

const onSearch = () => {
  page.value = 1
  getData()
}

onMounted(getData)
</script>