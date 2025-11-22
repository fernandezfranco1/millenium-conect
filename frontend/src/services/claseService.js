import axios from 'axios'

const API_URL = 'http://localhost:8080/api/clases'

export default {
  async getAll() {
    const response = await axios.get(API_URL)
    return response.data
  },

  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },

  async getByTipo(tipo) {
    const response = await axios.get(`${API_URL}/tipo/${tipo}`)
    return response.data
  },

  async getByHorario(horario) {
    const response = await axios.get(`${API_URL}/horario/${horario}`)
    return response.data
  },

  async filtrar(tipo, horario) {
    const params = new URLSearchParams()
    if (tipo) params.append('tipo', tipo)
    if (horario) params.append('horario', horario)
    
    const response = await axios.get(`${API_URL}/filtrar?${params.toString()}`)
    return response.data
  },

  async create(clase) {
    const response = await axios.post(API_URL, clase)
    return response.data
  },

  async update(id, clase) {
    const response = await axios.put(`${API_URL}/${id}`, clase)
    return response.data
  },

  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },

  async addAlumno(claseId, alumnoId) {
    const response = await axios.post(`${API_URL}/${claseId}/alumnos/${alumnoId}`)
    return response.data
  },

  async removeAlumno(claseId, alumnoId) {
    const response = await axios.delete(`${API_URL}/${claseId}/alumnos/${alumnoId}`)
    return response.data
  }
}
