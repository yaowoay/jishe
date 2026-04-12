import { createStore } from 'vuex'

export default createStore({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    userRole: localStorage.getItem('userRole') || null,
    profileCompleted: localStorage.getItem('profileCompleted') === 'true' || false
  },

  getters: {
    isAuthenticated: state => !!state.token,
    user: state => state.user,
    token: state => state.token,
    userRole: state => state.userRole,
    profileCompleted: state => state.profileCompleted
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

    SET_PROFILE_COMPLETED(state, completed) {
      state.profileCompleted = completed
      localStorage.setItem('profileCompleted', completed.toString())
    },

    LOGOUT(state) {
      state.user = null
      state.token = null
      state.userRole = null
      state.profileCompleted = false
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userId')
      localStorage.removeItem('profileCompleted')
    }
  },

  actions: {
    login({ commit }, { token, user }) {
      commit('SET_TOKEN', token)
      commit('SET_USER', user)
      commit('SET_USER_ROLE', user.role)
      commit('SET_PROFILE_COMPLETED', user.profileCompleted || false)
      localStorage.setItem('userId', user.userId)
    },

    logout({ commit }) {
      commit('LOGOUT')
    },

    setUser({ commit }, user) {
      commit('SET_USER', user)
    },

    updateProfileStatus({ commit }, completed) {
      commit('SET_PROFILE_COMPLETED', completed)
    }
  },

  modules: {
  }
})
