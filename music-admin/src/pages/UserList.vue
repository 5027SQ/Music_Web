<template>
  <el-card shadow="hover" class="page-card">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="toggle(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { userPage, userUpdateStatus, userDelete } from '../api/user';
import { ElMessage } from 'element-plus';

const list = ref<any[]>([]);

const getRecords = (res: any) =>
  res?.data?.records ?? res?.records ?? res?.data?.data?.records ?? [];

const load = async () => {
  const res = await userPage({ page: 1, size: 50 });
  list.value = getRecords(res);
};

const toggle = async (u: any) => {
  const next = u.status === 1 ? 0 : 1;
  await userUpdateStatus(u.id, next);
  ElMessage.success('操作成功');
  load();
};

const del = async (id: number) => {
  await userDelete(id);
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