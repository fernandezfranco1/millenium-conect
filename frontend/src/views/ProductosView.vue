<template>
  <app-layout>
    <div class="p-6">
      <a-page-header
        title="Gestión de Productos"
        class="bg-white mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="large" @click="showModal = true; resetForm()">
            <template #icon><PlusOutlined /></template>
            Nuevo Producto
          </a-button>
        </template>
      </a-page-header>
      
      <a-card class="mb-6">
        <a-input-search
          v-model:value="searchTerm"
          placeholder="Buscar producto por nombre..."
          size="large"
          @search="buscarProductos"
          @input="buscarProductos"
        >
          <template #prefix>
            <SearchOutlined />
          </template>
        </a-input-search>
      </a-card>
      
      <a-card>
        <a-table
          :columns="columns"
          :data-source="productos"
          :loading="loading"
          :row-key="record => record.idProducto"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'precioVenta'">
              {{ formatCurrency(record.precioVenta) }}
            </template>
            <template v-else-if="column.key === 'stock'">
              <a-tag :color="record.stock > 10 ? 'green' : record.stock > 0 ? 'orange' : 'red'">
                {{ record.stock }} unidades
              </a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-space>
                <a-tooltip title="Editar">
                  <a-button type="primary" style="background-color: #52c41a" @click="editProducto(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>
                <a-popconfirm
                  title="¿Estás seguro de eliminar este producto?"
                  ok-text="Sí"
                  cancel-text="No"
                  @confirm="deleteProducto(record.idProducto)"
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
    
    <!-- Modal -->
    <a-modal
      v-model:open="showModal"
      :title="editingProducto ? 'Editar Producto' : 'Nuevo Producto'"
      width="700px"
      ok-text="Guardar"
      cancel-text="Cancelar"
      @ok="saveProducto"
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
              :rules="[{ required: true, message: 'El nombre es obligatorio' }]"
            >
              <a-input v-model:value="form.nombre" placeholder="Ej: Dobok" />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="Marca"
              name="marca"
            >
              <a-input v-model:value="form.marca" placeholder="Ej: Adidas" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item
              label="Color"
              name="color"
            >
              <a-input v-model:value="form.color" placeholder="Ej: Blanco" />
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item
              label="Stock"
              name="stock"
              :rules="[{ required: true, message: 'El stock es obligatorio' }]"
            >
              <a-input-number
                v-model:value="form.stock"
                :min="0"
                style="width: 100%"
                placeholder="0"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item
              label="Precio de Venta"
              name="precioVenta"
              :rules="[{ required: true, message: 'El precio es obligatorio' }]"
            >
              <a-input-number
                v-model:value="form.precioVenta"
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
        
        <a-alert v-if="error" :message="error" type="error" show-icon closable class="mb-4" @close="error = ''" />
      </a-form>
    </a-modal>
  </app-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { notification } from 'ant-design-vue'
import productoService from '@/services/productoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'

const productos = ref([])
const showModal = ref(false)
const editingProducto = ref(null)
const error = ref('')
const loading = ref(false)
const formRef = ref()
const searchTerm = ref('')
const searchTimeout = ref(null)

const form = ref({
  nombre: '',
  marca: '',
  color: '',
  stock: 0,
  precioVenta: 0
})

const columns = [
  { title: 'Nombre', dataIndex: 'nombre' },
  { title: 'Marca', dataIndex: 'marca' },
  { title: 'Color', dataIndex: 'color' },
  { title: 'Stock', key: 'stock' },
  { title: 'Precio', key: 'precioVenta' },
  { title: 'Acciones', key: 'actions', align: 'center' }
]

const formatCurrency = (value) => {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS'
  }).format(value)
}

const loadProductos = async () => {
  loading.value = true
  try {
    productos.value = await productoService.getAll()
  } catch (error) {
    console.error('Error al cargar productos:', error)
  } finally {
    loading.value = false
  }
}

const buscarProductos = async () => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  if (!searchTerm.value) {
    loadProductos()
    return
  }
  
  searchTimeout.value = setTimeout(async () => {
    loading.value = true
    try {
      productos.value = await productoService.buscar(searchTerm.value)
    } catch (error) {
      console.error('Error al buscar:', error)
    } finally {
      loading.value = false
    }
  }, 500)
}

const resetForm = () => {
  form.value = {
    nombre: '',
    marca: '',
    color: '',
    stock: 0,
    precioVenta: 0
  }
  error.value = ''
  editingProducto.value = null
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const saveProducto = async () => {
  try {
    await formRef.value.validate()
    
    const productoData = {
      nombre: form.value.nombre,
      marca: form.value.marca || null,
      color: form.value.color || null,
      stock: parseInt(form.value.stock),
      precioVenta: parseFloat(form.value.precioVenta)
    }
    
    if (editingProducto.value) {
      await productoService.update(editingProducto.value.idProducto, productoData)
      notification.success({
        message: 'Producto actualizado',
        description: 'El producto se actualizó correctamente',
        duration: 3
      })
    } else {
      await productoService.create(productoData)
      notification.success({
        message: 'Producto creado',
        description: 'El producto se creó correctamente',
        duration: 3
      })
    }
    
    closeModal()
    loadProductos()
  } catch (error) {
    if (error.response) {
      notification.error({
        message: 'Error del servidor',
        description: error.response?.data?.error || 'Error al guardar el producto',
        duration: 3
      })
    }
    console.error('Error al guardar:', error)
  }
}

const editProducto = (producto) => {
  editingProducto.value = producto
  form.value = { ...producto }
  showModal.value = true
}

const deleteProducto = async (id) => {
  try {
    await productoService.delete(id)
    notification.success({
      message: 'Producto eliminado',
      description: 'El producto fue eliminado correctamente',
      duration: 3
    })
    loadProductos()
  } catch (error) {
    console.error('Error al eliminar:', error)
    notification.error({
      message: 'Error',
      description: 'No se pudo eliminar el producto',
      duration: 3
    })
  }
}

onMounted(() => {
  loadProductos()
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
