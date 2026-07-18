import { defineStore } from 'pinia'

const VIEW_MODE_KEY = 'books_view_mode'

export const useViewModeStore = defineStore('viewMode', {
  state: () => ({
    mode: localStorage.getItem(VIEW_MODE_KEY) === 'text' ? 'text' : 'cover'
  }),

  actions: {
    setMode(mode) {
      this.mode = mode
      localStorage.setItem(VIEW_MODE_KEY, mode)
    },
    toggle() {
      this.setMode(this.mode === 'cover' ? 'text' : 'cover')
    }
  }
})
