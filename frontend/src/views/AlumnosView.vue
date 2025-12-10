<template>
  <app-layout>
    <div class="p-3 sm:p-6">
      <a-page-header
        title="Gestión de Alumnos"
        class="bg-white mb-4 sm:mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" :size="isMobile ? 'middle' : 'large'" @click="showModal = true; editingAlumno = null; resetForm()">
            <template #icon><PlusOutlined /></template>
            <span class="hidden sm:inline">Nuevo Alumno</span>
          </a-button>
        </template>
      </a-page-header>
      
      <a-card class="mb-4 sm:mb-6">
        <a-input-search
          v-model:value="searchTerm"
          placeholder="Buscar alumno..."
          :size="isMobile ? 'middle' : 'large'"
          @search="buscarAlumnos"
          @input="buscarAlumnos"
        >
          <template #prefix>
            <SearchOutlined />
          </template>
        </a-input-search>
      </a-card>
      
      <a-card>
        <div class="overflow-x-auto">
          <a-table
            :columns="columns"
            :data-source="alumnos"
            :pagination="false"
            :loading="loading"
            :row-key="record => record.idAlumno"
            :scroll="{ x: 800 }"
          >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'nombre'">
              {{ record.nombre }} {{ record.apellido }}
            </template>
            <template v-else-if="column.key === 'disciplina'">
              <a-tag :color="record.disciplina === 'Taekwondo' ? 'blue' : 'green'">{{ record.disciplina }}</a-tag>
            </template>
            <template v-else-if="column.key === 'categoria'">
              <a-tag v-if="record.categoria" color="blue">{{ record.categoria }}</a-tag>
              <span v-else class="text-gray-400">N/A</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Ver Clases">
                  <a-button type="default" @click="verClasesAlumno(record)">
                    <template #icon><UnorderedListOutlined /></template>
                  </a-button>
                </a-tooltip>
                <a-tooltip title="Editar">
                  <a-button type="primary" style="background-color: #52c41a" @click="editAlumno(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar este alumno?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteAlumno(record.idAlumno)"
                >
                  <a-tooltip title="Eliminar">
                    <a-button type="primary" danger>
                      <DeleteOutlined />
                    </a-button>
                  </a-tooltip>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
        </div>
        
        <div class="mt-4 flex justify-between items-center">
          <div class="text-sm text-gray-700">
            Mostrando {{ (pagination.currentPage * pagination.size) + 1 }} a 
            {{ Math.min((pagination.currentPage + 1) * pagination.size, pagination.totalItems) }} 
            de {{ pagination.totalItems }} alumnos
          </div>
          
          <a-pagination
            v-model:current="currentPage"
            v-model:page-size="pagination.size"
            :total="pagination.totalItems"
            :show-size-changer="true"
            :page-size-options="['5', '10', '20', '50']"
            @change="onPageChange"
            @show-size-change="onPageSizeChange"
          />
        </div>
      </a-card>
    </div>
    
    <!-- Modal -->
    <a-modal
      v-model:open="showModal"
      :title="editingAlumno ? 'Editar Alumno' : 'Nuevo Alumno'"
      width="800px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      @ok="saveAlumno"
      @cancel="closeModal"
    >
      <a-form
        :model="form"
        layout="vertical"
        ref="formRef"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="Nombre"
              name="nombre"
              :rules="[
                { required: true, message: 'El nombre es obligatorio' },
                { pattern: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, message: 'Solo se permiten letras' }
              ]"
            >
              <a-input v-model:value="form.nombre" placeholder="Nombre del alumno" />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Apellido"
              name="apellido"
              :rules="[
                { required: true, message: 'El apellido es obligatorio' },
                { pattern: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, message: 'Solo se permiten letras' }
              ]"
            >
              <a-input v-model:value="form.apellido" placeholder="Apellido del alumno" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="DNI"
              name="dni"
              :rules="[
                { required: true, message: 'El DNI es obligatorio' },
                { pattern: /^\d+$/, message: 'Solo se permiten números' }
              ]"
            >
              <a-input v-model:value="form.dni" placeholder="Solo números" />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Teléfono"
              name="telefono"
              :rules="[{ pattern: /^\d+$/, message: 'Solo se permiten números' }]"
            >
              <a-input v-model:value="form.telefono" placeholder="Solo números" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item
              label="Peso (Kg)"
              name="peso"
            >
              <a-input-number
                v-model:value="form.peso"
                :min="0"
                :step="0.1"
                style="width: 100%"
                placeholder="Peso en Kg"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item
              label="Género"
              name="genero"
              :rules="[{ required: true, message: 'El género es obligatorio' }]"
            >
              <a-select v-model:value="form.genero" placeholder="Seleccionar">
                <a-select-option value="Masculino">Masculino</a-select-option>
                <a-select-option value="Femenino">Femenino</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item
              label="Fecha de Nacimiento"
              name="fechaNacimiento"
              :rules="[{ required: true, message: 'La fecha es obligatoria' }]"
            >
              <a-date-picker
                v-model:value="form.fechaNacimiento"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item
          label="Disciplina"
          name="disciplina"
          :rules="[{ required: true, message: 'La disciplina es obligatoria' }]"
        >
          <a-select v-model:value="form.disciplina" placeholder="Seleccionar disciplina" @change="onDisciplinaChange">
            <a-select-option value="Taekwondo">Taekwondo</a-select-option>
            <a-select-option value="Calistenia">Calistenia</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item
          v-if="form.disciplina === 'Taekwondo'"
          label="Categoría / Cinturón"
          name="categoria"
          :rules="form.disciplina === 'Taekwondo' ? [{ required: true, message: 'La categoría es obligatoria' }] : []"
        >
          <a-select v-model:value="form.categoria" placeholder="Seleccionar categoría">
            <a-select-option value="Blanco">Blanco</a-select-option>
            <a-select-option value="Blanco punta amarilla">Blanco punta amarilla</a-select-option>
            <a-select-option value="Amarillo">Amarillo</a-select-option>
            <a-select-option value="Amarillo punta verde">Amarillo punta verde</a-select-option>
            <a-select-option value="Verde">Verde</a-select-option>
            <a-select-option value="Verde punta azul">Verde punta azul</a-select-option>
            <a-select-option value="Azul">Azul</a-select-option>
            <a-select-option value="Azul punta roja">Azul punta roja</a-select-option>
            <a-select-option value="Rojo">Rojo</a-select-option>
            <a-select-option value="Rojo punta negra">Rojo punta negra</a-select-option>
            <a-select-option value="Negro primer dan">Negro primer dan</a-select-option>
            <a-select-option value="Negro segundo dan">Negro segundo dan</a-select-option>
            <a-select-option value="Negro tercer dan">Negro tercer dan</a-select-option>
            <a-select-option value="Negro cuarto dan">Negro cuarto dan</a-select-option>
            <a-select-option value="Negro quinto dan">Negro quinto dan</a-select-option>
            <a-select-option value="Negro sexto dan">Negro sexto dan</a-select-option>
            <a-select-option value="Negro séptimo dan">Negro séptimo dan</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-alert v-if="error" :message="error" type="error" show-icon closable class="mb-4" @close="error = ''" />
      </a-form>
    </a-modal>
    
    <!-- Modal Ver Clases -->
    <a-modal
      v-model:open="showClasesModal"
      :title="`Clases de ${alumnoSeleccionado?.nombre || ''} ${alumnoSeleccionado?.apellido || ''}`"
      width="700px"
      ok-text="Cerrar"
      :cancel-button-props="{ style: { display: 'none' } }"
      @ok="closeClasesModal"
    >
      <a-table
        :columns="clasesColumns"
        :data-source="clasesAlumno"
        :loading="loadingClases"
        :pagination="false"
        :row-key="record => record.idClase"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'instructor'">
            {{ record.instructor }} {{ record.apellidoInstructor }}
          </template>
          <template v-else-if="column.key === 'horario'">
            {{ formatHorario(record.horario) }}
          </template>
          <template v-else-if="column.key === 'tipo'">
            <a-tag :color="record.tipo === 'Taekwondo' ? 'blue' : 'green'">{{ record.tipo }}</a-tag>
          </template>
        </template>
        <template #emptyText>
          <a-empty description="Este alumno no está inscripto en ninguna clase" />
        </template>
      </a-table>
    </a-modal>
  </app-layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { notification } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import alumnoService from '@/services/alumnoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  UnorderedListOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const alumnos = ref([])
const showModal = ref(false)
const editingAlumno = ref(null)
const error = ref('')
const searchTerm = ref('')
const loading = ref(false)
const formRef = ref()

// Detectar si es móvil
const isMobile = computed(() => window.innerWidth < 640)

// Modal de clases
const showClasesModal = ref(false)
const alumnoSeleccionado = ref(null)
const clasesAlumno = ref([])
const loadingClases = ref(false)

const pagination = ref({
  currentPage: 0,
  totalPages: 0,
  totalItems: 0,
  size: 10,
  hasPrevious: false,
  hasNext: false
})

const currentPage = computed({
  get: () => pagination.value.currentPage + 1,
  set: (val) => pagination.value.currentPage = val - 1
})

const form = ref({
  nombre: '',
  apellido: '',
  dni: '',
  telefono: '',
  peso: null,
  disciplina: '',
  categoria: '',
  genero: '',
  fechaNacimiento: null
})

const columns = [
  { title: 'Nombre', key: 'nombre', dataIndex: 'nombre' },
  { title: 'DNI', dataIndex: 'dni' },
  { title: 'Teléfono', dataIndex: 'telefono' },
  { title: 'Disciplina', key: 'disciplina', dataIndex: 'disciplina' },
  { title: 'Categoría', key: 'categoria', dataIndex: 'categoria' },
  { title: 'Edad', dataIndex: 'edad' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const clasesColumns = [
  { title: 'Instructor', key: 'instructor', dataIndex: 'instructor' },
  { title: 'Horario', key: 'horario', dataIndex: 'horario' },
  { title: 'Tipo', key: 'tipo', dataIndex: 'tipo' }
]

const loadAlumnos = async () => {
  loading.value = true
  try {
    const response = await alumnoService.getAll(
      pagination.value.currentPage,
      pagination.value.size,
      'nombre',
      'asc'
    )
    alumnos.value = response.content
    pagination.value.totalPages = response.totalPages
    pagination.value.totalItems = response.totalItems
    pagination.value.currentPage = response.currentPage
    pagination.value.hasPrevious = response.currentPage > 0
    pagination.value.hasNext = response.currentPage < response.totalPages - 1
  } catch (error) {
    console.error('Error al cargar alumnos:', error)
  } finally {
    loading.value = false
  }
}

const buscarAlumnos = async () => {
  if (searchTerm.value.trim()) {
    loading.value = true
    try {
      const response = await alumnoService.buscar(searchTerm.value)
      alumnos.value = response
    } catch (error) {
      console.error('Error al buscar:', error)
    } finally {
      loading.value = false
    }
  } else {
    loadAlumnos()
  }
}

const onPageChange = (page) => {
  pagination.value.currentPage = page - 1
  loadAlumnos()
}

const onPageSizeChange = (current, size) => {
  pagination.value.size = size
  pagination.value.currentPage = 0
  loadAlumnos()
}

const resetForm = () => {
  form.value = {
    nombre: '',
    apellido: '',
    dni: '',
    telefono: '',
    peso: null,
    disciplina: '',
    categoria: '',
    genero: '',
    fechaNacimiento: null
  }
  error.value = ''
}

const onDisciplinaChange = () => {
  // Limpiar categoría si se cambia a Calistenia
  if (form.value.disciplina !== 'Taekwondo') {
    form.value.categoria = ''
  }
}

const closeModal = () => {
  showModal.value = false
  resetForm()
  editingAlumno.value = null
}

const saveAlumno = async () => {
  try {
    await formRef.value.validate()
    
    const alumnoData = { ...form.value }
    
    if (editingAlumno.value) {
      await alumnoService.update(editingAlumno.value.idAlumno, alumnoData)
      notification.success({
        message: 'Alumno actualizado',
        description: 'El alumno se actualizó correctamente',
        duration: 3
      })
    } else {
      await alumnoService.create(alumnoData)
      notification.success({
        message: 'Alumno creado',
        description: 'El alumno se creó correctamente',
        duration: 3
      })
    }
    
    closeModal()
    loadAlumnos()
  } catch (error) {
    if (error.response) {
      notification.error({
        message: 'Error del servidor',
        description: error.response?.data?.message || 'Error al guardar el alumno',
        duration: 3
      })
    }
    console.error('Error al guardar:', error)
  }
}

const editAlumno = (alumno) => {
  editingAlumno.value = alumno
  form.value = { ...alumno }
  showModal.value = true
}

const deleteAlumno = async (id) => {
  try {
    await alumnoService.delete(id)
    notification.success({
      message: 'Alumno eliminado',
      description: 'El alumno fue eliminado correctamente',
      duration: 3
    })
    loadAlumnos()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar el alumno',
      duration: 3
    })
  }
}

const verClasesAlumno = async (alumno) => {
  alumnoSeleccionado.value = alumno
  showClasesModal.value = true
  loadingClases.value = true
  
  try {
    clasesAlumno.value = await alumnoService.getClases(alumno.idAlumno)
  } catch (error) {
    console.error('Error al cargar clases:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudieron cargar las clases del alumno',
      duration: 3
    })
  } finally {
    loadingClases.value = false
  }
}

const closeClasesModal = () => {
  showClasesModal.value = false
  alumnoSeleccionado.value = null
  clasesAlumno.value = []
}

const formatHorario = (horario) => {
  if (!horario) return ''
  // horario viene como "HH:mm:ss" del backend
  const [hours, minutes] = horario.split(':')
  return `${hours}:${minutes}`
}

onMounted(() => {
  loadAlumnos()
})
</script>

<style scoped>
.ant-btn {
  border: none !important;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>
