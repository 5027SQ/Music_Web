import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as null | { id: number, username: string, email?: string },
  }),
  actions: {
    setUser(user) {
      this.user = user
    }
  }
})