<template>
  <div class="book-detail page-enter" v-loading="loading">
    <div class="detail-container" v-if="book">
      <div class="book-basic-info">
        <div class="book-cover-section">
          <BookCover :src="book.coverImage" :title="book.title" height="380px" />
        </div>

        <div class="book-info-section">
          <p class="eyebrow">三定圆梦书店</p>
          <h1 class="book-title">{{ book.title }}</h1>
          <div class="book-meta">
            <span>作者 {{ book.author }}</span>
            <span v-if="book.publisher">出版社 {{ book.publisher }}</span>
            <span v-if="book.publishDate">出版 {{ book.publishDate }}</span>
            <span v-if="book.isbn">ISBN {{ book.isbn }}</span>
          </div>

          <div class="price-section">
            <div class="current-price">
              <span class="price-label">现价</span>
              <span class="price-value">¥{{ book.price }}</span>
            </div>
            <div class="original-price" v-if="book.originalPrice">
              原价 ¥{{ book.originalPrice }}
              <span class="save">约省 {{ calculateDiscount(book.originalPrice, book.price) }}%</span>
            </div>
          </div>

          <div class="stock-section">
            <span>库存 {{ book.stock }} 本</span>
            <span v-if="book.stock < 5" class="low-stock">库存紧张</span>
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
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button
              size="large"
              plain
              :disabled="book.stock === 0"
              @click="buyNow"
            >
              立即购买
            </el-button>
          </div>

          <div class="trust-note">
            <p>面向高中生与家长：提交订单后联系店家线下付款，可电话或扫码加微信确认品相与发货。</p>
          </div>
        </div>
      </div>

      <div class="book-description">
        <h2>内容简介</h2>
        <div class="description-content">
          {{ book.description || '暂无描述信息' }}
        </div>
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
import { useCartStore } from '@/stores/cart'
import BookCover from '@/components/BookCover.vue'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()
const cartStore = useCartStore()

const book = ref(null)
const quantity = ref(1)
const loading = ref(false)

const calculateDiscount = (originalPrice, currentPrice) => {
  if (!originalPrice || !currentPrice) return 0
  return Math.round((1 - currentPrice / originalPrice) * 100)
}

const addToCart = () => {
  if (!book.value || book.value.stock === 0) return
  cartStore.addItem(book.value, quantity.value)
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  if (!book.value || book.value.stock === 0) return
  cartStore.setCheckoutItems([{ bookId: book.value.id, quantity: quantity.value }])
  router.push('/checkout')
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
  max-width: 980px;
  margin: 0 auto;
}

.detail-container {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 28px;
}

.book-basic-info {
  display: flex;
  gap: 36px;
  margin-bottom: 28px;
}

.book-cover-section {
  flex: 0 0 280px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--border);
}

.book-info-section {
  flex: 1;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  color: var(--accent);
  font-weight: 600;
}

.book-title {
  font-size: clamp(24px, 3vw, 32px);
  color: var(--ink);
  margin: 0 0 16px;
  line-height: 1.3;
}

.book-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 18px;
  color: var(--ink-soft);
  font-size: 14px;
}

.price-section {
  margin-bottom: 16px;
  padding: 16px 18px;
  background: var(--surface-raised);
  border-radius: 8px;
  border: 1px solid var(--border);
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 6px;
}

.price-label {
  color: var(--muted);
  font-size: 13px;
}

.price-value {
  font-size: 28px;
  color: var(--price);
  font-weight: 700;
}

.original-price {
  font-size: 13px;
  color: var(--muted);
}

.save {
  margin-left: 8px;
  color: var(--accent-deep);
}

.stock-section {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 18px;
  color: var(--ink-soft);
  font-size: 14px;
}

.low-stock {
  color: #b7791f;
  font-weight: 600;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.trust-note {
  padding: 14px 16px;
  border-radius: 8px;
  background: var(--surface-raised);
  border: 1px solid var(--border);
}

.trust-note p {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--ink-soft);
}

.book-description h2 {
  margin: 0 0 12px;
  font-size: 18px;
  color: var(--ink);
}

.description-content {
  line-height: 1.8;
  color: var(--ink-soft);
  font-size: 14px;
}

.book-not-found {
  text-align: center;
  padding: 80px 0;
}

@media (max-width: 768px) {
  .book-basic-info {
    flex-direction: column;
  }

  .book-cover-section {
    flex: none;
    width: min(260px, 100%);
    margin: 0 auto;
  }
}
</style>