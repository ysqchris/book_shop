<template>
  <div class="books-page">
    <!-- 搜索栏 -->
    <div class="search-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索图书标题、作者或出版社"
        size="large"
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
      
      <div class="filter-section">
        <el-select v-model="selectedCategory" placeholder="选择分类" @change="handleCategoryChange">
          <el-option label="全部" value=""></el-option>
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        
        <el-select v-model="sortBy" placeholder="排序方式" @change="handleSortChange">
          <el-option label="最新上架" value="newest"></el-option>
          <el-option label="价格从低到高" value="price_asc"></el-option>
          <el-option label="价格从高到低" value="price_desc"></el-option>
        </el-select>
      </div>
    </div>

    <!-- 图书列表 -->
    <div class="books-list">
      <el-row :gutter="20">
        <el-col :span="6" v-for="book in books" :key="book.id">
          <el-card class="book-card" shadow="hover" @click="$router.push(`/book/${book.id}`)">
            <div class="book-cover">
              <img :src="book.coverImage || '/default-book.jpg'" :alt="book.title" />
            </div>
            <div class="book-info">
              <h4 class="book-title">{{ book.title }}</h4>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-publisher">{{ book.publisher }}</p>
              <div class="book-price-section">
                <span class="current-price">¥{{ book.price }}</span>
                <span class="original-price">¥{{ book.originalPrice }}</span>
              </div>
              <div class="book-stock">库存: {{ book.stock }}本</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-section">
      <el-skeleton :rows="6" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getBooksApi, searchBooksApi } from '@/api/book'
import { getCategoriesApi } from '@/api/category'

const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('newest')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const books = ref([])
const categories = ref([])
const loading = ref(false)

// 获取图书列表
const fetchBooks = async () => {
  loading.value = true
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      categoryId: selectedCategory.value,
      sort: sortBy.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const response = await getBooksApi(params)
    if (response.code === 200) {
      books.value = response.data.books
      total.value = response.data.total
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategoriesApi()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
}

// 分类改变
const handleCategoryChange = () => {
  currentPage.value = 1
  fetchBooks()
}

// 排序改变
const handleSortChange = () => {
  fetchBooks()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchBooks()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchBooks()
}

// 监听分页和排序变化
watch([currentPage, sortBy], () => {
  fetchBooks()
})

onMounted(() => {
  fetchBooks()
  fetchCategories()
})
</script>

<style scoped>
.books-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-section {
  margin-bottom: 30px;
}

.search-input {
  width: 400px;
  margin-right: 20px;
}

.filter-section {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.books-list {
  margin-bottom: 30px;
}

.book-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.book-info {
  padding: 10px 0;
}

.book-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author,
.book-publisher {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.book-price-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 10px 0;
}

.current-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.book-stock {
  font-size: 12px;
  color: #67c23a;
}

.pagination-section {
  display: flex;
  justify-content: center;
}

.loading-section {
  margin-top: 20px;
}
</style>