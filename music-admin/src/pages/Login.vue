<template>
  <div class="login-root">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">管理端登录</h2>
      <el-form @submit.prevent>
        <el-form-item>
          <el-input v-model="username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-button type="primary" class="w-full" @click="onLogin">登录</el-button>
        <el-alert v-if="error" :title="error" type="error" class="mt-3" />
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { loginApi, meApi } from '../api/auth';
import { ElMessage } from 'element-plus';

const username = ref('');
const password = ref('');
const error = ref('');

const onLogin = async () => {
  error.value = '';
  try {
    const res = await loginApi({ username: username.value, password: password.value });
    localStorage.setItem('token', res.data.token);

    const me = await meApi();
    if (me.data.role !== 'ROLE_ADMIN') {
      localStorage.removeItem('token');
      error.value = '仅管理员允许登录后台';
      return;
    }

    ElMessage.success('登录成功');
    location.href = '/';
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || '登录失败';
  }
};
</script>

<style scoped>
.login-root {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f6f1e7;
}
.login-card {
  width: 380px;
  border-radius: 12px;
  background: #fffaf1;
}
.login-title {
  margin-bottom: 16px;
  color: #6b4f2a;
}
.w-full {
  width: 100%;
}
.mt-3 {
  margin-top: 12px;
}
</style>