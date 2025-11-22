<template>
  <a-layout class="min-h-screen">
    <a-layout-header class="bg-blue-600 flex items-center justify-between px-6">
      <div class="flex items-center space-x-4">
        <router-link to="/" class="text-2xl font-bold text-white hover:text-gray-200">
          Millenium Conect
        </router-link>
      </div>
      <a-space>
        <a-avatar style="background-color: #1890ff">
          <template #icon><UserOutlined /></template>
        </a-avatar>
        <span class="text-white">{{ authStore.username }}</span>
        <a-button type="primary" danger @click="handleLogout">
          <template #icon><LogoutOutlined /></template>
          Cerrar Sesión
        </a-button>
      </a-space>
    </a-layout-header>
    
    <a-layout-content class="bg-gray-100">
      <slot />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.ant-layout-header {
  line-height: 64px;
  height: 64px;
}
</style>
