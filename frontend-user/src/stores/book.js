import { defineStore } from 'pinia'
import { getBooksApi, getBookDetailApi } from '@/api/book'

export const useBookStore = defineStore('book', {
  state: () => ({
    books: [],
    totalBooks: 0,
    currentPage: 1,
    pageSize: 12,
    currentBook: null,
    searchKeyword: '',
    selectedCategory: '',
    sortBy: 'newest'
  }),

  getters: {
    paginationInfo: (state) => ({
      currentPage: state.currentPage,
      pageSize: state.pageSize,
      total: state.totalBooks,
      totalPages: Math.ceil(state.totalBooks / state.pageSize)
    })
  },

  actions: {
    async fetchBooks(params = {}) {
      try {
        const response = await getBooksApi({
          page: this.currentPage,
          size: this.pageSize,
          categoryId: this.selectedCategory,
          sort: this.sortBy,
          keyword: this.searchKeyword,
          ...params
        })

        if (response.code === 200) {
          this.books = response.data.list || response.data.books || []
          this.totalBooks = response.data.total || 0
        }
        return response
      } catch (error) {
        console.error('获取图书列表失败:', error)
        throw error
      }
    },

    async fetchBookDetail(bookId) {
      try {
        const response = await getBookDetailApi(bookId)
        if (response.code === 200) {
          this.currentBook = response.data
        }
        return response
      } catch (error) {
        console.error('获取图书详情失败:', error)
        throw error
      }
    },

    updateSearchConditions(keyword, category, sort) {
      this.searchKeyword = keyword || ''
      this.selectedCategory = category || ''
      this.sortBy = sort || 'newest'
      this.currentPage = 1
    },

    resetSearchConditions() {
      this.searchKeyword = ''
      this.selectedCategory = ''
      this.sortBy = 'newest'
      this.currentPage = 1
    }
  }
})
