// src/stores/ProblemsStore.js
import { ref } from 'vue'
import axios from 'axios'

export const useProblemsStore = () => {
  const problems = ref([])

  const fetchProblems = async () => {
    try {
      const response = await axios.get('/api/problems')
      problems.value = response.data
      return problems.value
    } catch (error) {
      console.error('Error fetching problems:', error)
    }
  }

  const fetchProblem = async (id) => {
    try {
      const response = await axios.get(`/api/problems/${id}`)
      return response.data
    } catch (error) {
      console.error('Error fetching problem:', error)
    }
  }

  return {
    problems,
    fetchProblems,
    fetchProblem
  }
}