<template>
  <el-card shadow="hover" class="page-card">
    <div class="page-header">
      <h2>歌曲管理</h2>
    </div>

    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="artist" label="歌手" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { songPage, songDelete } from '../api/song';
import { ElMessage } from 'element-plus';

const list = ref<any[]>([]);

const getRecords = (res: any) =>
  res?.data?.records ?? res?.records ?? res?.data?.data?.records ?? [];

const load = async () => {
  const res = await songPage({ page: 1, size: 50 });
  list.value = getRecords(res);
};

const del = async (id: number) => {
  await songDelete(id);
  ElMessage.success('删除成功');
  load();
};

onMounted(load);
</script>

<style scoped>
.page-card {
  background: #fffaf1;
  border-radius: 12px;
}
.page-header {
  margin-bottom: 16px;
  color: #6b4f2a;
}
</style>