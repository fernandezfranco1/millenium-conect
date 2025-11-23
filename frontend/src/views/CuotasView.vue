<template>
  <app-layout>
    <div class="p-6">
      <a-page-header
        title="Gestión de Cuotas"
        class="bg-white mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="large" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            Nueva Cuota
          </a-button>
        </template>
      </a-page-header>
      
      <!-- Filtros -->
      <a-card class="mb-4" :bodyStyle="{ padding: '16px' }">
        <a-row :gutter="16" align="middle">
          <a-col :span="6">
            <a-select v-model:value="filtroEstado" placeholder="Filtrar por estado" allow-clear style="width: 100%">
              <a-select-option value="Pendiente">Pendiente</a-select-option>
              <a-select-option value="Pagado">Pagado</a-select-option>
            </a-select>
          </a-col>
          
          <a-col :span="6">
            <a-select v-model:value="filtroFormaPago" placeholder="Filtrar por forma de pago" allow-clear style="width: 100%">
              <a-select-option value="Débito">Débito</a-select-option>
              <a-select-option value="Efectivo">Efectivo</a-select-option>
              <a-select-option value="Transferencia">Transferencia</a-select-option>
              <a-select-option value="Crédito">Crédito</a-select-option>
            </a-select>
          </a-col>
          
          <a-col :span="8">
            <a-range-picker 
              v-model:value="rangoFechas" 
              format="DD/MM/YYYY"
              style="width: 100%"
            />
          </a-col>
          
          <a-col :span="4">
            <a-space>
              <a-button type="primary" @click="aplicarFiltros">
                <template #icon><SearchOutlined /></template>
                Buscar
              </a-button>
              <a-button @click="limpiarFiltros">
                Limpiar
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-card>
      
      <a-card>
        <a-table
          :columns="columns"
          :data-source="cuotas"
          :pagination="false"
          :loading="loading"
          :row-key="record => record.idCuota"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'alumno'">
              {{ record.alumno?.nombre }} {{ record.alumno?.apellido }}
            </template>
            <template v-else-if="column.key === 'mesPago'">
              {{ formatMesPago(record.mesPago) }}
            </template>
            <template v-else-if="column.key === 'monto'">
              {{ formatCurrency(record.monto) }}
            </template>
            <template v-else-if="column.key === 'estado'">
              <a-tag :color="record.estado === 'Pagado' ? 'green' : 'orange'">
                {{ record.estado }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Ver comprobante" v-if="record.comprobante">
                  <a-button type="primary" @click="viewComprobante(record.comprobante)">
                    <EyeOutlined />
                  </a-button>
                </a-tooltip>
                <a-tooltip title="Editar">
                  <a-button type="primary" style="background-color: #52c41a" @click="editCuota(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar esta cuota?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteCuota(record.idCuota)"
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
        
        <div class="mt-4 flex justify-between items-center">
          <div class="text-sm text-gray-700">
            Mostrando {{ (pagination.currentPage * pagination.size) + 1 }} a 
            {{ Math.min((pagination.currentPage + 1) * pagination.size, pagination.totalItems) }} 
            de {{ pagination.totalItems }} cuotas
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

    <!-- Visor de Imagen Oculto -->
    <div style="display: none;">
      <a-image
        :preview="{
          visible: previewVisible,
          onVisibleChange: (visible) => previewVisible = visible,
        }"
        :src="previewImage"
      />
    </div>
    
    <!-- Modal -->
    <a-modal
      v-model:open="showModal"
      :title="editingCuota ? 'Editar Cuota' : 'Nueva Cuota'"
      width="700px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      @ok="saveCuota"
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
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="Mes de Pago"
              name="mesPago"
              :rules="[{ required: true, message: 'El mes es obligatorio' }]"
            >
              <a-date-picker
                v-model:value="form.mesPago"
                picker="month"
                style="width: 100%"
                format="YYYY-MM"
                value-format="YYYY-MM"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Monto"
              name="monto"
              :rules="[{ required: true, message: 'El monto es obligatorio' }]"
            >
              <a-input-number
                v-model:value="form.monto"
                :min="0"
                :step="0.01"
                :precision="2"
                style="width: 100%"
                :formatter="value => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="Fecha de Pago"
              name="fechaPago"
              :rules="[{ required: true, message: 'La fecha es obligatoria' }]"
            >
              <a-date-picker
                v-model:value="form.fechaPago"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Forma de Pago"
              name="formaPago"
              :rules="[{ required: true, message: 'La forma de pago es obligatoria' }]"
            >
              <a-select v-model:value="form.formaPago">
                <a-select-option value="Débito">Débito</a-select-option>
                <a-select-option value="Efectivo">Efectivo</a-select-option>
                <a-select-option value="Transferencia">Transferencia</a-select-option>
                <a-select-option value="Crédito">Crédito</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item
          label="Estado"
          name="estado"
          :rules="[{ required: true, message: 'El estado es obligatorio' }]"
        >
          <a-select v-model:value="form.estado">
            <a-select-option value="Pendiente">Pendiente</a-select-option>
            <a-select-option value="Pagado">Pagado</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="Comprobante (opcional)">
          <a-upload
            :file-list="fileList"
            :before-upload="beforeUpload"
            @remove="handleRemoveFile"
            accept="image/*,application/pdf"
            :max-count="1"
          >
            <a-button>
              <upload-outlined /> Seleccionar Archivo
            </a-button>
          </a-upload>
          <div class="text-xs text-gray-500 mt-1">Formatos: PDF o imágenes (máx. 10MB)</div>
        </a-form-item>
        
        <a-alert v-if="error" :message="error" type="error" show-icon closable class="mb-4" @close="error = ''" />
      </a-form>
    </a-modal>

  </app-layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { notification } from 'ant-design-vue'
import cuotaService from '@/services/cuotaService'
import alumnoService from '@/services/alumnoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  UploadOutlined,
  EyeOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const cuotas = ref([])
const showModal = ref(false)
const previewVisible = ref(false)
const previewImage = ref('')
const editingCuota = ref(null)
const error = ref('')
const loading = ref(false)
const formRef = ref()
const isSearching = ref(false)
const alumnosEncontrados = ref([])
const searchTimeout = ref(null)
const fileList = ref([])

// Filtros
const filtroEstado = ref(null)
const filtroFormaPago = ref(null)
const rangoFechas = ref(null)

const pagination = ref({
  currentPage: 0,
  totalPages: 0,
  totalItems: 0,
  size: 10
})

const currentPage = computed({
  get: () => pagination.value.currentPage + 1,
  set: (val) => pagination.value.currentPage = val - 1
})

const form = ref({
  alumno: null,
  mesPago: null,
  monto: null,
  fechaPago: new Date().toISOString().split('T')[0],
  formaPago: '',
  estado: 'Pendiente',
  comprobante: ''
})

const columns = [
  { title: 'Alumno', key: 'alumno' },
  { title: 'Mes', key: 'mesPago' },
  { title: 'Monto', key: 'monto' },
  { title: 'Fecha Pago', dataIndex: 'fechaPago' },
  { title: 'Forma de Pago', dataIndex: 'formaPago' },
  { title: 'Estado', key: 'estado' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const alumnosOptions = computed(() => {
  return alumnosEncontrados.value.map(alumno => ({
    label: `${alumno.nombre} ${alumno.apellido} - DNI: ${alumno.dni}`,
    value: alumno.idAlumno
  }))
})

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

const beforeUpload = (file) => {
  fileList.value = [file]
  return false
}

const handleRemoveFile = () => {
  fileList.value = []
  form.value.comprobante = ''
}

const loadCuotas = async () => {
  loading.value = true
  try {
    // Construir parámetros de filtro
    const params = {
      page: pagination.value.currentPage,
      size: pagination.value.size,
      sort: 'fechaPago',
      direction: 'desc'
    }
    
    if (filtroEstado.value) {
      params.estado = filtroEstado.value
    }
    
    if (filtroFormaPago.value) {
      params.formaPago = filtroFormaPago.value
    }
    
    if (rangoFechas.value && rangoFechas.value.length === 2) {
      params.fechaInicio = dayjs(rangoFechas.value[0]).format('YYYY-MM-DD')
      params.fechaFin = dayjs(rangoFechas.value[1]).format('YYYY-MM-DD')
    }
    
    const response = await cuotaService.getAll(
      params.page,
      params.size,
      params.sort,
      params.direction,
      params.estado,
      params.formaPago,
      params.fechaInicio,
      params.fechaFin
    )
    cuotas.value = response.content
    pagination.value.totalPages = response.totalPages
    pagination.value.totalItems = response.totalItems
    pagination.value.currentPage = response.currentPage
  } catch (error) {
    console.error('Error al cargar cuotas:', error)
  } finally {
    loading.value = false
  }
}

const onPageChange = (page) => {
  pagination.value.currentPage = page - 1
  loadCuotas()
}

const onPageSizeChange = (current, size) => {
  pagination.value.size = size
  pagination.value.currentPage = 0
  loadCuotas()
}

const resetForm = () => {
  form.value = {
    alumno: null,
    mesPago: null,
    monto: null,
    fechaPago: new Date().toISOString().split('T')[0],
    formaPago: '',
    estado: 'Pendiente',
    comprobante: ''
  }
  alumnosEncontrados.value = []
  fileList.value = []
  error.value = ''
}

const closeModal = () => {
  showModal.value = false
  resetForm()
  editingCuota.value = null
}

const saveCuota = async () => {
  try {
    await formRef.value.validate()
    
    // Subir archivo si existe
    if (fileList.value.length > 0) {
      const response = await cuotaService.uploadComprobante(fileList.value[0])
      form.value.comprobante = response.filename
    }
    
    const cuotaData = {
      alumno: { idAlumno: form.value.alumno },
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
    
    
    if (editingCuota.value) {
      notification.success({
        message: 'Cuota actualizada',
        description: 'La cuota se actualizó correctamente',
        duration: 3
      })
    } else {
      notification.success({
        message: 'Cuota registrada',
        description: 'La cuota se registró correctamente',
        duration: 3
      })
    }
    
    closeModal()
    loadCuotas()
  } catch (error) {
    if (error.response) {
      notification.error({
        message: 'Error del servidor',
        description: error.response?.data?.error || 'Error al guardar la cuota',
        duration: 3
      })
    }
    console.error('Error al guardar:', error)
  }
}

const editCuota = (cuota) => {
  editingCuota.value = cuota
  form.value = {
    alumno: cuota.alumno?.idAlumno,
    mesPago: cuota.mesPago,
    monto: cuota.monto,
    fechaPago: cuota.fechaPago,
    formaPago: cuota.formaPago,
    estado: cuota.estado,
    comprobante: cuota.comprobante || ''
  }
  if (cuota.alumno) {
    alumnosEncontrados.value = [cuota.alumno]
  }
  showModal.value = true
}

const deleteCuota = async (id) => {
  try {
    await cuotaService.delete(id)
    notification.success({
      message: 'Cuota eliminada',
      description: 'La cuota fue eliminada correctamente',
      duration: 3
    })
    loadCuotas()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar la cuota',
      duration: 3
    })
  }
}

const viewComprobante = (filename) => {
  previewImage.value = getComprobanteUrl(filename)
  previewVisible.value = true
}

const getComprobanteUrl = (filename) => {
  return `http://localhost:8080/uploads/comprobantes/${filename}`
}

const aplicarFiltros = () => {
  pagination.value.currentPage = 0
  loadCuotas()
}

const limpiarFiltros = () => {
  filtroEstado.value = null
  filtroFormaPago.value = null
  rangoFechas.value = null
  pagination.value.currentPage = 0
  loadCuotas()
}

onMounted(() => {
  loadCuotas()
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
