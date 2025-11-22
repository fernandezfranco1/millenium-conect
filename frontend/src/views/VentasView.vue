<template>
  <app-layout>
    <div class="p-6">
      <a-page-header
        title="Gestión de Ventas"
        sub-title="Próximamente con Ant Design"
        class="bg-white mb-6 rounded-lg"
      >
        <template #extra>
          <a-button type="primary" size="large" disabled>
            <template #icon><PlusOutlined /></template>
            Nueva Venta
          </a-button>
        </template>
      </a-page-header>
      
      <a-card>
        <a-empty description="Módulo en desarrollo" />
      </a-card>
    </div>
  </app-layout>
</template>

<script setup>
import AppLayout from '@/components/AppLayout.vue'
import { PlusOutlined } from '@ant-design/icons-vue'
</script>
