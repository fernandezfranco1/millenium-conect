import axios from 'axios'

const API_URL = '/api/eventos'

export default {
  async getAll() {
    const response = await axios.get(API_URL)
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },
  
  async create(evento) {
    const response = await axios.post(API_URL, evento)
    return response.data
  },
  
  async update(id, evento) {
    const response = await axios.put(`${API_URL}/${id}`, evento)
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },
  
  async getByTipo(tipo) {
    const response = await axios.get(`${API_URL}/tipo/${tipo}`)
    return response.data
  },
  
  async getByRangoFechas(inicio, fin) {
    const response = await axios.get(`${API_URL}/rango`, { params: { inicio, fin } })
    return response.data
  }
}
