import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/styles.css'
import { useAuthStore } from './stores/auth'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// Inicializar autenticación antes de montar la app
const authStore = useAuthStore()
authStore.initAuth()

app.use(router)
app.mount('#app')
