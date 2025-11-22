<template>
  <div class="min-h-screen bg-gray-100">
    <nav class="bg-blue-600 text-white p-4 shadow-lg">
      <div class="container mx-auto flex justify-between items-center">
        <router-link to="/" class="text-2xl font-bold">Millenium Conect</router-link>
        <button @click="handleLogout" class="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition">
          Cerrar Sesión
        </button>
      </div>
    </nav>
    
    <div class="container mx-auto p-6">
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-3xl font-bold text-gray-800">Gestión de Ventas</h2>
        <button
          @click="showModal = true"
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded transition"
        >
          + Nueva Venta
        </button>
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Producto</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Cantidad</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Total</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Fecha</th>
              <th class="px-6 py-3 text-center text-gray-700 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="venta in ventas" :key="venta.idVenta" class="border-b hover:bg-gray-50">
              <td class="px-6 py-4">{{ venta.producto?.nombre }}</td>
              <td class="px-6 py-4">{{ venta.cantidad }}</td>
              <td class="px-6 py-4">${{ venta.total }}</td>
              <td class="px-6 py-4">{{ venta.fechaVenta }}</td>
              <td class="px-6 py-4 text-center">
                <button @click="deleteVenta(venta.idVenta)" class="text-red-600 hover:text-red-800">
                  Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 class="text-2xl font-bold mb-4">Nueva Venta</h3>
        <p class="text-gray-600 mb-4">Formulario de venta (por implementar completamente)</p>
        <button @click="showModal = false" class="w-full bg-gray-300 hover:bg-gray-400 px-4 py-2 rounded">
          Cerrar
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ventaService from '@/services/ventaService'

const router = useRouter()
const authStore = useAuthStore()
const ventas = ref([])
const showModal = ref(false)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const loadVentas = async () => {
  try {
    ventas.value = await ventaService.getAll()
  } catch (error) {
    console.error('Error al cargar ventas:', error)
  }
}

const deleteVenta = async (id) => {
  if (confirm('¿Estás seguro de eliminar esta venta?')) {
    try {
      await ventaService.delete(id)
      loadVentas()
    } catch (error) {
      console.error('Error al eliminar venta:', error)
    }
  }
}

onMounted(() => {
  loadVentas()
})
</script>
