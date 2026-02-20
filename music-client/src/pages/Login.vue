<!-- src/pages/Login.vue -->
<template>
  <el-form :model="form" @submit.prevent="onSubmit" style="width: 300px; margin: 80px auto;">
    <el-form-item label="用户名">
      <el-input v-model="form.username" placeholder="用户名" />
    </el-form-item>
    <el-form-item label="密码">
      <el-input v-model="form.password" type="password" placeholder="密码" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit" style="width:100%;">登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'

const router = useRouter()
const form = reactive({
  username: '',
  password: ''
})

const onSubmit = async () => {
  try {
    await login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    ElMessage.error('登录失败，请检查用户名或密码')
  }
}
</script>