import axios from 'axios'

const API_URL = '/api/productos'

export default {
  async getAll() {
    const response = await axios.get(API_URL)
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },
  
  async create(producto) {
    const response = await axios.post(API_URL, producto)
    return response.data
  },
  
  async update(id, producto) {
    const response = await axios.put(`${API_URL}/${id}`, producto)
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },
  
  async buscar(nombre) {
    const response = await axios.get(`${API_URL}/buscar`, { params: { nombre } })
    return response.data
  }
}
