<template>
  <app-layout>
    <div class="p-3 sm:p-6">
      <a-page-header
        title="Gestión de Ventas"
        class="bg-white mb-4 sm:mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="middle" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            <span class="hidden sm:inline">Nueva Venta</span>
            <span class="sm:hidden">Nueva</span>
          </a-button>
        </template>
      </a-page-header>
      
      <a-card class="mb-4 sm:mb-6">
        <a-row :gutter="[16, 16]" align="middle">
          <a-col :xs="24" :sm="16">
            <a-range-picker
              v-model:value="rangoFechas"
              format="DD/MM/YYYY"
              placeholder="['Inicio', 'Fin']"
              style="width: 100%"
              @change="filtrarPorFechas"
            />
          </a-col>
          <a-col :xs="24" :sm="8">
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
            :data-source="ventas"
            :loading="loading"
            :row-key="record => record.idVenta"
            :scroll="{ x: 600 }"
          >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'producto'">
              {{ record.producto?.nombre }}
              <span v-if="record.producto?.marca" class="text-gray-500 text-sm">
                ({{ record.producto.marca }})
              </span>
            </template>
            <template v-else-if="column.key === 'total'">
              {{ formatCurrency(record.total) }}
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Editar">
                  <a-button type="primary" style="background-color: #52c41a" @click="editVenta(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar esta venta?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteVenta(record.idVenta)"
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
        
        <div class="mt-4 flex justify-end">
          <a-statistic 
            title="Total de Ventas" 
            :value="calcularTotalVentas()" 
            :precision="2"
            prefix="$"
            class="text-right"
          />
        </div>
      </a-card>
    </div>
    
    <!-- Modal -->
    <a-modal
      v-model:open="showModal"
      :title="editingVenta ? 'Editar Venta' : 'Nueva Venta'"
      width="600px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      :ok-button-props="{ disabled: isCantidadInvalida, loading: saving }"
      :cancel-button-props="{ disabled: saving }"
      @ok="saveVenta"
      @cancel="closeModal"
    >
      <a-form
        :model="form"
        layout="vertical"
        ref="formRef"
      >
        <a-form-item
          label="Producto"
          name="producto"
          :rules="[{ required: true, message: 'Debes seleccionar un producto' }]"
        >
          <a-select
            v-model:value="form.producto"
            show-search
            placeholder="Buscar producto..."
            :filter-option="false"
            :not-found-content="isSearching ? undefined : 'No se encontraron productos'"
            @search="searchProductos"
            :loading="isSearching"
            :options="productosOptions"
            @change="onProductoChange"
          >
            <template #notFoundContent v-if="isSearching">
              <a-spin size="small" />
            </template>
          </a-select>
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="Cantidad"
              name="cantidad"
              :rules="cantidadRules"
            >
              <a-input-number
                v-model:value="form.cantidad"
                :min="1"
                :disabled="!form.producto"
                style="width: 100%"
                @change="calcularTotal"
                placeholder="Seleccione un producto primero"
              />
            </a-form-item>
            <div v-if="stockDisponible !== null" class="text-sm text-gray-500 -mt-2">
              Stock disponible: {{ stockDisponible }}
            </div>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Precio Unitario"
              name="precioUnitario"
            >
              <a-input-number
                v-model:value="form.precioUnitario"
                :min="0"
                :step="0.01"
                :precision="2"
                style="width: 100%"
                :formatter="value => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                @change="calcularTotal"
                disabled
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item
          label="Total"
          name="total"
        >
          <a-input-number
            v-model:value="form.total"
            :min="0"
            :step="0.01"
            :precision="2"
            style="width: 100%"
            :formatter="value => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
            :parser="value => value.replace(/\$\s?|(,*)/g, '')"
            disabled
          />
        </a-form-item>
        
        <a-form-item
          label="Fecha de Venta"
          name="fechaVenta"
          :rules="[{ required: true, message: 'La fecha es obligatoria' }]"
        >
          <a-date-picker
            v-model:value="form.fechaVenta"
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
import ventaService from '@/services/ventaService'
import productoService from '@/services/productoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'

const ventas = ref([])
const showModal = ref(false)
const editingVenta = ref(null)
const error = ref('')
const loading = ref(false)
const formRef = ref()
const isSearching = ref(false)
const productosEncontrados = ref([])
const searchTimeout = ref(null)
const stockDisponible = ref(null)
const rangoFechas = ref(null)
const saving = ref(false)

const form = ref({
  producto: null,
  cantidad: 1,
  precioUnitario: 0,
  total: 0,
  fechaVenta: new Date().toISOString().split('T')[0]
})

const columns = [
  { title: 'Producto', key: 'producto' },
  { title: 'Cantidad', dataIndex: 'cantidad' },
  { title: 'Total', key: 'total' },
  { title: 'Fecha', dataIndex: 'fechaVenta' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const cantidadRules = [
  { required: true, message: 'La cantidad es obligatoria' },
  { type: 'number', min: 1, message: 'Debe ser mayor a 0' },
  { 
    validator: (rule, value) => {
      if (stockDisponible.value !== null && value && value > stockDisponible.value) {
        return Promise.reject(`La cantidad no puede superar el stock disponible (${stockDisponible.value})`);
      }
      return Promise.resolve();
    }
  }
]

const productosOptions = computed(() => {
  return productosEncontrados.value.map(producto => ({
    label: `${producto.nombre}${producto.marca ? ' - ' + producto.marca : ''} (Stock: ${producto.stock})`,
    value: producto.idProducto
  }))
})

const isCantidadInvalida = computed(() => {
  return stockDisponible.value !== null && 
         form.value.cantidad && 
         form.value.cantidad > stockDisponible.value
})

const formatCurrency = (value) => {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS'
  }).format(value)
}

const calcularTotalVentas = () => {
  return ventas.value.reduce((sum, venta) => sum + venta.total, 0)
}

const searchProductos = async (value) => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  if (!value || value.length < 2) {
    productosEncontrados.value = []
    return
  }
  
  searchTimeout.value = setTimeout(async () => {
    isSearching.value = true
    try {
      productosEncontrados.value = await productoService.buscar(value)
    } catch (error) {
      console.error('Error al buscar productos:', error)
      productosEncontrados.value = []
    } finally {
      isSearching.value = false
    }
  }, 500)
}

const onProductoChange = (productoId) => {
  const producto = productosEncontrados.value.find(p => p.idProducto === productoId)
  if (producto) {
    stockDisponible.value = producto.stock
    form.value.precioUnitario = producto.precioVenta
    form.value.cantidad = 1
    calcularTotal()
  }
}

const calcularTotal = () => {
  form.value.total = (form.value.cantidad || 0) * (form.value.precioUnitario || 0)
}

const loadVentas = async () => {
  loading.value = true
  try {
    ventas.value = await ventaService.getAll()
  } catch (error) {
    console.error('Error al cargar ventas:', error)
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
    // Convertir las fechas dayjs a formato YYYY-MM-DD
    const inicio = rangoFechas.value[0].format('YYYY-MM-DD')
    const fin = rangoFechas.value[1].format('YYYY-MM-DD')
    
    ventas.value = await ventaService.getByRangoFechas(inicio, fin)
  } catch (error) {
    console.error('Error al filtrar:', error)
    if (error.response) {
      notification.error({
        message: 'Error al filtrar',
        description: 'No se pudieron filtrar las ventas por fecha',
        duration: 3
      })
    }
  } finally {
    loading.value = false
  }
}

const limpiarFiltros = () => {
  rangoFechas.value = null
  loadVentas()
}

const resetForm = () => {
  form.value = {
    producto: null,
    cantidad: 1,
    precioUnitario: 0,
    total: 0,
    fechaVenta: new Date().toISOString().split('T')[0]
  }
  productosEncontrados.value = []
  stockDisponible.value = null
  error.value = ''
  editingVenta.value = null
}

const closeModal = () => {
  showModal.value = false
  saving.value = false
  resetForm()
}

const saveVenta = async () => {
  if (saving.value) return
  
  try {
    saving.value = true
    await formRef.value.validate()
    
    const ventaData = {
      producto: { idProducto: form.value.producto },
      cantidad: parseInt(form.value.cantidad),
      total: parseFloat(form.value.total),
      fechaVenta: form.value.fechaVenta
    }
    
    if (editingVenta.value) {
      await ventaService.update(editingVenta.value.idVenta, ventaData)
      notification.success({
        message: 'Venta actualizada',
        description: 'La venta se actualizó correctamente',
        duration: 3
      })
    } else {
      await ventaService.create(ventaData)
      notification.success({
        message: 'Venta registrada',
        description: 'La venta se registró correctamente y el stock fue actualizado',
        duration: 3
      })
    }
    
    closeModal()
    loadVentas()
  } catch (error) {
    // Solo mostrar toast si hay respuesta del servidor (error de API)
    if (error.response) {
      const errorMsg = error.response?.data?.message || error.response?.data || 'Error al guardar la venta'
      notification.error({
        message: 'Error del servidor',
        description: errorMsg,
        duration: 4
      })
    }
    console.error('Error al guardar:', error)
  } finally {
    saving.value = false
  }
}

const editVenta = (venta) => {
  editingVenta.value = venta
  form.value = {
    producto: venta.producto?.idProducto,
    cantidad: venta.cantidad,
    precioUnitario: venta.total / venta.cantidad,
    total: venta.total,
    fechaVenta: venta.fechaVenta
  }
  if (venta.producto) {
    productosEncontrados.value = [venta.producto]
    stockDisponible.value = venta.producto.stock
  }
  showModal.value = true
}

const deleteVenta = async (id) => {
  try {
    await ventaService.delete(id)
    notification.success({
      message: 'Venta eliminada',
      description: 'La venta fue eliminada y el stock fue restaurado',
      duration: 3
    })
    loadVentas()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar la venta',
      duration: 3
    })
  }
}

onMounted(() => {
  loadVentas()
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
