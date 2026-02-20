import { defineStore } from 'pinia'

export const usePlayerStore = defineStore('player', {
  state: () => ({
    currentSong: null as null | any
  }),
  actions: {
    play(song: any) {
      this.currentSong = song
    }
  }
})