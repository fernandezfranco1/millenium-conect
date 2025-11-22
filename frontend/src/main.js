import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import axios from 'axios'
import App from './App.vue'
import router from './router'
import './assets/styles.css'
import { useAuthStore } from './stores/auth'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(Antd)

// Inicializar autenticación antes de montar la app
const authStore = useAuthStore()
authStore.initAuth()

app.use(router)
app.mount('#app')
