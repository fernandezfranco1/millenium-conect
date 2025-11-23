<template>
  <a-layout class="min-h-screen">
    <a-layout-header class="bg-blue-600 flex items-center justify-between px-6">
      <div class="flex items-center space-x-4">
        <router-link to="/" class="flex items-center space-x-2 text-2xl font-bold text-white hover:text-gray-200">
          <img src="/logo.png" alt="Logo" class="w-10 h-10 object-contain rounded-lg p-1" />
          <span>Millenium Conect</span>
        </router-link>
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          theme="dark"
          style="background-color: transparent; border: none; line-height: 64px;"
          @click="handleMenuClick"
        >
          <a-menu-item key="alumnos">Alumnos</a-menu-item>
          <a-menu-item key="clases">Clases</a-menu-item>
          <a-menu-item key="asistencias">Asistencias</a-menu-item>
          <a-menu-item key="cuotas">Cuotas</a-menu-item>
          <a-menu-item key="productos">Productos</a-menu-item>
          <a-menu-item key="ventas">Ventas</a-menu-item>
          <a-menu-item key="eventos">Eventos</a-menu-item>
          <a-menu-item key="torneo">Torneo</a-menu-item>
        </a-menu>
      </div>
      <a-dropdown>
        <a-space class="cursor-pointer">
          <a-avatar style="background-color: #1890ff">
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <span class="text-white">{{ authStore.username }}</span>
        </a-space>
        <template #overlay>
          <a-menu>
            <a-menu-item key="logout" @click="handleLogout">
              <LogoutOutlined />
              <span class="ml-2">Cerrar Sesión</span>
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </a-layout-header>
    
    <a-layout-content class="bg-gray-100">
      <slot />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const selectedKeys = ref([route.name?.toLowerCase() || 'dashboard'])

watch(() => route.name, (newName) => {
  if (newName) {
    selectedKeys.value = [newName.toLowerCase()]
  }
})

const handleMenuClick = ({ key }) => {
  router.push(`/${key}`)
}

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
