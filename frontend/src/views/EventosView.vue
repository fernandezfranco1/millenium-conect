<template>
  <app-layout>
    <div class="p-3 sm:p-6">
      <a-page-header
        title="Gestión de Eventos"
        class="bg-white mb-4 sm:mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="middle" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            <span class="hidden sm:inline">Nuevo Evento</span>
            <span class="sm:hidden">Nuevo</span>
          </a-button>
        </template>
      </a-page-header>
      
      <a-card class="mb-4 sm:mb-6">
        <a-row :gutter="[16, 16]" align="middle">
          <a-col :xs="24" :sm="12" :md="8">
            <a-select
              v-model:value="filtroTipo"
              placeholder="Filtrar por tipo"
              style="width: 100%"
              @change="filtrarPorTipo"
              allowClear
            >
              <a-select-option value="Curso">Curso</a-select-option>
              <a-select-option value="Examen">Examen</a-select-option>
              <a-select-option value="Torneo">Torneo</a-select-option>
            </a-select>
          </a-col>
          <a-col :xs="24" :sm="12" :md="10">
            <a-range-picker
              v-model:value="rangoFechas"
              format="DD/MM/YYYY"
              placeholder="['Inicio', 'Fin']"
              style="width: 100%"
              @change="filtrarPorFechas"
            />
          </a-col>
          <a-col :xs="24" :sm="24" :md="6">
            <a-button type="default" @click="limpiarFiltros" block>
              Limpiar filtros
            </a-button>
          </a-col>
        </a-row>
      </a-card>
      
      <a-card>
        <div class="overflow-x-auto">
          <a-table
            :columns="columns"
            :data-source="eventos"
            :loading="loading"
            :row-key="record => record.idEvento"
            :scroll="{ x: 600 }"
          >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'tipo'">
              <a-tag :color="getTipoColor(record.tipo)">
                {{ record.tipo }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Editar">
                  <a-button type="primary" style="background-color: #52c41a" @click="editEvento(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar este evento?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteEvento(record.idEvento)"
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
      :title="editingEvento ? 'Editar Evento' : 'Nuevo Evento'"
      width="600px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      @ok="saveEvento"
      @cancel="closeModal"
    >
      <a-form
        :model="form"
        layout="vertical"
        ref="formRef"
      >
        <a-form-item
          label="Nombre"
          name="nombre"
          :rules="[
            { required: true, message: 'El nombre es obligatorio' },
            { max: 150, message: 'El nombre no puede superar los 150 caracteres' }
          ]"
        >
          <a-input 
            v-model:value="form.nombre" 
            placeholder="Ej: Torneo Nacional 2025"
            :maxlength="150"
            show-count
          />
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="Fecha"
              name="fecha"
              :rules="[{ required: true, message: 'La fecha es obligatoria' }]"
            >
              <a-date-picker
                v-model:value="form.fecha"
                style="width: 100%"
                format="DD/MM/YYYY"
                value-format="YYYY-MM-DD"
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
                <a-select-option value="Curso">Curso</a-select-option>
                <a-select-option value="Examen">Examen</a-select-option>
                <a-select-option value="Torneo">Torneo</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </app-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { notification } from 'ant-design-vue'
import eventoService from '@/services/eventoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'

const eventos = ref([])
const showModal = ref(false)
const editingEvento = ref(null)
const loading = ref(false)
const formRef = ref()
const filtroTipo = ref(null)
const rangoFechas = ref(null)

const form = ref({
  nombre: '',
  fecha: null,
  tipo: ''
})

const columns = [
  { title: 'Nombre', dataIndex: 'nombre' },
  { title: 'Fecha', dataIndex: 'fecha' },
  { title: 'Tipo', key: 'tipo' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const getTipoColor = (tipo) => {
  const colors = {
    'Curso': 'blue',
    'Examen': 'orange',
    'Torneo': 'green'
  }
  return colors[tipo] || 'default'
}

const loadEventos = async () => {
  loading.value = true
  try {
    eventos.value = await eventoService.getAll()
  } catch (error) {
    console.error('Error al cargar eventos:', error)
  } finally {
    loading.value = false
  }
}

const filtrarPorTipo = async () => {
  if (!filtroTipo.value) {
    loadEventos()
    return
  }
  
  loading.value = true
  try {
    eventos.value = await eventoService.getByTipo(filtroTipo.value)
  } catch (error) {
    console.error('Error al filtrar:', error)
  } finally {
    loading.value = false
  }
}

const filtrarPorFechas = async () => {
  if (!rangoFechas.value || !rangoFechas.value[0] || !rangoFechas.value[1]) {
    return
  }
  
  loading.value = true
  try {
    const inicio = rangoFechas.value[0].format('YYYY-MM-DD')
    const fin = rangoFechas.value[1].format('YYYY-MM-DD')
    
    eventos.value = await eventoService.getByRangoFechas(inicio, fin)
  } catch (error) {
    console.error('Error al filtrar:', error)
    if (error.response) {
      notification.error({
        message: 'Error al filtrar',
        description: 'No se pudieron filtrar los eventos por fecha',
        duration: 3
      })
    }
  } finally {
    loading.value = false
  }
}

const limpiarFiltros = () => {
  filtroTipo.value = null
  rangoFechas.value = null
  loadEventos()
}

const resetForm = () => {
  form.value = {
    nombre: '',
    fecha: null,
    tipo: ''
  }
  editingEvento.value = null
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const saveEvento = async () => {
  try {
    await formRef.value.validate()
    
    const eventoData = {
      nombre: form.value.nombre,
      fecha: form.value.fecha,
      tipo: form.value.tipo
    }
    
    if (editingEvento.value) {
      await eventoService.update(editingEvento.value.idEvento, eventoData)
      notification.success({
        message: 'Evento actualizado',
        description: 'El evento se actualizó correctamente',
        duration: 3
      })
    } else {
      await eventoService.create(eventoData)
      notification.success({
        message: 'Evento creado',
        description: 'El evento se creó correctamente',
        duration: 3
      })
    }
    
    closeModal()
    loadEventos()
  } catch (error) {
    if (error.response) {
      notification.error({
        message: 'Error del servidor',
        description: error.response?.data?.message || 'Error al guardar el evento',
        duration: 3
      })
    }
    console.error('Error al guardar:', error)
  }
}

const editEvento = (evento) => {
  editingEvento.value = evento
  form.value = {
    nombre: evento.nombre,
    fecha: evento.fecha,
    tipo: evento.tipo
  }
  showModal.value = true
}

const deleteEvento = async (id) => {
  try {
    await eventoService.delete(id)
    notification.success({
      message: 'Evento eliminado',
      description: 'El evento fue eliminado correctamente',
      duration: 3
    })
    loadEventos()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar el evento',
      duration: 3
    })
  }
}

onMounted(() => {
  loadEventos()
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
