<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-800 p-4">
    <div class="w-full max-w-sm bg-gray-700 rounded-lg shadow-2xl p-8 border border-gray-600">
      <div class="text-center mb-5">
        <img src="/logo.png" alt="Millenium Conect" class="w-32 h-32 mx-auto mb-3 object-contain" />
        <h1 class="text-2xl font-bold text-white uppercase">Millenium Conect</h1>
      </div>
      
      <a-form
        :model="loginForm"
        @finish="handleLogin"
        layout="vertical"
      >
        <a-form-item
          label="Usuario"
          name="username"
          :rules="[{ required: true, message: 'Por favor ingresa tu usuario' }]"
        >
          <a-input
            v-model:value="loginForm.username"
            size="large"
            placeholder="Ingresa tu usuario"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>
        
        <a-form-item
          label="Contraseña"
          name="password"
          :rules="[{ required: true, message: 'Por favor ingresa tu contraseña' }]"
        >
          <a-input-password
            v-model:value="loginForm.password"
            size="large"
            placeholder="Ingresa tu contraseña"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>
        
        <a-alert
          v-if="error"
          :message="error"
          type="error"
          show-icon
          closable
          class="mb-4"
          @close="error = ''"
        />
        
        <a-alert
          v-if="success"
          :message="success"
          type="success"
          show-icon
          class="mb-4"
        />
        
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            block
          >
            Ingresar
          </a-button>
        </a-form-item>
      </a-form>
      
      <div class="text-center mt-4 pt-4 border-t border-gray-600">
        <span class="text-gray-300 text-sm">¿No tienes cuenta? </span>
        <a-button type="link" size="small" @click="showRegisterModal = true" class="p-0 text-blue-400 hover:text-blue-300">
          Crear cuenta
        </a-button>
      </div>
    </div>
    
    <!-- Modal de Registro -->
    <a-modal
      v-model:open="showRegisterModal"
      title="Crear Cuenta"
      ok-text="Registrarse"
      cancel-text="Cancelar"
      @ok="handleRegister"
      :confirm-loading="loading"
    >
      <a-form
        :model="registerForm"
        layout="vertical"
        ref="registerFormRef"
      >
        <a-form-item
          label="Usuario"
          name="username"
          :rules="[
            { required: true, message: 'Por favor ingresa un usuario' },
            { min: 3, message: 'Mínimo 3 caracteres' }
          ]"
        >
          <a-input
            v-model:value="registerForm.username"
            size="large"
            placeholder="Elige un nombre de usuario"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>
        
        <a-form-item
          label="Contraseña"
          name="password"
          :rules="[
            { required: true, message: 'Por favor ingresa una contraseña' },
            { min: 6, message: 'Mínimo 6 caracteres' }
          ]"
        >
          <a-input-password
            v-model:value="registerForm.password"
            size="large"
            placeholder="Mínimo 6 caracteres"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>
        
        <a-form-item
          label="Confirmar Contraseña"
          name="confirmPassword"
          :rules="[
            { required: true, message: 'Por favor confirma tu contraseña' },
            { validator: validatePassword }
          ]"
        >
          <a-input-password
            v-model:value="registerForm.confirmPassword"
            size="large"
            placeholder="Confirma tu contraseña"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>
        
        <a-alert
          v-if="registerError"
          :message="registerError"
          type="error"
          show-icon
          closable
          class="mb-0"
          @close="registerError = ''"
        />
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref('')
const success = ref('')
const registerError = ref('')
const showRegisterModal = ref(false)
const registerFormRef = ref()

const loginForm = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const validatePassword = async (_rule, value) => {
  if (value !== registerForm.value.password) {
    return Promise.reject('Las contraseñas no coinciden')
  }
  return Promise.resolve()
}

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  
  const result = await authStore.login(loginForm.value.username, loginForm.value.password)
  
  if (result.success) {
    router.push('/')
  } else {
    error.value = result.message
  }
  
  loading.value = false
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    
    loading.value = true
    registerError.value = ''
    
    const result = await authStore.register(registerForm.value.username, registerForm.value.password)
    
    if (result.success) {
      success.value = 'Usuario registrado exitosamente. Ya puedes iniciar sesión.'
      registerForm.value = { username: '', password: '', confirmPassword: '' }
      showRegisterModal.value = false
      setTimeout(() => {
        success.value = ''
      }, 3000)
    } else {
      registerError.value = result.message
    }
    
    loading.value = false
  } catch (error) {
    console.log('Validación fallida:', error)
  }
}
</script>

<style scoped>
/* Estilos personalizados para labels en fondo oscuro */
:deep(.ant-form-item-label > label) {
  color: #e5e7eb;
}
</style>
