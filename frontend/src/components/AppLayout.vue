<template>
  <a-layout class="min-h-screen">
    <a-layout-header class="bg-blue-600 flex items-center justify-between px-3 sm:px-6">
      <div class="flex items-center space-x-2 sm:space-x-4 overflow-x-auto">
        <router-link to="/" class="flex items-center space-x-2 text-lg sm:text-2xl font-bold text-white hover:text-gray-200 whitespace-nowrap">
          <img src="/logo.png" alt="Logo" class="w-8 h-8 sm:w-10 sm:h-10 object-contain rounded-lg p-1" />
          <span class="hidden md:inline">Millenium Conect</span>
        </router-link>
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          theme="dark"
          style="background-color: transparent; border: none; line-height: 64px;"
          @click="handleMenuClick"
          class="hidden lg:flex"
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
      
      <!-- Menú móvil (dropdown) -->
      <div class="flex items-center space-x-2">
        <a-dropdown class="lg:hidden">
          <a-button type="primary">
            <template #icon><MenuOutlined /></template>
          </a-button>
          <template #overlay>
            <a-menu @click="handleMenuClick">
              <a-menu-item key="alumnos">Alumnos</a-menu-item>
              <a-menu-item key="clases">Clases</a-menu-item>
              <a-menu-item key="asistencias">Asistencias</a-menu-item>
              <a-menu-item key="cuotas">Cuotas</a-menu-item>
              <a-menu-item key="productos">Productos</a-menu-item>
              <a-menu-item key="ventas">Ventas</a-menu-item>
              <a-menu-item key="eventos">Eventos</a-menu-item>
              <a-menu-item key="torneo">Torneo</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        
        <a-dropdown>
          <a-space class="cursor-pointer">
            <a-avatar style="background-color: #1890ff">
              <template #icon><UserOutlined /></template>
            </a-avatar>
            <span class="text-white hidden sm:inline">{{ authStore.username }}</span>
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
      </div>
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
import { UserOutlined, LogoutOutlined, MenuOutlined } from '@ant-design/icons-vue'

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
