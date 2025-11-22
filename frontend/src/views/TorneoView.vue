<template>
  <app-layout>
    <div class="p-6">
      <a-page-header
        title="Generador de Llaves de Torneo"
        class="bg-white mb-6 rounded-lg"
      />
      
      <a-row :gutter="16">
        <!-- Panel de selección de participantes -->
        <a-col :span="8">
          <a-card title="Configuración del Torneo" class="mb-4">
            <a-form layout="vertical">
              <a-form-item label="Nombre del Torneo">
                <a-input v-model:value="nombreTorneo" placeholder="Ej: Torneo Regional 2025" />
              </a-form-item>
              
              <a-form-item label="Categoría">
                <a-select v-model:value="categoriaSeleccionada" placeholder="Seleccionar categoría">
                  <a-select-option value="Infantil">Infantil</a-select-option>
                  <a-select-option value="Cadete">Cadete</a-select-option>
                  <a-select-option value="Juvenil">Juvenil</a-select-option>
                  <a-select-option value="Senior">Senior</a-select-option>
                </a-select>
              </a-form-item>
              
              <a-divider>Participantes ({{ participantes.length }})</a-divider>
              
              <a-form-item label="Buscar y agregar alumno">
                <a-select
                  v-model:value="alumnoSeleccionado"
                  show-search
                  placeholder="Buscar por nombre o DNI"
                  style="width: 100%"
                  :filter-option="false"
                  :not-found-content="isSearching ? 'Buscando...' : 'No se encontraron alumnos'"
                  :options="alumnosOptions"
                  @search="searchAlumnos"
                  @change="agregarParticipante"
                />
              </a-form-item>
              
              <a-list
                v-if="participantes.length"
                :data-source="participantes"
                size="small"
                bordered
              >
                <template #renderItem="{ item }">
                  <a-list-item>
                    <template #actions>
                      <a-button type="link" danger size="small" @click="quitarParticipante(item.idAlumno)">
                        <DeleteOutlined />
                      </a-button>
                    </template>
                    {{ item.nombre }} {{ item.apellido }}
                  </a-list-item>
                </template>
              </a-list>
              
              <a-empty v-else description="No hay participantes" class="my-4" />
              
              <a-button 
                type="primary" 
                size="large" 
                block 
                class="mt-4"
                :disabled="participantes.length < 2"
                @click="generarLlaves"
              >
                <template #icon><TrophyOutlined /></template>
                Generar Llaves de Torneo
              </a-button>
              
              <a-alert 
                v-if="participantes.length < 2" 
                message="Se necesitan al menos 2 participantes" 
                type="warning" 
                show-icon 
                class="mt-2"
              />
            </a-form>
          </a-card>
        </a-col>
        
        <!-- Panel de visualización del bracket -->
        <a-col :span="16">
          <a-card title="Bracket del Torneo">
            <div v-if="!bracketGenerado" class="text-center py-12">
              <TrophyOutlined style="font-size: 64px; color: #d9d9d9;" />
              <p class="text-gray-500 mt-4">Configure el torneo y genere las llaves para ver el bracket</p>
            </div>
            
            <div v-else>
              <div class="mb-4 flex justify-between items-center">
                <h3 class="text-xl font-semibold">{{ nombreTorneo || 'Torneo' }}</h3>
                <a-space>
                  <a-button @click="resetearTorneo">
                    <template #icon><ReloadOutlined /></template>
                    Reiniciar
                  </a-button>
                  <a-button type="primary" @click="imprimirBracket">
                    <template #icon><PrinterOutlined /></template>
                    Imprimir
                  </a-button>
                </a-space>
              </div>
              
              <!-- Bracket personalizado -->
              <div class="bracket-container">
                <div v-for="(ronda, rondaIndex) in bracket" :key="rondaIndex" class="bracket-round">
                  <div class="round-title">{{ getRoundName(rondaIndex) }}</div>
                  <div class="matches">
                    <div v-for="(match, matchIndex) in ronda" :key="matchIndex" class="match">
                      <div 
                        class="match-participant" 
                        :class="{ 
                          winner: match.winner === 1,
                          clickable: match.participant1 && match.participant2 && !match.winner,
                          disabled: !match.participant1 || match.participant2 === 'BYE'
                        }"
                        @click="seleccionarGanador(rondaIndex, matchIndex, 1)"
                      >
                        <span class="participant-name">{{ match.participant1 || 'TBD' }}</span>
                        <a-tag v-if="match.winner === 1" color="green" class="ml-2">Ganador</a-tag>
                      </div>
                      <div 
                        class="match-participant" 
                        :class="{ 
                          winner: match.winner === 2,
                          clickable: match.participant1 && match.participant2 && match.participant2 !== 'BYE' && !match.winner,
                          disabled: match.participant2 === 'BYE' || !match.participant2
                        }"
                        @click="seleccionarGanador(rondaIndex, matchIndex, 2)"
                      >
                        <span class="participant-name">{{ match.participant2 || 'BYE' }}</span>
                        <a-tag v-if="match.winner === 2" color="green" class="ml-2">Ganador</a-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- Mostrar campeón -->
              <div v-if="campeon" class="champion-announcement">
                <TrophyOutlined class="trophy-icon" />
                <h2 class="champion-title">¡Campeón del Torneo!</h2>
                <div class="champion-name">{{ campeon }}</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </app-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { notification } from 'ant-design-vue'
import alumnoService from '@/services/alumnoService'
import AppLayout from '@/components/AppLayout.vue'
import {
  DeleteOutlined,
  TrophyOutlined,
  ReloadOutlined,
  PrinterOutlined
} from '@ant-design/icons-vue'

const nombreTorneo = ref('')
const categoriaSeleccionada = ref(null)
const participantes = ref([])
const alumnoSeleccionado = ref(null)
const alumnosEncontrados = ref([])
const isSearching = ref(false)
const searchTimeout = ref(null)
const bracketGenerado = ref(false)
const bracket = ref([])
const campeon = ref(null)

const alumnosOptions = computed(() => {
  return alumnosEncontrados.value.map(alumno => ({
    label: `${alumno.nombre} ${alumno.apellido} - DNI: ${alumno.dni} (${alumno.categoria})`,
    value: alumno.idAlumno,
    alumno: alumno
  }))
})

const searchAlumnos = async (value) => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  if (!value || value.length < 2) {
    alumnosEncontrados.value = []
    return
  }
  
  searchTimeout.value = setTimeout(async () => {
    isSearching.value = true
    try {
      const response = await alumnoService.searchAlumnos(value, 0, 10)
      alumnosEncontrados.value = response.content
    } catch (error) {
      console.error('Error al buscar alumnos:', error)
      alumnosEncontrados.value = []
    } finally {
      isSearching.value = false
    }
  }, 500)
}

const agregarParticipante = () => {
  if (!alumnoSeleccionado.value) return
  
  const option = alumnosOptions.value.find(opt => opt.value === alumnoSeleccionado.value)
  if (!option) return
  
  const alumno = option.alumno
  
  if (participantes.value.find(p => p.idAlumno === alumno.idAlumno)) {
    notification.warning({
      message: 'Participante duplicado',
      description: 'Este alumno ya está en la lista de participantes',
      duration: 2
    })
    alumnoSeleccionado.value = null
    return
  }
  
  participantes.value.push(alumno)
  alumnoSeleccionado.value = null
  
  notification.success({
    message: 'Participante agregado',
    description: `${alumno.nombre} ${alumno.apellido} ha sido agregado al torneo`,
    duration: 2
  })
}

const quitarParticipante = (idAlumno) => {
  participantes.value = participantes.value.filter(p => p.idAlumno !== idAlumno)
}

const generarLlaves = () => {
  if (participantes.value.length < 2) {
    notification.error({
      message: 'Participantes insuficientes',
      description: 'Se necesitan al menos 2 participantes para generar el torneo',
      duration: 3
    })
    return
  }
  
  // Mezclar participantes aleatoriamente
  const shuffled = [...participantes.value].sort(() => Math.random() - 0.5)
  
  // Calcular siguiente potencia de 2
  const nextPowerOf2 = Math.pow(2, Math.ceil(Math.log2(shuffled.length)))
  
  // Rellenar con BYEs si es necesario
  while (shuffled.length < nextPowerOf2) {
    shuffled.push(null)
  }
  
  // Crear primera ronda
  const firstRound = []
  for (let i = 0; i < shuffled.length; i += 2) {
    firstRound.push({
      participant1: shuffled[i] ? `${shuffled[i].nombre} ${shuffled[i].apellido}` : null,
      participant2: shuffled[i + 1] ? `${shuffled[i + 1].nombre} ${shuffled[i + 1].apellido}` : null,
      score1: null,
      score2: null,
      winner: null
    })
  }
  
  // Crear rondas subsiguientes
  bracket.value = [firstRound]
  let currentRound = firstRound
  
  while (currentRound.length > 1) {
    const nextRound = []
    for (let i = 0; i < currentRound.length; i += 2) {
      nextRound.push({
        participant1: null,
        participant2: null,
        score1: null,
        score2: null,
        winner: null
      })
    }
    bracket.value.push(nextRound)
    currentRound = nextRound
  }
  
  bracketGenerado.value = true
  
  notification.success({
    message: 'Llaves generadas',
    description: 'El bracket del torneo ha sido generado exitosamente',
    duration: 3
  })
}

const getRoundName = (index) => {
  const totalRounds = bracket.value.length
  const roundsFromEnd = totalRounds - index
  
  if (roundsFromEnd === 1) return 'Final'
  if (roundsFromEnd === 2) return 'Semifinal'
  if (roundsFromEnd === 3) return 'Cuartos de Final'
  return `Ronda ${index + 1}`
}

const seleccionarGanador = (rondaIndex, matchIndex, participante) => {
  const match = bracket.value[rondaIndex][matchIndex]
  
  // No permitir selección si ya hay ganador o si no hay ambos participantes
  if (match.winner) return
  if (!match.participant1 || !match.participant2) return
  if (match.participant2 === 'BYE') {
    // Auto-avanzar si es BYE
    avanzarGanador(rondaIndex, matchIndex, match.participant1)
    return
  }
  
  // Marcar ganador
  match.winner = participante
  
  // Obtener el nombre del ganador
  const ganador = participante === 1 ? match.participant1 : match.participant2
  
  // Avanzar a la siguiente ronda
  avanzarGanador(rondaIndex, matchIndex, ganador)
  
  notification.success({
    message: 'Ganador seleccionado',
    description: `${ganador} avanza a la siguiente ronda`,
    duration: 2
  })
}

const avanzarGanador = (rondaIndex, matchIndex, ganador) => {
  // Si es la última ronda, es el campeón
  if (rondaIndex === bracket.value.length - 1) {
    campeon.value = ganador
    notification.success({
      message: '¡Tenemos campeón!',
      description: `${ganador} es el ganador del torneo`,
      duration: 5
    })
    return
  }
  
  // Calcular posición en la siguiente ronda
  const siguienteRonda = rondaIndex + 1
  const siguienteMatchIndex = Math.floor(matchIndex / 2)
  const posicionEnMatch = matchIndex % 2 === 0 ? 1 : 2
  
  // Asignar ganador a la siguiente ronda
  if (posicionEnMatch === 1) {
    bracket.value[siguienteRonda][siguienteMatchIndex].participant1 = ganador
  } else {
    bracket.value[siguienteRonda][siguienteMatchIndex].participant2 = ganador
  }
}

const resetearTorneo = () => {
  bracketGenerado.value = false
  bracket.value = []
  campeon.value = null
}

const imprimirBracket = () => {
  window.print()
}
</script>

<style scoped>
.bracket-container {
  display: flex;
  gap: 40px;
  overflow-x: auto;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  min-height: 400px;
}

.bracket-round {
  display: flex;
  flex-direction: column;
  min-width: 200px;
}

.round-title {
  font-weight: 600;
  font-size: 16px;
  color: #1890ff;
  margin-bottom: 16px;
  text-align: center;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.matches {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  flex: 1;
}

.match {
  background: white;
  border-radius: 4px;
  border: 2px solid #d9d9d9;
  margin-bottom: 20px;
  overflow: hidden;
}

.match-participant {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
  position: relative;
}

.match-participant:last-child {
  border-bottom: none;
}

.match-participant.clickable {
  cursor: pointer;
}

.match-participant.clickable:hover {
  background: #f0f7ff;
  transform: translateX(2px);
}

.match-participant.winner {
  background: #f6ffed;
  border-left: 4px solid #52c41a;
  font-weight: 600;
}

.match-participant.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.participant-name {
  flex: 1;
  font-size: 14px;
  color: #262626;
}

.participant-score {
  font-weight: 600;
  font-size: 16px;
  color: #1890ff;
  min-width: 30px;
  text-align: center;
}

.champion-announcement {
  margin-top: 40px;
  padding: 40px;
  text-align: center;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);
  animation: celebration 0.6s ease-in-out;
}

@keyframes celebration {
  0% { transform: scale(0.9); opacity: 0; }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); opacity: 1; }
}

.trophy-icon {
  font-size: 64px;
  color: #d48806;
  margin-bottom: 16px;
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.champion-title {
  font-size: 28px;
  font-weight: bold;
  color: #ad6800;
  margin-bottom: 16px;
}

.champion-name {
  font-size: 36px;
  font-weight: bold;
  color: #873800;
  text-transform: uppercase;
  letter-spacing: 2px;
}

/* Estilos para impresión */
@media print {
  .ant-page-header,
  .ant-col:first-child,
  button {
    display: none !important;
  }
  
  .bracket-container {
    background: white;
  }
  
  .ant-col {
    width: 100% !important;
  }
}
</style>
