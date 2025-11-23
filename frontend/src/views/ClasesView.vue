<template>
  <app-layout>
    <div class="p-6">
      <a-page-header
        title="Gestión de Clases"
        class="bg-white mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="large" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            Nueva Clase
          </a-button>
        </template>
      </a-page-header>
      
      <a-card class="mb-6" style="max-width: 800px;">
        <a-row :gutter="16" align="middle">
          <a-col :span="8">
            <a-select
              v-model:value="filtroTipo"
              placeholder="Filtrar por tipo"
              style="width: 100%"
              allowClear
            >
              <a-select-option value="Taekwondo">Taekwondo</a-select-option>
              <a-select-option value="Calistenia">Calistenia</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="8">
            <a-time-picker
              v-model:value="filtroHorario"
              placeholder="Filtrar por horario"
              format="HH:mm"
              style="width: 100%"
            />
          </a-col>
          <a-col :span="8">
            <a-space>
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
        <a-table
          :columns="columns"
          :data-source="clases"
          :loading="loading"
          :row-key="record => record.idClase"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'instructor'">
              {{ record.instructor }} {{ record.apellidoInstructor }}
            </template>
            <template v-else-if="column.key === 'horario'">
              {{ formatHorario(record.horario) }}
            </template>
            <template v-else-if="column.key === 'alumnos'">
              <a-tag color="blue">{{ record.alumnos?.length || 0 }} alumnos</a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Añadir Alumnos">
                  <a-button 
                    type="primary" 
                    style="background-color: #1890ff" 
                    @click.stop="showAlumnosModal(record)"
                  >
                    <TeamOutlined />
                  </a-button>
                </a-tooltip>
                <a-tooltip title="Editar">
                  <a-button 
                    type="primary" 
                    style="background-color: #52c41a" 
                    @click.stop="editClase(record)"
                  >
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar esta clase?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteClase(record.idClase)"
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
      </a-card>
    </div>
    
    <!-- Modal Crear/Editar Clase -->
    <a-modal
      v-model:open="showModal"
      :title="editingClase ? 'Editar Clase' : 'Nueva Clase'"
      width="600px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      @ok="saveClase"
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
              label="Nombre Instructor"
              name="instructor"
              :rules="[{ required: true, message: 'El nombre es obligatorio' }]"
            >
              <a-input v-model:value="form.instructor" placeholder="Ej: Juan" />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Apellido Instructor"
              name="apellidoInstructor"
              :rules="[{ required: true, message: 'El apellido es obligatorio' }]"
            >
              <a-input v-model:value="form.apellidoInstructor" placeholder="Ej: Pérez" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="Horario"
              name="horario"
              :rules="[{ required: true, message: 'El horario es obligatorio' }]"
            >
              <a-time-picker
                v-model:value="form.horario"
                style="width: 100%"
                format="HH:mm"
                value-format="HH:mm:ss"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Tipo"
              name="tipo"
              :rules="[{ required: true, message: 'El tipo es obligatorio' }]"
            >
              <a-select v-model:value="form.tipo" placeholder="Seleccione tipo">
                <a-select-option value="Taekwondo">Taekwondo</a-select-option>
                <a-select-option value="Calistenia">Calistenia</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
    
    <!-- Modal Gestionar Alumnos -->
    <a-modal
      :open="showAlumnosModalVisible"
      @update:open="(val) => showAlumnosModalVisible = val"
      :title="`Alumnos de la clase - ${claseSeleccionada?.instructor || ''} ${claseSeleccionada?.apellidoInstructor || ''}`"
      width="700px"
      ok-text="Cerrar"
      :cancel-button-props="{ style: { display: 'none' } }"
      @ok="closeAlumnosModal"
      :z-index="1000"
      :destroy-on-close="true"
    >
      <div class="mb-4">
        <a-select
          v-model:value="alumnoSeleccionado"
          show-search
          placeholder="Buscar y agregar alumno (mín. 2 caracteres)"
          style="width: 100%"
          :filter-option="false"
          :not-found-content="isSearchingAlumnos ? 'Buscando...' : 'No se encontraron alumnos'"
          :options="alumnosOptions"
          @search="searchAlumnos"
          @change="addAlumnoToClase"
        />
      </div>
      
      <a-divider>Alumnos inscriptos</a-divider>
      
      <a-list
        v-if="claseSeleccionada?.alumnos?.length"
        :data-source="claseSeleccionada.alumnos"
        :loading="loadingAlumnos"
      >
        <template #renderItem="{ item }">
          <a-list-item>
            <template #actions>
              <a-popconfirm
                title="¿Quitar alumno de esta clase?"
                ok-text="Sí"
                cancel-text="No"
                @confirm="removeAlumnoFromClase(item.idAlumno)"
              >
                <a-button type="link" danger size="small">
                  <template #icon><DeleteOutlined /></template>
                  Quitar
                </a-button>
              </a-popconfirm>
            </template>
            <a-list-item-meta>
              <template #title>
                {{ item.nombre }} {{ item.apellido }}
              </template>
              <template #description>
                DNI: {{ item.dni }} 
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
        
        <template #header>
          <div class="font-semibold">
            Total: {{ claseSeleccionada.alumnos.length }} alumnos
          </div>
        </template>
      </a-list>
      
      <a-empty v-else description="No hay alumnos en esta clase" />
    </a-modal>
  </app-layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { notification } from 'ant-design-vue'
import dayjs from 'dayjs'
import claseService from '@/services/claseService'
import alumnoService from '@/services/alumnoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

const clases = ref([])
const showModal = ref(false)
const showAlumnosModalVisible = ref(false)
const editingClase = ref(null)
const claseSeleccionada = ref(null)
const loading = ref(false)
const loadingAlumnos = ref(false)
const formRef = ref()
const filtroTipo = ref(null)
const filtroHorario = ref(null)
const alumnoSeleccionado = ref(null)
const alumnosEncontrados = ref([])
const isSearchingAlumnos = ref(false)
const searchTimeout = ref(null)

const form = ref({
  instructor: '',
  apellidoInstructor: '',
  horario: null,
  tipo: ''
})

const columns = [
  { title: 'Instructor', key: 'instructor' },
  { title: 'Horario', key: 'horario' },
  { title: 'Tipo', dataIndex: 'tipo' },
  { title: 'Alumnos', key: 'alumnos', align: 'center' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const alumnosOptions = computed(() => {
  if (!claseSeleccionada.value) return []
  
  const alumnosEnClase = claseSeleccionada.value.alumnos?.map(a => a.idAlumno) || []
  
  return alumnosEncontrados.value
    .filter(alumno => !alumnosEnClase.includes(alumno.idAlumno))
    .map(alumno => ({
      label: `${alumno.nombre} ${alumno.apellido} - DNI: ${alumno.dni}`,
      value: alumno.idAlumno
    }))
})

const formatHorario = (horario) => {
  if (!horario) return '-'
  // horario viene como "HH:mm:ss" del backend
  return horario.substring(0, 5) // Retorna "HH:mm"
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
    isSearchingAlumnos.value = true
    try {
      const response = await alumnoService.searchAlumnos(value, 0, 10)
      alumnosEncontrados.value = response.content
    } catch (error) {
      console.error('Error al buscar alumnos:', error)
      alumnosEncontrados.value = []
    } finally {
      isSearchingAlumnos.value = false
    }
  }, 500)
}

const filterAlumno = (input, option) => {
  const text = option.children[0].children.toLowerCase()
  return text.includes(input.toLowerCase())
}

const loadClases = async () => {
  loading.value = true
  try {
    clases.value = await claseService.getAll()
  } catch (error) {
    console.error('Error al cargar clases:', error)
  } finally {
    loading.value = false
  }
}

const aplicarFiltros = async () => {
  loading.value = true
  try {
    const tipo = filtroTipo.value
    const horario = filtroHorario.value ? filtroHorario.value.format('HH:mm:ss') : null
    
    if (!tipo && !horario) {
      await loadClases()
      return
    }
    
    clases.value = await claseService.filtrar(tipo, horario)
  } catch (error) {
    console.error('Error al filtrar:', error)
    if (error.response) {
      notification.error({
        message: 'Error al filtrar',
        description: 'No se pudieron filtrar las clases',
        duration: 3
      })
    }
  } finally {
    loading.value = false
  }
}

const limpiarFiltros = () => {
  filtroTipo.value = null
  filtroHorario.value = null
  loadClases()
}

const resetForm = () => {
  form.value = {
    instructor: '',
    apellidoInstructor: '',
    horario: null,
    tipo: ''
  }
  editingClase.value = null
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const saveClase = async () => {
  try {
    await formRef.value.validate()
    
    const claseData = {
      instructor: form.value.instructor,
      apellidoInstructor: form.value.apellidoInstructor,
      horario: form.value.horario,
      tipo: form.value.tipo
    }
    
    if (editingClase.value) {
      await claseService.update(editingClase.value.idClase, claseData)
      notification.success({
        message: 'Clase actualizada',
        description: 'La clase se actualizó correctamente',
        duration: 3
      })
    } else {
      await claseService.create(claseData)
      notification.success({
        message: 'Clase creada',
        description: 'La clase se creó correctamente',
        duration: 3
      })
    }
    
    closeModal()
    loadClases()
  } catch (error) {
    if (error.response) {
      notification.error({
        message: 'Error del servidor',
        description: error.response?.data?.message || 'Error al guardar la clase',
        duration: 3
      })
    }
    console.error('Error al guardar:', error)
  }
}

const editClase = (clase) => {
  editingClase.value = clase
  form.value = {
    instructor: clase.instructor,
    apellidoInstructor: clase.apellidoInstructor,
    horario: clase.horario,
    tipo: clase.tipo
  }
  showModal.value = true
}

const deleteClase = async (id) => {
  try {
    await claseService.delete(id)
    notification.success({
      message: 'Clase eliminada',
      description: 'La clase fue eliminada correctamente',
      duration: 3
    })
    loadClases()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar la clase',
      duration: 3
    })
  }
}

const showAlumnosModal = async (clase) => {
  console.log('Abriendo modal de alumnos para clase:', clase)
  claseSeleccionada.value = clase
  alumnoSeleccionado.value = null
  showAlumnosModalVisible.value = true
  console.log('Modal visible:', showAlumnosModalVisible.value)
}

const closeAlumnosModal = () => {
  showAlumnosModalVisible.value = false
  claseSeleccionada.value = null
  alumnoSeleccionado.value = null
}

const addAlumnoToClase = async () => {
  if (!alumnoSeleccionado.value) return
  
  loadingAlumnos.value = true
  try {
    const claseActualizada = await claseService.addAlumno(
      claseSeleccionada.value.idClase,
      alumnoSeleccionado.value
    )
    
    claseSeleccionada.value = claseActualizada
    alumnoSeleccionado.value = null
    
    notification.success({
      message: 'Alumno agregado',
      description: 'El alumno fue agregado a la clase correctamente',
      duration: 2
    })
    
    loadClases()
  } catch (error) {
    console.error('Error al agregar alumno:', error)
    if (error.response) {
      notification.error({
        message: 'Error',
        description: 'No se pudo agregar el alumno a la clase',
        duration: 3
      })
    }
  } finally {
    loadingAlumnos.value = false
  }
}

const removeAlumnoFromClase = async (alumnoId) => {
  loadingAlumnos.value = true
  try {
    const claseActualizada = await claseService.removeAlumno(
      claseSeleccionada.value.idClase,
      alumnoId
    )
    
    claseSeleccionada.value = claseActualizada
    
    notification.success({
      message: 'Alumno removido',
      description: 'El alumno fue removido de la clase correctamente',
      duration: 2
    })
    
    loadClases()
  } catch (error) {
    console.error('Error al remover alumno:', error)
    if (error.response) {
      notification.error({
        message: 'Error',
        description: 'No se pudo remover el alumno de la clase',
        duration: 3
      })
    }
  } finally {
    loadingAlumnos.value = false
  }
}

onMounted(() => {
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
