import { createStore } from 'vuex'

export default createStore({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    userRole: localStorage.getItem('userRole') || null
  },

  getters: {
    isAuthenticated: state => !!state.token,
    user: state => state.user,
    token: state => state.token,
    userRole: state => state.userRole
  },

  mutations: {
    SET_USER(state, user) {
      state.user = user
    },

    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },

    SET_USER_ROLE(state, role) {
      state.userRole = role
      if (role) {
        localStorage.setItem('userRole', role)
      } else {
        localStorage.removeItem('userRole')
      }
    },

    LOGOUT(state) {
      state.user = null
      state.token = null
      state.userRole = null
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userId')
    }
  },

  actions: {
    login({ commit }, { token, user }) {
      commit('SET_TOKEN', token)
      commit('SET_USER', user)
      commit('SET_USER_ROLE', user.role)
      localStorage.setItem('userId', user.userId)
    },

    logout({ commit }) {
      commit('LOGOUT')
    },

    setUser({ commit }, user) {
      commit('SET_USER', user)
    }
  },

  modules: {
  }
})
