import { createStore } from 'vuex'

export default createStore({
  state: {
    user: JSON.parse(localStorage.getItem('user') || 'null'),  // ← 改这行
    token: localStorage.getItem('token') || null,
    userRole: localStorage.getItem('userRole') || null,
    profileCompleted: localStorage.getItem('profileCompleted') === 'true' || false
  },

  getters: {
    isAuthenticated: state => !!state.token,
    user: state => state.user,
    token: state => state.token,
    userRole: state => state.userRole,
    profileCompleted: state => state.profileCompleted,
    profileCompletionRate: state => {
      const profile = state.user || {}
      const fields = ['realName', 'phone', 'email', 'college', 'major', 'title', 'bio', 'researchArea', 'avatar']
      const filledCount = fields.filter(f => profile[f]).length
      return Math.round((filledCount / fields.length) * 100)
    }
  },

  mutations: {
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))  // ← 加这行
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
