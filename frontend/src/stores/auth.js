import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    username: localStorage.getItem('username') || null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  
  actions: {
    async login(username, password) {
      try {
        const response = await axios.post('/api/auth/login', { username, password })
        this.token = response.data.token
        this.username = response.data.username
        
        localStorage.setItem('token', this.token)
        localStorage.setItem('username', this.username)
        
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
        
        return { success: true }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || 'Error al iniciar sesión' 
        }
      }
    },
    
    async register(username, password) {
      try {
        const response = await axios.post('/api/auth/register', { username, password })
        return { 
          success: true, 
          message: response.data.message 
        }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || 'Error al registrar usuario' 
        }
      }
    },
    
    logout() {
      this.token = null
      this.username = null
      
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      
      delete axios.defaults.headers.common['Authorization']
    },
    
    initAuth() {
      if (this.token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
      }
    }
  }
})
