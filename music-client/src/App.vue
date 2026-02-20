<template>
  <el-container style="min-height:100vh;">
    <el-aside width="200px">
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/library">歌曲库</el-menu-item>
        <el-menu-item index="/favorites">我的收藏</el-menu-item>
        <el-menu-item index="/profile">个人中心</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>🎵 Vibe 音乐客户端</el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
// 此处可暂时不用任何逻辑
</script>