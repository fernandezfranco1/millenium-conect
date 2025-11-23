import axios from 'axios'

const API_URL = '/api/alumnos'

export default {
  async getAll(page = 0, size = 10, sortBy = 'idAlumno', sortDir = 'asc') {
    const response = await axios.get(API_URL, {
      params: { page, size, sortBy, sortDir }
    })
    return response.data
  },
  
  async getAllWithoutPagination() {
    const response = await axios.get(`${API_URL}/all`)
    return response.data
  },
  
  async getById(id) {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
  },
  
  async create(alumno) {
    const response = await axios.post(API_URL, alumno)
    return response.data
  },
  
  async update(id, alumno) {
    const response = await axios.put(`${API_URL}/${id}`, alumno)
    return response.data
  },
  
  async delete(id) {
    await axios.delete(`${API_URL}/${id}`)
  },
  
  async buscar(termino) {
    const response = await axios.get(`${API_URL}/buscar`, { params: { termino } })
    return response.data
  },
  
  async getByCategoria(categoria) {
    const response = await axios.get(`${API_URL}/categoria/${categoria}`)
    return response.data
  },
  
  async searchAlumnos(searchTerm, page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/search`, {
      params: { q: searchTerm, page, size }
    })
    return response.data
  },
  
  async getClases(alumnoId) {
    const response = await axios.get(`${API_URL}/${alumnoId}/clases`)
    return response.data
  }
}
