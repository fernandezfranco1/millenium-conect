<template>
  <div class="min-h-screen bg-gray-100">
    <nav class="bg-blue-600 text-white p-4 shadow-lg">
      <div class="container mx-auto flex justify-between items-center">
        <router-link to="/" class="text-2xl font-bold">Millenium Conect</router-link>
        <button
          @click="handleLogout"
          class="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition"
        >
          Cerrar Sesión
        </button>
      </div>
    </nav>
    
    <div class="container mx-auto p-6">
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-3xl font-bold text-gray-800">Gestión de Alumnos</h2>
        <button
          @click="showModal = true; editingAlumno = null; resetForm()"
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded transition"
        >
          + Nuevo Alumno
        </button>
      </div>
      
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <input
          v-model="searchTerm"
          @input="buscarAlumnos"
          type="text"
          placeholder="Buscar alumno por nombre o apellido..."
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Nombre</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">DNI</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Teléfono</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Categoría</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Edad</th>
              <th class="px-6 py-3 text-center text-gray-700 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="alumno in alumnos" :key="alumno.idAlumno" class="border-b hover:bg-gray-50">
              <td class="px-6 py-4">{{ alumno.nombre }} {{ alumno.apellido }}</td>
              <td class="px-6 py-4">{{ alumno.dni }}</td>
              <td class="px-6 py-4">{{ alumno.telefono }}</td>
              <td class="px-6 py-4">{{ alumno.categoria }}</td>
              <td class="px-6 py-4">{{ alumno.edad }}</td>
              <td class="px-6 py-4 text-center">
                <button
                  @click="editAlumno(alumno)"
                  class="text-blue-600 hover:text-blue-800 mr-3"
                >
                  Editar
                </button>
                <button
                  @click="deleteAlumno(alumno.idAlumno)"
                  class="text-red-600 hover:text-red-800"
                >
                  Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- Paginación -->
        <div class="bg-gray-50 px-6 py-4 flex items-center justify-between border-t">
          <div class="text-sm text-gray-700">
            Mostrando {{ (pagination.currentPage * pagination.size) + 1 }} a 
            {{ Math.min((pagination.currentPage + 1) * pagination.size, pagination.totalItems) }} 
            de {{ pagination.totalItems }} alumnos
          </div>
          
          <div class="flex items-center space-x-2">
            <button
              @click="changePage(pagination.currentPage - 1)"
              :disabled="!pagination.hasPrevious"
              class="px-4 py-2 border rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Anterior
            </button>
            
            <div class="flex space-x-1">
              <button
                v-for="page in displayPages"
                :key="page"
                @click="changePage(page)"
                :class="page === pagination.currentPage ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-100'"
                class="px-4 py-2 border rounded-lg"
              >
                {{ page + 1 }}
              </button>
            </div>
            
            <button
              @click="changePage(pagination.currentPage + 1)"
              :disabled="!pagination.hasNext"
              class="px-4 py-2 border rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Siguiente
            </button>
          </div>
          
          <div class="flex items-center space-x-2">
            <label class="text-sm text-gray-700">Por página:</label>
            <select
              v-model="pagination.size"
              @change="changePageSize"
              class="border rounded-lg px-3 py-2"
            >
              <option :value="5">5</option>
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="50">50</option>
            </select>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-lg p-6 w-full max-w-2xl">
        <h3 class="text-2xl font-bold mb-4">
          {{ editingAlumno ? 'Editar Alumno' : 'Nuevo Alumno' }}
        </h3>
        
        <form @submit.prevent="saveAlumno" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-gray-700 mb-2">Nombre</label>
              <input 
                v-model="form.nombre" 
                type="text" 
                required 
                @input="validateTextOnly($event, 'nombre')"
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="Solo letras"
              />
            </div>
            <div>
              <label class="block text-gray-700 mb-2">Apellido</label>
              <input 
                v-model="form.apellido" 
                type="text" 
                required 
                @input="validateTextOnly($event, 'apellido')"
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="Solo letras"
              />
            </div>
            <div>
              <label class="block text-gray-700 mb-2">DNI</label>
              <input 
                v-model="form.dni" 
                type="text" 
                required 
                @input="validateNumbersOnly($event, 'dni')"
                maxlength="10"
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="Solo números"
              />
            </div>
            <div>
              <label class="block text-gray-700 mb-2">Teléfono</label>
              <input 
                v-model="form.telefono" 
                type="text" 
                @input="validateNumbersOnly($event, 'telefono')"
                maxlength="15"
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="Solo números"
              />
            </div>
            <div>
              <label class="block text-gray-700 mb-2">Peso (Kg)</label>
              <div class="relative">
                <input 
                  v-model="form.peso" 
                  type="number" 
                  step="0.1" 
                  min="0"
                  class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 pr-12" 
                  placeholder="0.0"
                />
                <span class="absolute right-3 top-2.5 text-gray-500 font-medium">Kg</span>
              </div>
            </div>
            <div>
              <label class="block text-gray-700 mb-2">Categoría</label>
              <select 
                v-model="form.categoria" 
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Selecciona una categoría</option>
                <option value="Blanco">Blanco</option>
                <option value="Blanco punta amarilla">Blanco punta amarilla</option>
                <option value="Amarillo">Amarillo</option>
                <option value="Amarillo punta verde">Amarillo punta verde</option>
                <option value="Verde">Verde</option>
                <option value="Verde punta azul">Verde punta azul</option>
                <option value="Azul">Azul</option>
                <option value="Azul punta roja">Azul punta roja</option>
                <option value="Rojo">Rojo</option>
                <option value="Rojo punta negra">Rojo punta negra</option>
                <option value="Negra">Negra</option>
                <option value="Negro primer dan">Negro primer dan</option>
                <option value="Negro segundo dan">Negro segundo dan</option>
                <option value="Negro tercer dan">Negro tercer dan</option>
                <option value="Negro cuarto dan">Negro cuarto dan</option>
                <option value="Negro quinto dan">Negro quinto dan</option>
                <option value="Negro sexto dan">Negro sexto dan</option>
                <option value="Negro séptimo dan">Negro séptimo dan</option>
              </select>
            </div>
            <div>
              <label class="block text-gray-700 mb-2">Género</label>
              <select 
                v-model="form.genero" 
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="Masculino">Masculino</option>
                <option value="Femenino">Femenino</option>
              </select>
            </div>
            <div>
              <label class="block text-gray-700 mb-2">Fecha de Nacimiento</label>
              <input 
                v-model="form.fechaNacimiento" 
                type="date" 
                required 
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
              />
            </div>
          </div>
          
          <div class="flex justify-end space-x-4 mt-6">
            <button
              type="button"
              @click="showModal = false"
              class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-100"
            >
              Cancelar
            </button>
            <button
              type="submit"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              Guardar
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import alumnoService from '@/services/alumnoService'

const router = useRouter()
const authStore = useAuthStore()

const alumnos = ref([])
const showModal = ref(false)
const editingAlumno = ref(null)
const searchTerm = ref('')

const pagination = ref({
  currentPage: 0,
  size: 10,
  totalItems: 0,
  totalPages: 0,
  hasNext: false,
  hasPrevious: false
})

const form = ref({
  nombre: '',
  apellido: '',
  dni: '',
  telefono: '',
  peso: null,
  categoria: '',
  genero: 'Masculino',
  fechaNacimiento: ''
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const resetForm = () => {
  form.value = {
    nombre: '',
    apellido: '',
    dni: '',
    telefono: '',
    peso: null,
    categoria: '',
    genero: 'Masculino',
    fechaNacimiento: ''
  }
}

const loadAlumnos = async () => {
  try {
    const response = await alumnoService.getAll(
      pagination.value.currentPage,
      pagination.value.size,
      'nombre',
      'asc'
    )
    alumnos.value = response.content
    pagination.value.totalItems = response.totalItems
    pagination.value.totalPages = response.totalPages
    pagination.value.hasNext = response.hasNext
    pagination.value.hasPrevious = response.hasPrevious
  } catch (error) {
    console.error('Error al cargar alumnos:', error)
  }
}

const changePage = (page) => {
  if (page >= 0 && page < pagination.value.totalPages) {
    pagination.value.currentPage = page
    loadAlumnos()
  }
}

const changePageSize = () => {
  pagination.value.currentPage = 0
  loadAlumnos()
}

const displayPages = computed(() => {
  const pages = []
  const total = pagination.value.totalPages
  const current = pagination.value.currentPage
  
  let start = Math.max(0, current - 2)
  let end = Math.min(total - 1, current + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const buscarAlumnos = async () => {
  if (searchTerm.value.trim()) {
    try {
      alumnos.value = await alumnoService.buscar(searchTerm.value)
    } catch (error) {
      console.error('Error al buscar alumnos:', error)
    }
  } else {
    loadAlumnos()
  }
}

const editAlumno = (alumno) => {
  editingAlumno.value = alumno
  form.value = { ...alumno }
  showModal.value = true
}

const saveAlumno = async () => {
  try {
    if (editingAlumno.value) {
      await alumnoService.update(editingAlumno.value.idAlumno, form.value)
    } else {
      await alumnoService.create(form.value)
    }
    showModal.value = false
    loadAlumnos()
  } catch (error) {
    console.error('Error al guardar alumno:', error)
    alert('Error al guardar el alumno')
  }
}

const deleteAlumno = async (id) => {
  if (confirm('¿Estás seguro de eliminar este alumno?')) {
    try {
      await alumnoService.delete(id)
      loadAlumnos()
    } catch (error) {
      console.error('Error al eliminar alumno:', error)
      alert('Error al eliminar el alumno')
    }
  }
}

const validateTextOnly = (event, field) => {
  // Eliminar números y símbolos, solo permite letras y espacios
  form.value[field] = event.target.value.replace(/[^a-záéíóúñA-ZÁÉÍÓÚÑ\s]/g, '')
}

const validateNumbersOnly = (event, field) => {
  // Solo permite números
  form.value[field] = event.target.value.replace(/[^0-9]/g, '')
}

onMounted(() => {
  loadAlumnos()
})
</script>
