<template>
  <el-card shadow="hover" class="page-card">
    <div class="page-header">
      <h2>歌手管理</h2>
      <el-button type="primary" @click="showForm = true">新增歌手</el-button>
    </div>

    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="intro" label="简介" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showForm" title="新增歌手" width="420px">
      <el-form label-width="80px">
        <el-form-item label="歌手名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="form.avatarUrl" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.intro" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="create">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { artistPage, artistCreate, artistDelete } from '../api/artist';
import { ElMessage } from 'element-plus';

const list = ref<any[]>([]);
const showForm = ref(false);
const form = ref({ name: '', avatarUrl: '', intro: '' });

const getRecords = (res: any) =>
  res?.data?.records ?? res?.records ?? res?.data?.data?.records ?? [];

const load = async () => {
  const res = await artistPage({ page: 1, size: 50 });
  list.value = getRecords(res);
};

const create = async () => {
  await artistCreate(form.value);
  ElMessage.success('新增成功');
  showForm.value = false;
  form.value = { name: '', avatarUrl: '', intro: '' };
  load();
};

const del = async (id: number) => {
  await artistDelete(id);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>