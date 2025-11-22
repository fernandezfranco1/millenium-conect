import axios from 'axios'

const API_URL = '/api/ventas'

export default {
  async getAll() {
    const response = await axios.get(API_URL)
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },
  
  async create(venta) {
    const response = await axios.post(API_URL, venta)
    return response.data
  },
  
  async update(id, venta) {
    const response = await axios.put(`${API_URL}/${id}`, venta)
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },
  
  async getByRangoFechas(inicio, fin) {
    const response = await axios.get(`${API_URL}/rango`, { params: { inicio, fin } })
    return response.data
  }
}
