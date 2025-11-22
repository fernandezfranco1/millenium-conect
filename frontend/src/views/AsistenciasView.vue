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
        <h2 class="text-3xl font-bold text-gray-800">Gestión de Asistencias</h2>
        <button
          @click="showModal = true; resetForm()"
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded transition"
        >
          + Nueva Asistencia
        </button>
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Alumno</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Estado</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Fecha</th>
              <th class="px-6 py-3 text-center text-gray-700 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="asistencia in asistencias" :key="asistencia.idAsistencia" class="border-b hover:bg-gray-50">
              <td class="px-6 py-4">{{ asistencia.alumno?.nombre }} {{ asistencia.alumno?.apellido }}</td>
              <td class="px-6 py-4">
                <span :class="getEstadoClass(asistencia.estado)" class="px-3 py-1 rounded-full text-sm">
                  {{ asistencia.estado }}
                </span>
              </td>
              <td class="px-6 py-4">{{ asistencia.fechaAsistencia }}</td>
              <td class="px-6 py-4 text-center">
                <button
                  @click="editAsistencia(asistencia)"
                  class="text-blue-600 hover:text-blue-800 mr-3"
                >
                  Editar
                </button>
                <button
                  @click="deleteAsistencia(asistencia.idAsistencia)"
                  class="text-red-600 hover:text-red-800"
                >
                  Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 class="text-2xl font-bold mb-4">
          {{ editingAsistencia ? 'Editar Asistencia' : 'Nueva Asistencia' }}
        </h3>
        
        <form @submit.prevent="saveAsistencia" class="space-y-4">
          <!-- Autocomplete Alumno -->
          <div>
            <label class="block text-gray-700 mb-2">Alumno *</label>
            <div class="relative">
              <input
                v-model="alumnoSearch"
                @input="searchAlumnos"
                @focus="alumnoSearch.length >= 2 && searchAlumnos()"
                type="text"
                required
                placeholder="Escribe al menos 2 letras para buscar..."
                class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              
              <!-- Indicador de búsqueda -->
              <div v-if="isSearching" class="absolute right-3 top-3">
                <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
              </div>
              
              <!-- Lista de alumnos -->
              <div
                v-if="showAlumnosList && !isSearching"
                class="absolute z-10 w-full mt-1 bg-white border rounded-lg shadow-lg max-h-60 overflow-y-auto"
              >
                <div v-if="alumnosEncontrados.length === 0" class="px-4 py-3 text-gray-500 text-center">
                  No se encontraron alumnos
                </div>
                <button
                  v-for="alumno in alumnosEncontrados"
                  :key="alumno.idAlumno"
                  @click.prevent="selectAlumno(alumno)"
                  type="button"
                  class="w-full px-4 py-2 text-left hover:bg-blue-50 border-b last:border-b-0"
                >
                  <div class="font-medium">{{ alumno.nombre }} {{ alumno.apellido }}</div>
                  <div class="text-sm text-gray-500">DNI: {{ alumno.dni }} | {{ alumno.categoria }}</div>
                </button>
              </div>
            </div>
            <div v-if="form.alumno" class="mt-2 text-sm text-green-600">
              ✓ Alumno seleccionado: {{ form.alumno.nombre }} {{ form.alumno.apellido }}
            </div>
          </div>
          
          <!-- Estado -->
          <div>
            <label class="block text-gray-700 mb-2">Estado *</label>
            <select
              v-model="form.estado"
              required
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="Presente">Presente</option>
              <option value="Ausente">Ausente</option>
              <option value="Tardanza">Tardanza</option>
            </select>
          </div>
          
          <!-- Fecha -->
          <div>
            <label class="block text-gray-700 mb-2">Fecha *</label>
            <input
              v-model="form.fechaAsistencia"
              type="date"
              required
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            {{ error }}
          </div>
          
          <div class="flex justify-end space-x-4 mt-6">
            <button
              type="button"
              @click="closeModal"
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
import asistenciaService from '@/services/asistenciaService'
import alumnoService from '@/services/alumnoService'

const router = useRouter()
const authStore = useAuthStore()
const asistencias = ref([])
const showModal = ref(false)
const editingAsistencia = ref(null)
const error = ref('')

// Autocomplete de alumnos
const alumnosEncontrados = ref([])
const alumnoSearch = ref('')
const showAlumnosList = ref(false)
const searchTimeout = ref(null)
const isSearching = ref(false)

const form = ref({
  alumno: null,
  estado: 'Presente',
  fechaAsistencia: new Date().toISOString().split('T')[0]
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const searchAlumnos = async () => {
  // Limpiar timeout anterior
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  // Si está vacío, limpiar resultados
  if (!alumnoSearch.value || alumnoSearch.value.length < 2) {
    alumnosEncontrados.value = []
    showAlumnosList.value = false
    return
  }
  
  // Mostrar el dropdown inmediatamente
  showAlumnosList.value = true
  
  // Debounce de 500ms
  searchTimeout.value = setTimeout(async () => {
    isSearching.value = true
    
    try {
      const response = await alumnoService.searchAlumnos(alumnoSearch.value, 0, 10)
      alumnosEncontrados.value = response.content
    } catch (error) {
      console.error('Error al buscar alumnos:', error)
      alumnosEncontrados.value = []
    } finally {
      isSearching.value = false
    }
  }, 500)
}

const selectAlumno = (alumno) => {
  form.value.alumno = alumno
  alumnoSearch.value = `${alumno.nombre} ${alumno.apellido}`
  showAlumnosList.value = false
}

const resetForm = () => {
  form.value = {
    alumno: null,
    estado: 'Presente',
    fechaAsistencia: new Date().toISOString().split('T')[0]
  }
  alumnoSearch.value = ''
  error.value = ''
  editingAsistencia.value = null
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const getEstadoClass = (estado) => {
  const classes = {
    'Presente': 'bg-green-100 text-green-800',
    'Ausente': 'bg-red-100 text-red-800',
    'Tardanza': 'bg-yellow-100 text-yellow-800'
  }
  return classes[estado] || 'bg-gray-100 text-gray-800'
}

const loadAsistencias = async () => {
  try {
    asistencias.value = await asistenciaService.getAll()
  } catch (error) {
    console.error('Error al cargar asistencias:', error)
  }
}



const editAsistencia = (asistencia) => {
  editingAsistencia.value = asistencia
  form.value = {
    alumno: asistencia.alumno,
    estado: asistencia.estado,
    fechaAsistencia: asistencia.fechaAsistencia
  }
  alumnoSearch.value = `${asistencia.alumno.nombre} ${asistencia.alumno.apellido}`
  showModal.value = true
}

const saveAsistencia = async () => {
  if (!form.value.alumno) {
    error.value = 'Debe seleccionar un alumno'
    return
  }
  
  try {
    const asistenciaData = {
      alumno: { idAlumno: form.value.alumno.idAlumno },
      estado: form.value.estado,
      fechaAsistencia: form.value.fechaAsistencia
    }
    
    if (editingAsistencia.value) {
      await asistenciaService.update(editingAsistencia.value.idAsistencia, asistenciaData)
    } else {
      await asistenciaService.create(asistenciaData)
    }
    
    closeModal()
    loadAsistencias()
  } catch (error) {
    console.error('Error al guardar asistencia:', error)
    error.value = 'Error al guardar la asistencia'
  }
}

const deleteAsistencia = async (id) => {
  if (confirm('¿Estás seguro de eliminar esta asistencia?')) {
    try {
      await asistenciaService.delete(id)
      loadAsistencias()
    } catch (error) {
      console.error('Error al eliminar asistencia:', error)
      alert('Error al eliminar la asistencia')
    }
  }
}

onMounted(() => {
  loadAsistencias()
})

// Cerrar el dropdown cuando se hace click fuera
document.addEventListener('click', (e) => {
  if (!e.target.closest('.relative')) {
    showAlumnosList.value = false
  }
})
</script>
