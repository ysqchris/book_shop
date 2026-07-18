<template>
  <div class="books-page page-enter">
    <section class="intro" v-if="!isMobile">
      <h2>图书选购</h2>
      <p class="desc">按分类浏览，或搜索书名、作者和出版社</p>
    </section>

    <div class="toolbar" :class="{ 'toolbar--mobile': isMobile }">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索书名、作者或出版社"
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #append v-if="!isMobile">
          <el-button :icon="Search" @click="handleSearch">搜索</el-button>
        </template>
        <template #append v-else>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>

      <el-select
        v-model="sortBy"
        :style="isMobile ? { width: '112px' } : { width: '140px' }"
        @change="handleSortChange"
      >
        <el-option label="最新上架" value="newest" />
        <el-option label="价格升序" value="price_asc" />
        <el-option label="价格降序" value="price_desc" />
      </el-select>

      <el-radio-group v-if="!isMobile" v-model="viewModeStore.mode" class="view-mode" @change="viewModeStore.setMode">
        <el-radio-button label="cover">卡片</el-radio-button>
        <el-radio-button label="text">列表</el-radio-button>
      </el-radio-group>
    </div>

    <div class="category-bar">
      <div class="category-inner">
        <button
          class="category-chip"
          :class="{ active: selectedCategory === '' }"
          @click="selectCategory('')"
        >
          全部
        </button>
        <button
          v-for="category in categories"
          :key="category.id"
          class="category-chip"
          :class="{ active: String(selectedCategory) === String(category.id) }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </button>
      </div>
    </div>

    <div class="books-list" v-loading="loading && !books.length">
      <el-empty v-if="!loading && books.length === 0" description="暂无符合条件的图书" :image-size="80" />

      <div v-else-if="viewModeStore.mode === 'cover'" class="books-grid">
        <article
          v-for="book in books"
          :key="book.id"
          class="book-item"
          @click="$router.push(`/book/${book.id}`)"
        >
          <div class="cover-wrap">
            <BookCover :src="book.coverImage" :title="book.title" :height="isMobile ? '150px' : '170px'" />
          </div>
          <div class="book-info">
            <h4 class="book-title">{{ book.title }}</h4>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-meta-row">
              <span class="current-price">¥{{ book.price }}</span>
              <span class="book-stock">余 {{ book.stock }}</span>
            </div>
          </div>
        </article>
      </div>

      <div v-else class="books-text-list">
        <div
          v-for="book in books"
          :key="book.id"
          class="book-text-row"
          @click="$router.push(`/book/${book.id}`)"
        >
          <div class="text-main">
            <h4 class="book-title">{{ book.title }}</h4>
            <p class="book-sub">
              <span>{{ book.author }}</span>
              <span v-if="book.publisher"> · {{ book.publisher }}</span>
            </p>
          </div>
          <div class="text-side">
            <span class="current-price">¥{{ book.price }}</span>
            <span class="book-stock">余 {{ book.stock }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端分页 -->
    <div class="pagination-section" v-if="!isMobile">
      <el-pagination
        background
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[18, 24, 36, 48]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 移动端：加载更多按钮 -->
    <div class="load-more-section" v-if="isMobile && books.length > 0">
      <button
        class="load-more-btn"
        :disabled="loading || !hasMore"
        @click="loadMore"
      >
        {{ loading ? '加载中...' : (hasMore ? '加载更多' : '已加载全部') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getBooksApi } from '@/api/book'
import { getCategoriesApi } from '@/api/category'
import BookCover from '@/components/BookCover.vue'
import { useMobile } from '@/composables/useMobile'
import { useViewModeStore } from '@/stores/viewMode'

const VIEW_MODE_KEY = 'books_view_mode'

const { isMobile } = useMobile()
const viewModeStore = useViewModeStore()

const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('newest')
const viewMode = viewModeStore.mode
const currentPage = ref(1)
const pageSize = ref(24)
const total = ref(0)
const books = ref([])
const categories = ref([])
const loading = ref(false)
const hasMore = ref(false)

watch(viewModeStore.mode, (mode) => {
  localStorage.setItem(VIEW_MODE_KEY, mode)
})

const fetchBooks = async (append = false) => {
  if (append && loading.value) return
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      sort: sortBy.value
    }
    if (selectedCategory.value !== '' && selectedCategory.value != null) {
      params.categoryId = selectedCategory.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const response = await getBooksApi(params)
    if (response.code === 200) {
      const data = response.data
      const list = Array.isArray(data) ? data : (data?.list || data?.books || [])
      total.value = Array.isArray(data) ? data.length : (data?.total || 0)
      if (append) {
        books.value = [...books.value, ...list]
      } else {
        books.value = list
      }
      hasMore.value = books.value.length < total.value
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!hasMore.value || loading.value) return
  currentPage.value += 1
  fetchBooks(true)
}

const fetchCategories = async () => {
  const response = await getCategoriesApi()
  if (response.code === 200) {
    categories.value = response.data || []
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
}

const selectCategory = (id) => {
  selectedCategory.value = id
  currentPage.value = 1
  fetchBooks()
}

const handleSortChange = () => {
  currentPage.value = 1
  fetchBooks()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchBooks()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchBooks()
}

onMounted(() => {
  fetchCategories()
  fetchBooks()
})
</script>

<style scoped>
.books-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.intro {
  margin-bottom: 20px;
}

.intro h2 {
  margin: 0;
  font-size: 24px;
  line-height: 1.35;
  color: var(--ink);
}

.desc {
  margin: 6px 0 0;
  font-size: 14px;
  color: var(--muted);
}

.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  max-width: 420px;
  min-width: 220px;
}

.view-mode {
  margin-left: auto;
}

.category-bar {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.category-bar::-webkit-scrollbar {
  display: none;
}

.category-inner {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  min-width: max-content;
}

.category-chip {
  appearance: none;
  -webkit-appearance: none;
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--ink-soft);
  border-radius: var(--radius-sm);
  padding: 5px 11px;
  font-size: 13px;
  line-height: 1.4;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-chip:hover {
  border-color: var(--accent-line);
  color: var(--accent-deep);
  background: var(--accent-soft);
}

.category-chip.active,
.category-chip.active:hover {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
  font-weight: 600;
}

.books-list {
  flex: 1;
  min-height: 240px;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
}

.book-item {
  cursor: pointer;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.cover-wrap {
  overflow: hidden;
  background: #e2e6eb;
}

.book-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.book-info {
  padding: 12px;
}

.book-title {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  margin: 0 0 8px;
  font-size: 12px;
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}

.current-price {
  font-size: 15px;
  color: var(--price);
  font-weight: 700;
}

.book-stock {
  font-size: 11px;
  color: var(--muted);
}

.books-text-list {
  display: flex;
  flex-direction: column;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
}

.book-text-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid var(--border);
  transition: background 0.2s;
}

.book-text-row:last-child {
  border-bottom: none;
}

.book-text-row:hover {
  background: var(--accent-soft);
}

.text-main {
  min-width: 0;
  flex: 1;
}

.text-main .book-title {
  margin-bottom: 4px;
  font-size: 15px;
}

.book-sub {
  margin: 0;
  font-size: 12px;
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  flex-shrink: 0;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: auto;
  padding-top: 22px;
}

/* ===== 移动端加载更多按钮 ===== */
.load-more-section {
  margin-top: 18px;
  text-align: center;
}

.load-more-btn {
  appearance: none;
  -webkit-appearance: none;
  display: inline-block;
  width: 100%;
  padding: 11px 16px;
  font-size: 14px;
  color: var(--ink-soft);
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.load-more-btn:active:not(:disabled) {
  background: var(--accent-soft);
  border-color: var(--accent-line);
}

.load-more-btn:disabled {
  color: var(--muted);
  cursor: default;
}

@media (max-width: 1100px) {
  .books-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 800px) {
  .books-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  }

  .search-input {
    max-width: none;
    width: 100%;
  }

  .toolbar {
    gap: 8px;
  }

  /* 工具栏第一行：搜索框占满；第二行：排序+视图切换 */
  .search-input {
    order: 0;
    flex: 0 0 100%;
    max-width: 100%;
  }

  .view-mode {
    margin-left: auto;
  }

  /* 分类栏移动端：单行横向滚动，不换行（参考小程序 category-scroll） */
  .category-inner {
    flex-wrap: nowrap;
  }
}

/* ===== 移动端紧凑布局（参考小程序 books 页） ===== */
.toolbar--mobile {
  flex-wrap: nowrap;
  gap: 8px;
  margin-bottom: 10px;
}

.toolbar--mobile .search-input {
  flex: 1;
  min-width: 0;
  max-width: none;
  order: 0;
}

.toolbar--mobile .el-select {
  flex-shrink: 0;
  order: 1;
}

/* 移动端视图切换：紧凑吸右 */
.view-mode--mobile {
  flex-shrink: 0;
  margin-left: 0;
  order: 2;
}

.view-mode--mobile :deep(.el-radio-button__inner) {
  padding: 0 10px;
  height: 32px;
  line-height: 30px;
  font-size: 12px;
}

.category-bar {
  margin-bottom: 12px;
  padding-bottom: 0;
  border-bottom: none;
}

.is-mobile .category-bar {
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .books-page {
    /* 移动端减少整体留白 */
  }

  .toolbar {
    margin-bottom: 10px;
  }

  .category-bar {
    margin-bottom: 12px;
  }

  .category-chip {
    padding: 4px 10px;
    font-size: 12px;
  }

  .book-info {
    padding: 8px 10px 10px;
  }

  .book-title {
    font-size: 13px;
  }

  .book-author {
    font-size: 11px;
    margin-bottom: 6px;
  }

  .book-meta-row {
    gap: 4px;
  }

  .current-price {
    font-size: 14px;
  }

  .book-stock {
    font-size: 10px;
  }

  .load-more-btn {
    padding: 10px 14px;
    font-size: 13px;
  }
}
</style>