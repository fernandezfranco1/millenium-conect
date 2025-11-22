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
        <h2 class="text-3xl font-bold text-gray-800">Gestión de Cuotas</h2>
        <button
          @click="showModal = true; resetForm()"
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded transition"
        >
          + Nueva Cuota
        </button>
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 border-b">
            <tr>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Alumno</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Mes</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Monto</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Fecha Pago</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Forma de Pago</th>
              <th class="px-6 py-3 text-left text-gray-700 font-medium">Estado</th>
              <th class="px-6 py-3 text-center text-gray-700 font-medium">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cuota in cuotas" :key="cuota.idCuota" class="border-b hover:bg-gray-50">
              <td class="px-6 py-4">{{ cuota.alumno?.nombre }} {{ cuota.alumno?.apellido }}</td>
              <td class="px-6 py-4">{{ formatMesPago(cuota.mesPago) }}</td>
              <td class="px-6 py-4 font-semibold">{{ formatCurrency(cuota.monto) }}</td>
              <td class="px-6 py-4">{{ cuota.fechaPago }}</td>
              <td class="px-6 py-4">{{ cuota.formaPago }}</td>
              <td class="px-6 py-4">
                <span :class="getEstadoClass(cuota.estado)" class="px-3 py-1 rounded-full text-sm">
                  {{ cuota.estado }}
                </span>
              </td>
              <td class="px-6 py-4 text-center">
                <button
                  @click="editCuota(cuota)"
                  class="text-blue-600 hover:text-blue-800 mr-3"
                >
                  Editar
                </button>
                <button
                  @click="deleteCuota(cuota.idCuota)"
                  class="text-red-600 hover:text-red-800"
                >
                  Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- Paginación -->
      <div class="bg-white px-6 py-4 flex items-center justify-between border-t">
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-700">Mostrar</span>
          <select
            v-model="pagination.size"
            @change="changePageSize"
            class="border rounded px-2 py-1 text-sm"
          >
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
          <span class="text-sm text-gray-700">
            de {{ pagination.totalItems }} cuotas
          </span>
        </div>
        
        <div class="flex items-center space-x-2">
          <button
            @click="changePage(0)"
            :disabled="pagination.currentPage === 0"
            class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            «
          </button>
          <button
            @click="changePage(pagination.currentPage - 1)"
            :disabled="pagination.currentPage === 0"
            class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ‹
          </button>
          
          <button
            v-for="page in displayPages"
            :key="page"
            @click="changePage(page)"
            :class="[
              'px-3 py-1 border rounded',
              pagination.currentPage === page
                ? 'bg-blue-600 text-white'
                : 'hover:bg-gray-100'
            ]"
          >
            {{ page + 1 }}
          </button>
          
          <button
            @click="changePage(pagination.currentPage + 1)"
            :disabled="pagination.currentPage >= pagination.totalPages - 1"
            class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ›
          </button>
          <button
            @click="changePage(pagination.totalPages - 1)"
            :disabled="pagination.currentPage >= pagination.totalPages - 1"
            class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            »
          </button>
        </div>
      </div>
    </div>
    
    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50 overflow-y-auto">
      <div class="bg-white rounded-lg p-6 w-full max-w-md my-8">
        <h3 class="text-2xl font-bold mb-4">
          {{ editingCuota ? 'Editar Cuota' : 'Nueva Cuota' }}
        </h3>
        
        <form @submit.prevent="saveCuota" class="space-y-4">
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
          
          <!-- Mes de Pago -->
          <div>
            <label class="block text-gray-700 mb-2">Mes de Pago *</label>
            <input
              v-model="form.mesPago"
              type="month"
              required
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <!-- Monto -->
          <div>
            <label class="block text-gray-700 mb-2">Monto *</label>
            <div class="relative">
              <span class="absolute left-3 top-2.5 text-gray-600 font-semibold">$</span>
              <input
                v-model="form.monto"
                type="number"
                step="0.01"
                min="0"
                required
                placeholder="0.00"
                class="w-full pl-8 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
          
          <!-- Fecha de Pago -->
          <div>
            <label class="block text-gray-700 mb-2">Fecha de Pago *</label>
            <input
              v-model="form.fechaPago"
              type="date"
              required
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <!-- Forma de Pago -->
          <div>
            <label class="block text-gray-700 mb-2">Forma de Pago *</label>
            <select
              v-model="form.formaPago"
              required
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Seleccionar...</option>
              <option value="Débito">Débito</option>
              <option value="Efectivo">Efectivo</option>
              <option value="Transferencia">Transferencia</option>
              <option value="Crédito">Crédito</option>
            </select>
          </div>
          
          <!-- Estado -->
          <div>
            <label class="block text-gray-700 mb-2">Estado *</label>
            <select
              v-model="form.estado"
              required
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="Pendiente">Pendiente</option>
              <option value="Pagado">Pagado</option>
            </select>
          </div>
          
          <!-- Comprobante -->
          <div>
            <label class="block text-gray-700 mb-2">Comprobante (opcional)</label>
            <input
              ref="fileInput"
              type="file"
              accept="image/*,application/pdf"
              @change="handleFileChange"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <p class="text-xs text-gray-500 mt-1">Formatos: PDF o imágenes (máx. 10MB)</p>
            <div v-if="uploadProgress" class="mt-2">
              <div class="text-sm text-blue-600">Subiendo archivo...</div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-blue-600 h-2 rounded-full transition-all" :style="{width: uploadProgress + '%'}"></div>
              </div>
            </div>
            <div v-if="form.comprobante" class="mt-2 text-sm text-green-600">
              ✓ Archivo: {{ form.comprobante }}
            </div>
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
              :disabled="uploadProgress > 0 && uploadProgress < 100"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
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
import cuotaService from '@/services/cuotaService'
import alumnoService from '@/services/alumnoService'

const router = useRouter()
const authStore = useAuthStore()
const cuotas = ref([])
const showModal = ref(false)
const editingCuota = ref(null)
const error = ref('')
const fileInput = ref(null)
const uploadProgress = ref(0)

// Paginación
const pagination = ref({
  currentPage: 0,
  totalPages: 0,
  totalItems: 0,
  size: 10
})

// Autocomplete de alumnos
const alumnosEncontrados = ref([])
const alumnoSearch = ref('')
const showAlumnosList = ref(false)
const searchTimeout = ref(null)
const isSearching = ref(false)

const form = ref({
  alumno: null,
  mesPago: '',
  monto: '',
  fechaPago: new Date().toISOString().split('T')[0],
  formaPago: '',
  estado: 'Pendiente',
  comprobante: ''
})

const displayPages = computed(() => {
  const pages = []
  const current = pagination.value.currentPage
  const total = pagination.value.totalPages
  
  let start = Math.max(0, current - 2)
  let end = Math.min(total - 1, current + 2)
  
  if (end - start < 4) {
    if (start === 0) {
      end = Math.min(total - 1, start + 4)
    } else if (end === total - 1) {
      start = Math.max(0, end - 4)
    }
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS'
  }).format(value)
}

const formatMesPago = (mesPago) => {
  if (!mesPago) return ''
  const [year, month] = mesPago.split('-')
  const months = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 
                  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre']
  return `${months[parseInt(month) - 1]} ${year}`
}

const getEstadoClass = (estado) => {
  const classes = {
    'Pagado': 'bg-green-100 text-green-800',
    'Pendiente': 'bg-yellow-100 text-yellow-800'
  }
  return classes[estado] || 'bg-gray-100 text-gray-800'
}

const searchAlumnos = async () => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  if (!alumnoSearch.value || alumnoSearch.value.length < 2) {
    alumnosEncontrados.value = []
    showAlumnosList.value = false
    return
  }
  
  showAlumnosList.value = true
  
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

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  try {
    uploadProgress.value = 50
    const response = await cuotaService.uploadComprobante(file)
    form.value.comprobante = response.filename
    uploadProgress.value = 100
    
    setTimeout(() => {
      uploadProgress.value = 0
    }, 1000)
  } catch (error) {
    console.error('Error al subir archivo:', error)
    error.value = error.response?.data?.error || 'Error al subir el archivo'
    uploadProgress.value = 0
  }
}

const resetForm = () => {
  form.value = {
    alumno: null,
    mesPago: '',
    monto: '',
    fechaPago: new Date().toISOString().split('T')[0],
    formaPago: '',
    estado: 'Pendiente',
    comprobante: ''
  }
  alumnoSearch.value = ''
  alumnosEncontrados.value = []
  showAlumnosList.value = false
  editingCuota.value = null
  error.value = ''
  uploadProgress.value = 0
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const saveCuota = async () => {
  if (!form.value.alumno) {
    error.value = 'Debes seleccionar un alumno'
    return
  }
  
  try {
    const cuotaData = {
      alumno: { idAlumno: form.value.alumno.idAlumno },
      mesPago: form.value.mesPago,
      monto: parseFloat(form.value.monto),
      fechaPago: form.value.fechaPago,
      formaPago: form.value.formaPago,
      estado: form.value.estado,
      comprobante: form.value.comprobante || null
    }
    
    if (editingCuota.value) {
      await cuotaService.update(editingCuota.value.idCuota, cuotaData)
    } else {
      await cuotaService.create(cuotaData)
    }
    
    closeModal()
    loadCuotas()
  } catch (error) {
    console.error('Error al guardar cuota:', error)
    error.value = 'Error al guardar la cuota'
  }
}

const editCuota = (cuota) => {
  editingCuota.value = cuota
  form.value = {
    alumno: cuota.alumno,
    mesPago: cuota.mesPago,
    monto: cuota.monto,
    fechaPago: cuota.fechaPago,
    formaPago: cuota.formaPago,
    estado: cuota.estado,
    comprobante: cuota.comprobante || ''
  }
  alumnoSearch.value = `${cuota.alumno.nombre} ${cuota.alumno.apellido}`
  showModal.value = true
}

const loadCuotas = async () => {
  try {
    const response = await cuotaService.getAll(
      pagination.value.currentPage,
      pagination.value.size,
      'fechaPago',
      'desc'
    )
    cuotas.value = response.content
    pagination.value.totalPages = response.totalPages
    pagination.value.totalItems = response.totalItems
    pagination.value.currentPage = response.currentPage
  } catch (error) {
    console.error('Error al cargar cuotas:', error)
  }
}

const changePage = (page) => {
  if (page >= 0 && page < pagination.value.totalPages) {
    pagination.value.currentPage = page
    loadCuotas()
  }
}

const changePageSize = () => {
  pagination.value.currentPage = 0
  loadCuotas()
}

const deleteCuota = async (id) => {
  if (confirm('¿Estás seguro de eliminar esta cuota?')) {
    try {
      await cuotaService.delete(id)
      loadCuotas()
    } catch (error) {
      console.error('Error al eliminar cuota:', error)
    }
  }
}

onMounted(() => {
  loadCuotas()
})
</script>
