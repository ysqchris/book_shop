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
    hasMore: true,
    loading: false,
    loadingMore: false,
    viewMode: 'text'
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 })
    }
  },

  onLoad() {
    try {
      const saved = wx.getStorageSync('books_view_mode')
      if (saved === 'text' || saved === 'cover') {
        this.setData({ viewMode: saved })
      }
    } catch (e) {
      // ignore
    }
    this.loadCategories()
    this.reloadBooks()
  },

  onPullDownRefresh() {
    this.reloadBooks().finally(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    this.loadMoreBooks()
  },

  async loadCategories() {
    try {
      const res = await api.getCategories()
      this.setData({ categories: res.data || [] })
    } catch (e) {
      // toast already shown
    }
  },

  reloadBooks() {
    this.setData({ page: 1, hasMore: true, books: [] })
    return this.loadBooks({ reset: true })
  },

  loadMoreBooks() {
    const { loading, loadingMore, hasMore, page } = this.data
    if (loading || loadingMore || !hasMore) return Promise.resolve()
    this.setData({ page: page + 1 })
    return this.loadBooks({ reset: false })
  },

  async loadBooks({ reset = true } = {}) {
    if (reset) {
      this.setData({ loading: true })
    } else {
      this.setData({ loadingMore: true })
    }

    try {
      const { page, size, keyword, selectedCategory, sortIndex, books } = this.data
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
      const mapped = list.map((book) => ({
        ...book,
        coverUrl: resolveUrl(book.coverImage)
      }))
      const nextBooks = reset ? mapped : books.concat(mapped)
      this.setData({
        books: nextBooks,
        total,
        hasMore: nextBooks.length < total && mapped.length > 0
      })
    } catch (e) {
      if (reset) {
        this.setData({ books: [], total: 0, hasMore: false })
      } else {
        this.setData({ page: Math.max(1, this.data.page - 1) })
      }
    } finally {
      this.setData({ loading: false, loadingMore: false })
    }
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onClearKeyword() {
    this.setData({ keyword: '' })
    this.reloadBooks()
  },

  onSearch() {
    this.reloadBooks()
  },

  onSortChange(e) {
    const sortIndex = Number(e.detail.value)
    if (sortIndex === this.data.sortIndex) return
    this.setData({ sortIndex })
    this.reloadBooks()
  },

  onViewModeTap(e) {
    const viewMode = e.currentTarget.dataset.mode
    if (!viewMode || viewMode === this.data.viewMode) return
    this.setData({ viewMode })
    try {
      wx.setStorageSync('books_view_mode', viewMode)
    } catch (err) {
      // ignore
    }
  },

  onSelectCategory(e) {
    const id = e.currentTarget.dataset.id
    this.setData({
      selectedCategory: id === '' || id == null ? '' : id
    })
    this.reloadBooks()
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/book-detail/book-detail?id=${id}` })
  }
})
