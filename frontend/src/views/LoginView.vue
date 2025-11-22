<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-blue-700 p-4">
    <div class="bg-white rounded-lg shadow-2xl p-8 w-full max-w-md">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-2">Millenium Conect</h1>
        <p class="text-gray-600">Sistema de Gestión - Taekwondo</p>
      </div>
      
      <!-- Tabs -->
      <div class="flex mb-6 border-b">
        <button
          @click="activeTab = 'login'"
          :class="activeTab === 'login' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'"
          class="flex-1 py-2 font-medium transition"
        >
          Iniciar Sesión
        </button>
        <button
          @click="activeTab = 'register'"
          :class="activeTab === 'register' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'"
          class="flex-1 py-2 font-medium transition"
        >
          Registrarse
        </button>
      </div>
      
      <!-- Login Form -->
      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block text-gray-700 font-medium mb-2">Usuario</label>
          <input
            v-model="loginForm.username"
            type="text"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Ingresa tu usuario"
          />
        </div>
        
        <div>
          <label class="block text-gray-700 font-medium mb-2">Contraseña</label>
          <input
            v-model="loginForm.password"
            type="password"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Ingresa tu contraseña"
          />
        </div>
        
        <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {{ error }}
        </div>
        
        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50"
        >
          {{ loading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
        </button>
      </form>
      
      <!-- Register Form -->
      <form v-if="activeTab === 'register'" @submit.prevent="handleRegister" class="space-y-6">
        <div>
          <label class="block text-gray-700 font-medium mb-2">Usuario</label>
          <input
            v-model="registerForm.username"
            type="text"
            required
            minlength="3"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Elige un nombre de usuario"
          />
        </div>
        
        <div>
          <label class="block text-gray-700 font-medium mb-2">Contraseña</label>
          <input
            v-model="registerForm.password"
            type="password"
            required
            minlength="6"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Mínimo 6 caracteres"
          />
        </div>
        
        <div>
          <label class="block text-gray-700 font-medium mb-2">Confirmar Contraseña</label>
          <input
            v-model="registerForm.confirmPassword"
            type="password"
            required
            minlength="6"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Confirma tu contraseña"
          />
        </div>
        
        <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {{ error }}
        </div>
        
        <div v-if="success" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
          {{ success }}
        </div>
        
        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50"
        >
          {{ loading ? 'Registrando...' : 'Registrarse' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

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
  
  // Validar que las contraseñas coincidan
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    error.value = 'Las contraseñas no coinciden'
    loading.value = false
    return
  }
  
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
