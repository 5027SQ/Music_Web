<template>
  <div>
    <el-input v-model="keyword" placeholder="搜索歌名/歌手" style="width: 300px;" @change="onSearch" clearable/>
    <el-table :data="songs" style="margin-top: 20px" v-loading="loading">
      <el-table-column prop="title" label="歌名" />
      <el-table-column prop="artist" label="歌手" />
      <el-table-column label="操作">
            <template #default="scope">
              <el-button @click="play(scope.row)">播放</el-button>
            </template>
          </el-table-column>
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
import { usePlayerStore } from '../store/player'

const player = usePlayerStore()
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const songs = ref<any[]>([])
const loading = ref(false)
const keyword = ref('')

onMounted(() => {
  console.log('Library mounted')
  getData()
})
const getData = async () => {
  console.log('Requesting song list')
  const params:any = { page: page.value, pageSize: pageSize.value }
  console.log('Request params:', params)
  const res = await fetchSongList(params)
  console.log('Response:', res)
  loading.value = true
  try {
    const params:any = { page: page.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    function getRecords(res: any) {
      return res?.data?.records ?? res?.records ?? []
    }
    function getTotal(res: any) {
      return res?.data?.total ?? res?.total ?? 0
    }
    const res = await fetchSongList(params)
    songs.value = getRecords(res)
    total.value = getTotal(res)
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

function play(song: any) {
  player.play(song)
}

onMounted(getData)
</script>