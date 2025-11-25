<template>
  <app-layout>
    <div class="p-3 sm:p-6">
      <a-page-header
        title="Gestión de Asistencias"
        class="bg-white mb-4 sm:mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="middle" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            <span class="hidden sm:inline">Nueva Asistencia</span>
            <span class="sm:hidden">Nueva</span>
          </a-button>
        </template>
      </a-page-header>
      
      <a-card class="mb-4 sm:mb-6">
        <a-row :gutter="[16, 16]" align="middle">
          <a-col :xs="24" :sm="12" :md="6">
            <a-select
              v-model:value="filtroClase"
              placeholder="Filtrar por clase"
              style="width: 100%"
              allowClear
              :options="clasesOptions"
            />
          </a-col>
          <a-col :xs="24" :sm="12" :md="5">
            <a-select
              v-model:value="filtroEstado"
              placeholder="Filtrar por estado"
              style="width: 100%"
              allowClear
            >
              <a-select-option value="Presente">Presente</a-select-option>
              <a-select-option value="Ausente">Ausente</a-select-option>
              <a-select-option value="Tardanza">Tardanza</a-select-option>
            </a-select>
          </a-col>
          <a-col :xs="24" :sm="12" :md="7">
            <a-range-picker
              v-model:value="rangoFechas"
              format="DD/MM/YYYY"
              placeholder="['Inicio', 'Fin']"
              style="width: 100%"
            />
          </a-col>
          <a-col :xs="24" :sm="12" :md="6">
            <a-space class="w-full flex-wrap">
              <a-button type="primary" @click="aplicarFiltros">
                <template #icon><SearchOutlined /></template>
                Filtrar
              </a-button>
              <a-button type="default" @click="limpiarFiltros">
                Limpiar
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-card>
      
      <a-card>
        <div class="overflow-x-auto">
          <a-table
            :scroll="{ x: 700 }"
          :columns="columns"
          :data-source="asistenciasFiltradas"
          :loading="loading"
          :row-key="record => record.idAsistencia"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'alumno'">
              {{ record.alumno?.nombre }} {{ record.alumno?.apellido }}
            </template>
            <template v-else-if="column.key === 'clase'">
              {{ record.clase?.tipo }} - {{ formatHorario(record.clase?.horario) }}
            </template>
            <template v-else-if="column.key === 'estado'">
              <a-tag :color="getEstadoColor(record.estado)">
                {{ record.estado }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Editar">
                  <a-button type="primary" style="background-color: #52c41a" @click="editAsistencia(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar esta asistencia?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteAsistencia(record.idAsistencia)"
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
      </a-card>
    </div>
    
    <!-- Modal -->
    <a-modal
      v-model:open="showModal"
      :title="editingAsistencia ? 'Editar Asistencia' : 'Nueva Asistencia'"
      width="600px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      @ok="saveAsistencia"
      @cancel="closeModal"
    >
      <a-form
        :model="form"
        layout="vertical"
        ref="formRef"
      >
        <a-form-item
          label="Clase"
          name="clase"
          :rules="[{ required: true, message: 'La clase es obligatoria' }]"
        >
          <a-select
            v-model:value="form.clase"
            placeholder="Seleccionar clase"
            :options="clasesOptions"
            @change="onClaseChange"
          />
        </a-form-item>
        
        <a-form-item
          label="Alumno"
          name="alumno"
          :rules="[{ required: true, message: 'Debes seleccionar un alumno' }]"
        >
          <a-select
            v-model:value="form.alumno"
            placeholder="Seleccionar alumno de la clase"
            :options="alumnosDeClaseOptions"
            :disabled="!form.clase"
            show-search
            :filter-option="filterAlumno"
          >
            <template #notFoundContent v-if="isSearching">
              <a-spin size="small" />
            </template>
          </a-select>
        </a-form-item>
        
        <a-form-item
          label="Estado"
          name="estado"
          :rules="[{ required: true, message: 'El estado es obligatorio' }]"
        >
          <a-select v-model:value="form.estado">
            <a-select-option value="Presente">Presente</a-select-option>
            <a-select-option value="Ausente">Ausente</a-select-option>
            <a-select-option value="Tardanza">Tardanza</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item
          label="Fecha"
          name="fechaAsistencia"
          :rules="[{ required: true, message: 'La fecha es obligatoria' }]"
        >
          <a-date-picker
            v-model:value="form.fechaAsistencia"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        
        <a-alert v-if="error" :message="error" type="error" show-icon closable class="mb-4" @close="error = ''" />
      </a-form>
    </a-modal>
  </app-layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { notification } from 'ant-design-vue'
import asistenciaService from '@/services/asistenciaService'
import alumnoService from '@/services/alumnoService'
import claseService from '@/services/claseService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'

const asistencias = ref([])
const asistenciasFiltradas = ref([])
const showModal = ref(false)
const editingAsistencia = ref(null)
const error = ref('')
const loading = ref(false)
const formRef = ref()
const isSearching = ref(false)
const alumnosEncontrados = ref([])
const searchTimeout = ref(null)
const clases = ref([])
const claseSeleccionada = ref(null)
const filtroClase = ref(null)
const filtroEstado = ref(null)
const rangoFechas = ref(null)

const form = ref({
  clase: null,
  alumno: null,
  estado: 'Presente',
  fechaAsistencia: new Date().toISOString().split('T')[0]
})

const columns = [
  { title: 'Alumno', key: 'alumno' },
  { title: 'Clase', key: 'clase' },
  { title: 'Estado', key: 'estado' },
  { title: 'Fecha', dataIndex: 'fechaAsistencia' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const alumnosOptions = computed(() => {
  return alumnosEncontrados.value.map(alumno => ({
    label: `${alumno.nombre} ${alumno.apellido} - DNI: ${alumno.dni}`,
    value: alumno.idAlumno
  }))
})

const clasesOptions = computed(() => {
  return clases.value.map(clase => ({
    label: `${clase.instructor} ${clase.apellidoInstructor} - ${formatHorario(clase.horario)} (${clase.tipo})`,
    value: clase.idClase
  }))
})

const alumnosDeClaseOptions = computed(() => {
  if (!claseSeleccionada.value || !claseSeleccionada.value.alumnos) {
    return []
  }
  
  return claseSeleccionada.value.alumnos.map(alumno => ({
    label: `${alumno.nombre} ${alumno.apellido} - DNI: ${alumno.dni}`,
    value: alumno.idAlumno
  }))
})

const formatHorario = (horario) => {
  if (!horario) return '-'
  return horario.substring(0, 5)
}

const getEstadoColor = (estado) => {
  const colors = {
    'Presente': 'green',
    'Ausente': 'red',
    'Tardanza': 'orange'
  }
  return colors[estado] || 'default'
}

const filterAlumno = (input, option) => {
  return option.label.toLowerCase().includes(input.toLowerCase())
}

const onClaseChange = async (claseId) => {
  // Resetear alumno seleccionado cuando cambia la clase
  form.value.alumno = null
  
  if (!claseId) {
    claseSeleccionada.value = null
    return
  }
  
  // Buscar la clase completa con sus alumnos
  try {
    claseSeleccionada.value = await claseService.getById(claseId)
  } catch (error) {
    console.error('Error al cargar clase:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo cargar la información de la clase',
      duration: 3
    })
  }
}

const searchAlumnos = async (value) => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  if (!value || value.length < 2) {
    alumnosEncontrados.value = []
    return
  }
  
  searchTimeout.value = setTimeout(async () => {
    isSearching.value = true
    try {
      const response = await alumnoService.searchAlumnos(value, 0, 10)
      alumnosEncontrados.value = response.content
    } catch (error) {
      console.error('Error al buscar alumnos:', error)
      alumnosEncontrados.value = []
    } finally {
      isSearching.value = false
    }
  }, 500)
}

const loadAsistencias = async () => {
  loading.value = true
  try {
    asistencias.value = await asistenciaService.getAll()
    asistenciasFiltradas.value = asistencias.value
  } catch (error) {
    console.error('Error al cargar asistencias:', error)
  } finally {
    loading.value = false
  }
}

const aplicarFiltros = () => {
  let filtered = [...asistencias.value]
  
  // Filtrar por clase
  if (filtroClase.value) {
    filtered = filtered.filter(a => a.clase?.idClase === filtroClase.value)
  }
  
  // Filtrar por estado
  if (filtroEstado.value) {
    filtered = filtered.filter(a => a.estado === filtroEstado.value)
  }
  
  // Filtrar por rango de fechas
  if (rangoFechas.value && rangoFechas.value[0] && rangoFechas.value[1]) {
    const inicio = rangoFechas.value[0].format('YYYY-MM-DD')
    const fin = rangoFechas.value[1].format('YYYY-MM-DD')
    filtered = filtered.filter(a => {
      return a.fechaAsistencia >= inicio && a.fechaAsistencia <= fin
    })
  }
  
  asistenciasFiltradas.value = filtered
}

const limpiarFiltros = () => {
  filtroClase.value = null
  filtroEstado.value = null
  rangoFechas.value = null
  asistenciasFiltradas.value = asistencias.value
}

const loadClases = async () => {
  try {
    clases.value = await claseService.getAll()
  } catch (error) {
    console.error('Error al cargar clases:', error)
  }
}

const resetForm = () => {
  form.value = {
    clase: null,
    alumno: null,
    estado: 'Presente',
    fechaAsistencia: new Date().toISOString().split('T')[0]
  }
  alumnosEncontrados.value = []
  claseSeleccionada.value = null
  error.value = ''
}

const closeModal = () => {
  showModal.value = false
  resetForm()
  editingAsistencia.value = null
}

const saveAsistencia = async () => {
  try {
    await formRef.value.validate()
    
    const asistenciaData = {
      alumno: { idAlumno: form.value.alumno },
      clase: { idClase: form.value.clase },
      estado: form.value.estado,
      fechaAsistencia: form.value.fechaAsistencia
    }
    
    if (editingAsistencia.value) {
      await asistenciaService.update(editingAsistencia.value.idAsistencia, asistenciaData)
      notification.success({
        message: 'Asistencia actualizada',
        description: 'La asistencia se actualizó correctamente',
        duration: 3
      })
    } else {
      await asistenciaService.create(asistenciaData)
      notification.success({
        message: 'Asistencia registrada',
        description: 'La asistencia se registró correctamente',
        duration: 3
      })
    }
    
    closeModal()
    loadAsistencias()
  } catch (error) {
    if (error.response) {
      notification.error({
        message: 'Error del servidor',
        description: error.response?.data?.message || 'Error al guardar la asistencia',
        duration: 3
      })
    }
    console.error('Error al guardar:', error)
  }
}

const editAsistencia = async (asistencia) => {
  editingAsistencia.value = asistencia
  
  // Cargar la clase con sus alumnos
  if (asistencia.clase?.idClase) {
    try {
      claseSeleccionada.value = await claseService.getById(asistencia.clase.idClase)
    } catch (error) {
      console.error('Error al cargar clase:', error)
    }
  }
  
  form.value = {
    clase: asistencia.clase?.idClase,
    alumno: asistencia.alumno?.idAlumno,
    estado: asistencia.estado,
    fechaAsistencia: asistencia.fechaAsistencia
  }
  
  // Agregar el alumno actual a las opciones
  if (asistencia.alumno) {
    alumnosEncontrados.value = [asistencia.alumno]
  }
  showModal.value = true
}

const deleteAsistencia = async (id) => {
  try {
    await asistenciaService.delete(id)
    notification.success({
      message: 'Asistencia eliminada',
      description: 'La asistencia fue eliminada correctamente',
      duration: 3
    })
    loadAsistencias()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar la asistencia',
      duration: 3
    })
  }
}

onMounted(() => {
  loadAsistencias()
  loadClases()
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
