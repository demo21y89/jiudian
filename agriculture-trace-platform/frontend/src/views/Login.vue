<template>
  <div style="min-height:100vh;display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,#e8f5e9,#c8e6c9);">
    <el-card style="width:400px;padding:20px;">
      <h2 style="text-align:center;margin-bottom:24px;color:#1a6b3c;">
        <el-icon :size="24"><GraduationCap /></el-icon> 农溯AI · 登录
      </h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center;color:#999;font-size:13px;">
        还没有账号？<router-link to="/register" style="color:#409eff;">立即注册</router-link>
      </div>
      <div style="margin-top:12px;padding:12px;background:#f5f7fa;border-radius:8px;font-size:12px;color:#666;">
        <div><b>测试账号：</b></div>
        <div>管理员：admin / admin123</div>
        <div>消费者：user / user123</div>
        <div>生产者：producer / prod123</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { authApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await authApi.login(form)
    if (res.code === 200) {
      userStore.setUser(res.data)
      ElMessage.success('登录成功')
      router.push('/products')
    } else {
      ElMessage.error(res.msg)
    }
  } finally {
    loading.value = false
  }
}
</script>
