<template>
  <div class="book-detail" v-loading="loading">
    <div class="detail-container" v-if="book">
      <!-- 图书基本信息 -->
      <div class="book-basic-info">
        <div class="book-cover-section">
          <BookCover :src="book.coverImage" :title="book.title" height="360px" />
        </div>
        
        <div class="book-info-section">
          <h1 class="book-title">{{ book.title }}</h1>
          <div class="book-meta">
            <span class="author">作者：{{ book.author }}</span>
            <span class="publisher">出版社：{{ book.publisher }}</span>
            <span class="publish-date">出版日期：{{ book.publishDate }}</span>
            <span class="isbn">ISBN：{{ book.isbn }}</span>
          </div>
          
          <div class="price-section">
            <div class="current-price">
              <span class="price-label">现价：</span>
              <span class="price-value">¥{{ book.price }}</span>
            </div>
            <div class="original-price">
              <span class="price-label">原价：</span>
              <span class="price-value">¥{{ book.originalPrice }}</span>
            </div>
            <div class="discount">
              <el-tag type="success">
                节省 {{ calculateDiscount(book.originalPrice, book.price) }}%
              </el-tag>
            </div>
          </div>
          
          <div class="stock-section">
            <span class="stock-label">库存：</span>
            <span class="stock-value" :class="{ 'low-stock': book.stock < 5 }">
              {{ book.stock }}本
            </span>
            <el-tag v-if="book.stock < 5" type="warning" size="small">库存紧张</el-tag>
          </div>
          
          <div class="action-section">
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="book.stock"
              size="large"
              controls-position="right"
            />
            <el-button
              type="primary"
              size="large"
              :disabled="book.stock === 0"
              @click="addToCart"
              class="add-cart-btn"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button
              type="success"
              size="large"
              :disabled="book.stock === 0"
              @click="buyNow"
              class="buy-now-btn"
            >
              立即购买
            </el-button>
          </div>
          
          <div class="seller-info">
            <h3>卖家信息</h3>
            <p>卖家：{{ book.sellerName || '平台自营' }}</p>
            <p>信誉：⭐⭐⭐⭐⭐</p>
          </div>
        </div>
      </div>
      
      <!-- 图书描述 -->
      <div class="book-description">
        <h2>图书描述</h2>
        <div class="description-content">
          {{ book.description || '暂无描述信息' }}
        </div>
      </div>
      
      <!-- 用户评价 -->
      <div class="book-reviews">
        <h2>用户评价</h2>
        <div class="review-stats">
          <div class="rating-overview">
            <span class="average-rating">综合评分：4.8</span>
            <el-rate v-model="averageRating" disabled show-score />
          </div>
          <div class="rating-distribution">
            <div class="rating-item" v-for="rating in ratingDistribution" :key="rating.stars">
              <span>{{ rating.stars }}星</span>
              <el-progress :percentage="rating.percentage" :show-text="false" />
              <span>{{ rating.count }}人</span>
            </div>
          </div>
        </div>
        
        <div class="review-list">
          <div class="review-item" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <span class="reviewer">{{ review.reviewer }}</span>
              <el-rate v-model="review.rating" disabled size="small" />
              <span class="review-time">{{ formatTime(review.createTime) }}</span>
            </div>
            <div class="review-content">
              {{ review.content }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 相关推荐 -->
      <div class="related-books">
        <h2>相关推荐</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="relatedBook in relatedBooks" :key="relatedBook.id">
            <el-card class="related-book-card" shadow="hover" @click="$router.push(`/book/${relatedBook.id}`)">
              <BookCover :src="relatedBook.coverImage" :title="relatedBook.title" />
              <div class="book-info">
                <h4 class="book-title">{{ relatedBook.title }}</h4>
                <p class="book-author">{{ relatedBook.author }}</p>
                <p class="book-price">¥{{ relatedBook.price }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <!-- 图书不存在 -->
    <div v-else class="book-not-found">
      <el-result icon="error" title="图书不存在" sub-title="抱歉，您查找的图书不存在或已下架">
        <template #extra>
          <el-button type="primary" @click="$router.push('/books')">返回图书列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import { useBookStore } from '@/stores/book'
import { useUserStore } from '@/stores/user'
import BookCover from '@/components/BookCover.vue'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()
const userStore = useUserStore()

const book = ref(null)
const quantity = ref(1)
const loading = ref(false)
const averageRating = ref(4.8)

// 模拟评价数据
const ratingDistribution = ref([
  { stars: 5, percentage: 80, count: 160 },
  { stars: 4, percentage: 15, count: 30 },
  { stars: 3, percentage: 3, count: 6 },
  { stars: 2, percentage: 1, count: 2 },
  { stars: 1, percentage: 1, count: 2 }
])

const reviews = ref([
  {
    id: 1,
    reviewer: '读书爱好者',
    rating: 5,
    content: '书的质量很好，价格实惠，发货速度快，非常满意！',
    createTime: '2024-01-15'
  },
  {
    id: 2,
    reviewer: '文学迷',
    rating: 5,
    content: '正版图书，内容完整，品相很新，值得购买。',
    createTime: '2024-01-10'
  }
])

const relatedBooks = ref([
  {
    id: 2,
    title: '三体Ⅱ：黑暗森林',
    author: '刘慈欣',
    price: 28.00,
    coverImage: '/default-book.jpg'
  },
  {
    id: 3,
    title: '三体Ⅲ：死神永生',
    author: '刘慈欣',
    price: 32.00,
    coverImage: '/default-book.jpg'
  }
])

// 计算折扣
const calculateDiscount = (originalPrice, currentPrice) => {
  if (!originalPrice || !currentPrice) return 0
  return Math.round((1 - currentPrice / originalPrice) * 100)
}

// 格式化时间
const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleDateString()
}

// 添加到购物车
const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await bookStore.addToCart(book.value.id, quantity.value)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    ElMessage.error('添加到购物车失败')
  }
}

// 立即购买
const buyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 跳转到确认订单页面
  router.push({
    path: '/order/confirm',
    query: {
      bookId: book.value.id,
      quantity: quantity.value
    }
  })
}

// 获取图书详情
const fetchBookDetail = async () => {
  loading.value = true
  const bookId = route.params.id
  
  try {
    const response = await bookStore.fetchBookDetail(bookId)
    if (response.code === 200) {
      book.value = response.data
    } else {
      book.value = null
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
    book.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped>
.book-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.book-basic-info {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

.book-cover-section {
  flex: 0 0 300px;
}

.book-cover {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.book-info-section {
  flex: 1;
}

.book-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.book-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
  color: #666;
}

.price-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.current-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
  margin-bottom: 10px;
}

.stock-section {
  margin-bottom: 20px;
}

.stock-value.low-stock {
  color: #e6a23c;
  font-weight: bold;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.add-cart-btn,
.buy-now-btn {
  height: 40px;
  font-size: 16px;
}

.seller-info {
  padding: 20px;
  background: #f0f2f5;
  border-radius: 8px;
}

.book-description,
.book-reviews,
.related-books {
  margin-bottom: 40px;
}

.description-content {
  line-height: 1.6;
  color: #666;
}

.review-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.rating-overview {
  text-align: center;
}

.average-rating {
  display: block;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.rating-distribution {
  flex: 1;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.review-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.reviewer {
  font-weight: bold;
}

.review-time {
  color: #999;
  font-size: 12px;
}

.review-content {
  color: #666;
  line-height: 1.5;
}

.related-book-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.related-book-card:hover {
  transform: translateY(-3px);
}

.related-book-card .book-cover img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.related-book-card .book-info {
  padding: 10px 0;
}

.related-book-card .book-title {
  font-size: 14px;
  margin-bottom: 5px;
}

.related-book-card .book-author {
  font-size: 12px;
  color: #666;
}

.related-book-card .book-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.book-not-found {
  text-align: center;
  padding: 100px 0;
}
</style>