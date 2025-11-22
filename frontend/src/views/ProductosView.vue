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
        <h2 class="text-3xl font-bold text-gray-800">Gestión de Productos</h2>
        <button
          @click="showModal = true"
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded transition"
        >
          + Nuevo Producto
        </button>
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Nombre</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Marca</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Color</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Stock</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Precio</th>
              <th class="px-6 py-3 text-center text-gray-700 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="producto in productos" :key="producto.idProducto" class="border-b hover:bg-gray-50">
              <td class="px-6 py-4">{{ producto.nombre }}</td>
              <td class="px-6 py-4">{{ producto.marca }}</td>
              <td class="px-6 py-4">{{ producto.color }}</td>
              <td class="px-6 py-4">{{ producto.stock }}</td>
              <td class="px-6 py-4">${{ producto.precioVenta }}</td>
              <td class="px-6 py-4 text-center">
                <button @click="deleteProducto(producto.idProducto)" class="text-red-600 hover:text-red-800">
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
        <h3 class="text-2xl font-bold mb-4">Nuevo Producto</h3>
        <p class="text-gray-600 mb-4">Formulario de producto (por implementar completamente)</p>
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
import productoService from '@/services/productoService'

const router = useRouter()
const authStore = useAuthStore()
const productos = ref([])
const showModal = ref(false)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const loadProductos = async () => {
  try {
    productos.value = await productoService.getAll()
  } catch (error) {
    console.error('Error al cargar productos:', error)
  }
}

const deleteProducto = async (id) => {
  if (confirm('¿Estás seguro de eliminar este producto?')) {
    try {
      await productoService.delete(id)
      loadProductos()
    } catch (error) {
      console.error('Error al eliminar producto:', error)
    }
  }
}

onMounted(() => {
  loadProductos()
})
</script>
