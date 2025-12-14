<template>
  <a-layout class="min-h-screen">
    <a-layout-header class="bg-blue-600">
      <div class="container mx-auto flex justify-between items-center">
        <div class="flex items-center space-x-2">
          <img src="/logo.png" alt="Logo" class="w-12 h-12 object-contain rounded-lg p-1" />
          <h1 class="text-2xl font-bold text-white">Millenium Conect</h1>
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
      </div>
    </a-layout-header>
    
    <a-layout-content class="tatami-background p-6 overflow-auto">
      <div class="container mx-auto max-w-screen-xl lg:max-w-[80vw]">
        
        <a-row :gutter="[16, 16]" class="dashboard-grid">
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/alumnos')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-blue-500 to-blue-600">
                <UserOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Alumnos</h3>
                <p class="card-description">Gestionar información de alumnos</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/clases')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-teal-500 to-teal-600">
                <TeamOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Clases</h3>
                <p class="card-description">Administrar clases y horarios</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/asistencias')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-green-500 to-green-600">
                <CheckCircleOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Asistencias</h3>
                <p class="card-description">Registrar asistencias a clases</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/cuotas')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-yellow-500 to-orange-500">
                <DollarOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Cuotas</h3>
                <p class="card-description">Administrar pagos de cuotas</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/productos')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-purple-500 to-purple-600">
                <ShoppingOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Productos</h3>
                <p class="card-description">Gestionar inventario de productos</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/ventas')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-red-500 to-pink-600">
                <ShoppingCartOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Ventas</h3>
                <p class="card-description">Registrar ventas de productos</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/eventos')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-indigo-500 to-indigo-600">
                <CalendarOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Eventos</h3>
                <p class="card-description">Gestionar eventos y torneos</p>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="8">
            <a-card hoverable @click="router.push('/torneo')" class="module-card">
              <div class="card-icon-wrapper bg-gradient-to-br from-amber-500 to-orange-600">
                <TrophyOutlined class="card-icon" />
              </div>
              <div class="card-content">
                <h3 class="card-title">Generador de Torneo</h3>
                <p class="card-description">Crear llaves de competencia</p>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  UserOutlined,
  LogoutOutlined,
  CheckCircleOutlined,
  DollarOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  CalendarOutlined,
  TeamOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.ant-layout-header {
  padding: 0 24px;
  line-height: 64px;
  height: 64px;
}

.module-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  height: 100%;
  min-height: 140px;
}

.module-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  border-radius: 12px 12px 0 0;
  margin: -24px -24px 10px -24px;
}

.card-icon {
  font-size: 32px;
  color: white;
}

.card-content {
  text-align: center;
  padding: 0 8px 0;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 2px;
}

.card-description {
  font-size: 11px;
  color: #8c8c8c;
  margin: 0;
}

:deep(.ant-card-body) {
  padding: 16px;
}

.dashboard-grid {
  max-height: calc(100vh - 100px);
}

.tatami-background {
  background: 
    linear-gradient(135deg, rgba(37, 99, 235, 0.03) 0%, transparent 50%),
    linear-gradient(225deg, rgba(220, 38, 38, 0.03) 0%, transparent 50%),
    linear-gradient(45deg, rgba(15, 23, 42, 0.02) 0%, transparent 50%),
    radial-gradient(circle at 20% 80%, rgba(37, 99, 235, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(220, 38, 38, 0.05) 0%, transparent 50%),
    linear-gradient(to bottom, #f8f9fa, #e9ecef);
  background-size: 100% 100%;
  min-height: calc(100vh - 64px);
  position: relative;
}
</style>
