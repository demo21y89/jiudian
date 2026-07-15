import { defineStore } from 'pinia'
import { computed } from 'vue'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || '',
    userId: localStorage.getItem('userId') || ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
    isProducer: (state) => state.role === 'PRODUCER',
    isConsumer: (state) => state.role === 'CONSUMER',
    isAdminOrProducer: (state) => state.role === 'ADMIN' || state.role === 'PRODUCER'
  },

  actions: {
    setUser(data) {
      this.token = data.token
      this.username = data.username
      this.role = data.role
      this.userId = data.userId
      localStorage.setItem('token', data.token)
      localStorage.setItem('username', data.username)
      localStorage.setItem('role', data.role)
      localStorage.setItem('userId', data.userId)
    },

    logout() {
      this.token = ''
      this.username = ''
      this.role = ''
      this.userId = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('userId')
    }
  }
})
