import axios from 'axios'

const API_URL = '/api/asistencias'

export default {
  async getAll() {
    const response = await axios.get(API_URL)
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },
  
  async create(asistencia) {
    const response = await axios.post(API_URL, asistencia)
    return response.data
  },
  
  async update(id, asistencia) {
    const response = await axios.put(`${API_URL}/${id}`, asistencia)
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },
  
  async getByFecha(fecha) {
    const response = await axios.get(`${API_URL}/fecha`, { params: { fecha } })
    return response.data
  },
  
  async getByRangoFechas(inicio, fin) {
    const response = await axios.get(`${API_URL}/rango`, { params: { inicio, fin } })
    return response.data
  }
}
