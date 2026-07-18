const api = require('../../utils/api')
const { resolveUrl } = require('../../utils/request')

const SORTS = [
  { label: '最新上架', value: 'newest' },
  { label: '价格升序', value: 'price_asc' },
  { label: '价格降序', value: 'price_desc' }
]

Page({
  data: {
    keyword: '',
    categories: [],
    selectedCategory: '',
    sortIndex: 0,
    sortLabels: SORTS.map((s) => s.label),
    books: [],
    page: 1,
    size: 20,
    total: 0,
    totalPages: 1,
    loading: false
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 })
    }
  },

  onLoad() {
    this.loadCategories()
    this.loadBooks()
  },

  onPullDownRefresh() {
    this.setData({ page: 1 })
    this.loadBooks().finally(() => wx.stopPullDownRefresh())
  },

  async loadCategories() {
    try {
      const res = await api.getCategories()
      this.setData({ categories: res.data || [] })
    } catch (e) {
      // toast already shown
    }
  },

  async loadBooks() {
    this.setData({ loading: true })
    try {
      const { page, size, keyword, selectedCategory, sortIndex } = this.data
      const params = {
        page,
        size,
        sort: SORTS[sortIndex].value
      }
      if (keyword) params.keyword = keyword
      if (selectedCategory !== '' && selectedCategory != null) {
        params.categoryId = selectedCategory
      }
      const res = await api.getBooks(params)
      const list = (res.data && res.data.list) || []
      const total = (res.data && res.data.total) || 0
      const books = list.map((book) => ({
        ...book,
        coverUrl: resolveUrl(book.coverImage)
      }))
      this.setData({
        books,
        total,
        totalPages: Math.max(1, Math.ceil(total / size))
      })
    } catch (e) {
      this.setData({ books: [], total: 0, totalPages: 1 })
    } finally {
      this.setData({ loading: false })
    }
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.setData({ page: 1 })
    this.loadBooks()
  },

  onSortChange(e) {
    this.setData({ sortIndex: Number(e.detail.value), page: 1 })
    this.loadBooks()
  },

  onSelectCategory(e) {
    const id = e.currentTarget.dataset.id
    this.setData({
      selectedCategory: id === '' || id == null ? '' : id,
      page: 1
    })
    this.loadBooks()
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/book-detail/book-detail?id=${id}` })
  },

  prevPage() {
    if (this.data.page <= 1) return
    this.setData({ page: this.data.page - 1 })
    this.loadBooks()
  },

  nextPage() {
    if (this.data.page >= this.data.totalPages) return
    this.setData({ page: this.data.page + 1 })
    this.loadBooks()
  }
})
