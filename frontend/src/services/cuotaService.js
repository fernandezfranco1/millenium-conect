import axios from 'axios'

const API_URL = '/api/cuotas'

export default {
  async getAll(page = 0, size = 10, sortBy = 'fechaPago', sortDir = 'desc') {
    const response = await axios.get(API_URL, {
      params: { page, size, sortBy, sortDir }
    })
    return response.data
  },
  
  async getAllNonPaginated() {
    const response = await axios.get(`${API_URL}/all`)
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },
  
  async create(cuota) {
    const response = await axios.post(API_URL, cuota)
    return response.data
  },
  
  async update(id, cuota) {
    const response = await axios.put(`${API_URL}/${id}`, cuota)
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },
  
  async uploadComprobante(file) {
    const formData = new FormData()
    formData.append('file', file)
    const response = await axios.post(`${API_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
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
