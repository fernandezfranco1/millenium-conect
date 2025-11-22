<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-blue-700 p-4">
    <a-card class="w-full max-w-md" :bordered="false">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-2">Millenium Conect</h1>
        <p class="text-gray-600">Sistema de Gestión - Taekwondo</p>
      </div>
      
      <a-tabs v-model:activeKey="activeTab" centered>
        <a-tab-pane key="login" tab="Iniciar Sesión">
          <a-form
            :model="loginForm"
            @finish="handleLogin"
            layout="vertical"
            class="mt-4"
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
            
            <a-form-item>
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                :loading="loading"
                block
              >
                Iniciar Sesión
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
        
        <a-tab-pane key="register" tab="Registrarse">
          <a-form
            :model="registerForm"
            @finish="handleRegister"
            layout="vertical"
            class="mt-4"
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
                Registrarse
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('login')
const loading = ref(false)
const error = ref('')
const success = ref('')

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
  loading.value = true
  error.value = ''
  success.value = ''
  
  const result = await authStore.register(registerForm.value.username, registerForm.value.password)
  
  if (result.success) {
    success.value = 'Usuario registrado exitosamente. Ya puedes iniciar sesión.'
    registerForm.value = { username: '', password: '', confirmPassword: '' }
    setTimeout(() => {
      activeTab.value = 'login'
      success.value = ''
    }, 2000)
  } else {
    error.value = result.message
  }
  
  loading.value = false
}
</script>

<style scoped>
.ant-card {
  box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
}
</style>
