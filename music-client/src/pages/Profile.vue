<template>
  <div>
    <h2>个人中心</h2>
    <div v-if="user">
      <el-descriptions title="用户信息" :column="1">
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
        <!-- 加入更多信息 -->
      </el-descriptions>
      <el-button type="danger" @click="logout" style="margin-top:24px;">退出登录</el-button>
    </div>
    <div v-else>
      <el-result icon="info" title="未登录" sub-title="请先登录以查看个人中心">
        <template #extra>
          <el-button type="primary" @click="$router.push('/login')">去登录</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()
const user = ref<any>(null)

onMounted(async () => {
  try {
    const res = await request.get('/user/me')
    user.value = res.data
  } catch (e) {
    user.value = null
  }
})

async function logout() {
  await request.post('/user/logout')
  // 清 token（比如本地 storage），再跳首页/登录
  localStorage.removeItem('token')
  router.replace('/login')
}
</script>