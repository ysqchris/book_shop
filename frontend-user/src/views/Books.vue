<template>
  <div class="books-page">
    <div class="stats-banner">
      <div class="stat-item" v-for="item in statItems" :key="item.label">
        <strong>{{ item.value }}</strong>
        <span>{{ item.label }}</span>
      </div>
    </div>

    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索书名、作者或出版社"
        size="default"
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>

      <el-select v-model="sortBy" size="default" style="width: 140px" @change="handleSortChange">
        <el-option label="最新上架" value="newest" />
        <el-option label="价格升序" value="price_asc" />
        <el-option label="价格降序" value="price_desc" />
      </el-select>
    </div>

    <div class="category-bar">
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
        :class="{ active: selectedCategory === category.id }"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </button>
    </div>

    <div class="books-list" v-loading="loading">
      <el-empty v-if="!loading && books.length === 0" description="暂无符合条件的图书" :image-size="80" />
      <div v-else class="books-grid">
        <div
          v-for="book in books"
          :key="book.id"
          class="book-card"
          @click="$router.push(`/book/${book.id}`)"
        >
          <BookCover :src="book.coverImage" :title="book.title" height="150px" />
          <div class="book-info">
            <h4 class="book-title">{{ book.title }}</h4>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-meta-row">
              <span class="current-price">¥{{ book.price }}</span>
              <span class="book-stock">库存 {{ book.stock }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-section">
      <el-pagination
        small
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
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getBooksApi } from '@/api/book'
import { getCategoriesApi } from '@/api/category'
import { getStatsApi } from '@/api/stats'
import BookCover from '@/components/BookCover.vue'

const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('newest')
const currentPage = ref(1)
const pageSize = ref(24)
const total = ref(0)
const books = ref([])
const categories = ref([])
const loading = ref(false)
const stats = ref({
  bookCount: 0,
  onSaleBookCount: 0,
  soldCount: 0,
  categoryCount: 0,
  userCount: 0
})

const statItems = computed(() => [
  { label: '在售图书', value: stats.value.onSaleBookCount || stats.value.bookCount || 0 },
  { label: '累计卖出', value: stats.value.soldCount || 0 },
  { label: '图书分类', value: stats.value.categoryCount || 0 }
])

const fetchBooks = async () => {
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
      books.value = Array.isArray(data) ? data : (data?.list || data?.books || [])
      total.value = Array.isArray(data) ? data.length : (data?.total || 0)
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  const response = await getCategoriesApi()
  if (response.code === 200) {
    categories.value = response.data || []
  }
}

const fetchStats = async () => {
  try {
    const response = await getStatsApi()
    if (response.code === 200) {
      stats.value = { ...stats.value, ...response.data }
    }
  } catch (error) {
    console.error('获取统计失败:', error)
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
  fetchStats()
  fetchCategories()
  fetchBooks()
})
</script>

<style scoped>
.books-page {
  max-width: 1280px;
  margin: 0 auto;
}

.stats-banner {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  background: linear-gradient(90deg, #1f2a37 0%, #2f4b6e 100%);
  color: #fff;
}

.stat-item {
  text-align: center;
  min-width: 0;
}

.stat-item strong {
  display: block;
  font-size: 20px;
  line-height: 1.2;
}

.stat-item span {
  font-size: 12px;
  opacity: 0.85;
}

.toolbar {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 10px;
}

.search-input {
  flex: 1;
  max-width: 420px;
}

.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.category-chip {
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #606266;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  line-height: 1.4;
  cursor: pointer;
}

.category-chip:hover {
  border-color: #409eff;
  color: #409eff;
}

.category-chip.active {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

.books-list {
  min-height: 240px;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 10px;
}

.book-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}

.book-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.book-info {
  padding: 8px;
}

.book-title {
  margin: 0 0 4px;
  font-size: 13px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  margin: 0 0 6px;
  font-size: 12px;
  color: #909399;
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
  font-size: 14px;
  color: #f56c6c;
  font-weight: 700;
}

.book-stock {
  font-size: 11px;
  color: #67c23a;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 14px;
}

@media (max-width: 1100px) {
  .books-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .stats-banner {
    grid-template-columns: repeat(2, 1fr);
  }

  .books-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .toolbar {
    flex-wrap: wrap;
  }

  .search-input {
    max-width: none;
    width: 100%;
  }
}
</style>
