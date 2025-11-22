import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/alumnos',
    name: 'Alumnos',
    component: () => import('@/views/AlumnosView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/asistencias',
    name: 'Asistencias',
    component: () => import('@/views/AsistenciasView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/cuotas',
    name: 'Cuotas',
    component: () => import('@/views/CuotasView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/productos',
    name: 'Productos',
    component: () => import('@/views/ProductosView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/ventas',
    name: 'Ventas',
    component: () => import('@/views/VentasView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/eventos',
    name: 'Eventos',
    component: () => import('@/views/EventosView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/clases',
    name: 'Clases',
    component: () => import('@/views/ClasesView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/torneo',
    name: 'Torneo',
    component: () => import('@/views/TorneoView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
