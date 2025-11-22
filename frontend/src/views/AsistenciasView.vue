<template>
  <app-layout>
    <div class="p-6">
      <a-page-header
        title="Gestión de Asistencias"
        class="bg-white mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="large" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            Nueva Asistencia
          </a-button>
        </template>
      </a-page-header>
      
      <a-card>
        <a-table
          :columns="columns"
          :data-source="asistencias"
          :loading="loading"
          :row-key="record => record.idAsistencia"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'alumno'">
              {{ record.alumno?.nombre }} {{ record.alumno?.apellido }}
            </template>
            <template v-else-if="column.key === 'estado'">
              <a-tag :color="getEstadoColor(record.estado)">
                {{ record.estado }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-button type="link" @click="editAsistencia(record)">
                  <EditOutlined /> Editar
                </a-button>
                <a-popconfirm
                  title="¿Estás seguro de eliminar esta asistencia?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteAsistencia(record.idAsistencia)"
                >
                  <a-button type="link" danger>
                    <DeleteOutlined /> Eliminar
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
    
    <!-- Modal -->
    <a-modal
      v-model:open="showModal"
      :title="editingAsistencia ? 'Editar Asistencia' : 'Nueva Asistencia'"
      width="600px"
      @ok="saveAsistencia"
      @cancel="closeModal"
    >
      <a-form
        :model="form"
        layout="vertical"
        ref="formRef"
      >
        <a-form-item
          label="Alumno"
          name="alumno"
          :rules="[{ required: true, message: 'Debes seleccionar un alumno' }]"
        >
          <a-select
            v-model:value="form.alumno"
            show-search
            placeholder="Buscar alumno..."
            :filter-option="false"
            :not-found-content="isSearching ? undefined : 'No se encontraron alumnos'"
            @search="searchAlumnos"
            :loading="isSearching"
            :options="alumnosOptions"
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
import asistenciaService from '@/services/asistenciaService'
import alumnoService from '@/services/alumnoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'

const asistencias = ref([])
const showModal = ref(false)
const editingAsistencia = ref(null)
const error = ref('')
const loading = ref(false)
const formRef = ref()
const isSearching = ref(false)
const alumnosEncontrados = ref([])
const searchTimeout = ref(null)

const form = ref({
  alumno: null,
  estado: 'Presente',
  fechaAsistencia: new Date().toISOString().split('T')[0]
})

const columns = [
  { title: 'Alumno', key: 'alumno' },
  { title: 'Estado', key: 'estado', dataIndex: 'estado' },
  { title: 'Fecha', dataIndex: 'fechaAsistencia' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const alumnosOptions = computed(() => {
  return alumnosEncontrados.value.map(alumno => ({
    label: `${alumno.nombre} ${alumno.apellido} - DNI: ${alumno.dni}`,
    value: alumno.idAlumno
  }))
})

const getEstadoColor = (estado) => {
  const colors = {
    'Presente': 'green',
    'Ausente': 'red',
    'Tardanza': 'orange'
  }
  return colors[estado] || 'default'
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
  } catch (error) {
    console.error('Error al cargar asistencias:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.value = {
    alumno: null,
    estado: 'Presente',
    fechaAsistencia: new Date().toISOString().split('T')[0]
  }
  alumnosEncontrados.value = []
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
    console.error('Error al guardar:', error)
    error.value = 'Error al guardar la asistencia'
  }
}

const editAsistencia = (asistencia) => {
  editingAsistencia.value = asistencia
  form.value = {
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
    loadAsistencias()
  } catch (error) {
    console.error('Error al eliminar:', error)
  }
}

onMounted(() => {
  loadAsistencias()
})
</script>
