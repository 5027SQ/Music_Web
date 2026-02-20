<template>
  <div class="admin-root">
    <el-container class="admin-container">
      <el-aside width="220px" class="admin-aside">
        <div class="logo">音乐管理后台</div>
        <el-menu
          class="admin-menu"
          :default-active="active"
          router
        >
          <el-menu-item index="/">
            仪表盘
          </el-menu-item>
          <el-menu-item index="/artists">
            歌手管理
          </el-menu-item>
          <el-menu-item index="/songs">
            歌曲管理
          </el-menu-item>
          <el-menu-item index="/users">
            用户管理
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="admin-header">
          <div class="admin-title">Admin</div>
          <el-button type="danger" plain size="small" @click="logout">
            退出登录
          </el-button>
        </el-header>
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { logoutApi } from '../api/auth';
import { useRoute } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();
const active = computed(() => route.path);

const logout = async () => {
  try {
    await logoutApi();
  } finally {
    localStorage.removeItem('token');
    location.href = '/login';
  }
};
</script>

<style scoped>
.admin-root {
  min-height: 100vh;
  background: #f6f1e7;
}
.admin-container {
  min-height: 100vh;
}
.admin-aside {
  background: #fbf7ef;
  border-right: 1px solid #eee2cf;
}
.logo {
  padding: 18px 16px;
  font-weight: 700;
  color: #6b4f2a;
}
.admin-menu {
  border-right: none;
  background: transparent;
}
.admin-header {
  background: #fffaf1;
  border-bottom: 1px solid #eee2cf;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.admin-title {
  font-size: 14px;
  color: #8c6b3f;
}
.admin-main {
  padding: 20px;
}
</style>