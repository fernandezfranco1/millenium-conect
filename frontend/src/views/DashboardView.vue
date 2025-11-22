<template>
  <div class="min-h-screen bg-gray-100">
    <nav class="bg-blue-600 text-white p-4 shadow-lg">
      <div class="container mx-auto flex justify-between items-center">
        <h1 class="text-2xl font-bold">Millenium Conect</h1>
        <div class="flex items-center space-x-4">
          <span>{{ authStore.username }}</span>
          <button
            @click="handleLogout"
            class="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition"
          >
            Cerrar Sesión
          </button>
        </div>
      </div>
    </nav>
    
    <div class="container mx-auto p-6">
      <div class="mb-6">
        <h2 class="text-3xl font-bold text-gray-800 mb-2">Panel de Control</h2>
        <p class="text-gray-600">Bienvenido al sistema de gestión de la escuela de taekwondo</p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <router-link
          to="/alumnos"
          class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition"
        >
          <div class="text-blue-600 text-4xl mb-4">👨‍🎓</div>
          <h3 class="text-xl font-bold mb-2">Alumnos</h3>
          <p class="text-gray-600">Gestionar información de alumnos</p>
        </router-link>
        
        <router-link
          to="/asistencias"
          class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition"
        >
          <div class="text-green-600 text-4xl mb-4">✓</div>
          <h3 class="text-xl font-bold mb-2">Asistencias</h3>
          <p class="text-gray-600">Registrar asistencias a clases</p>
        </router-link>
        
        <router-link
          to="/cuotas"
          class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition"
        >
          <div class="text-yellow-600 text-4xl mb-4">💰</div>
          <h3 class="text-xl font-bold mb-2">Cuotas</h3>
          <p class="text-gray-600">Administrar pagos de cuotas</p>
        </router-link>
        
        <router-link
          to="/productos"
          class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition"
        >
          <div class="text-purple-600 text-4xl mb-4">📦</div>
          <h3 class="text-xl font-bold mb-2">Productos</h3>
          <p class="text-gray-600">Gestionar inventario de productos</p>
        </router-link>
        
        <router-link
          to="/ventas"
          class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition"
        >
          <div class="text-red-600 text-4xl mb-4">🛒</div>
          <h3 class="text-xl font-bold mb-2">Ventas</h3>
          <p class="text-gray-600">Registrar ventas de productos</p>
        </router-link>
        
        <router-link
          to="/eventos"
          class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition"
        >
          <div class="text-indigo-600 text-4xl mb-4">📅</div>
          <h3 class="text-xl font-bold mb-2">Eventos</h3>
          <p class="text-gray-600">Gestionar eventos y torneos</p>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>
