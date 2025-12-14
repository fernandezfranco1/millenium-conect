import axios from 'axios'

const API_URL = '/api/cuotas'

// Función para obtener headers con token
const getAuthHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export default {
  async getAll(page = 0, size = 10, sortBy = 'fechaPago', sortDir = 'desc', estado = null, formaPago = null, fechaInicio = null, fechaFin = null) {
    const params = { page, size, sortBy, sortDir }
    
    if (estado) params.estado = estado
    if (formaPago) params.formaPago = formaPago
    if (fechaInicio) params.fechaInicio = fechaInicio
    if (fechaFin) params.fechaFin = fechaFin
    
    const response = await axios.get(API_URL, { 
      params,
      headers: getAuthHeaders()
    })
    return response.data
  },
  
  async getAllNonPaginated() {
    const response = await axios.get(`${API_URL}/all`, {
      headers: getAuthHeaders()
    })
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`, {
      headers: getAuthHeaders()
    })
    return response.data
  },
  
  async create(cuota) {
    const response = await axios.post(API_URL, cuota, {
      headers: getAuthHeaders()
    })
    return response.data
  },
  
  async update(id, cuota) {
    const response = await axios.put(`${API_URL}/${id}`, cuota, {
      headers: getAuthHeaders()
    })
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`, {
      headers: getAuthHeaders()
    })
  },
  
  async uploadComprobante(file) {
    const formData = new FormData()
    formData.append('file', file)
    const response = await axios.post(`${API_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        ...getAuthHeaders()
      }
    })
    return response.data
  },
  
  async getByRangoFechas(inicio, fin) {
    const response = await axios.get(`${API_URL}/rango`, { params: { inicio, fin } })
    return response.data
  },
  
  async getByEstado(estado) {
    const response = await axios.get(`${API_URL}/estado/${estado}`)
    return response.data
  }
}
