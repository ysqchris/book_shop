<template>
  <div class="cart-page">
    <div class="cart-header">
      <h1>购物车</h1>
      <p class="cart-summary">
        共 <span class="item-count">{{ cartItemCount }}</span> 件商品
      </p>
    </div>

    <div class="cart-content" v-if="hasCartItems">
      <!-- 购物车商品列表 -->
      <div class="cart-items">
        <div class="cart-item" v-for="item in cartItems" :key="item.id">
          <div class="item-checkbox">
            <el-checkbox v-model="item.selected" @change="updateTotal"></el-checkbox>
          </div>
          
          <div class="item-image">
            <el-image
              :src="item.bookCover || '/default-book.jpg'"
              :alt="item.bookTitle"
              fit="cover"
              class="book-image"
            />
          </div>
          
          <div class="item-info">
            <h4 class="book-title" @click="$router.push(`/book/${item.bookId}`)">
              {{ item.bookTitle }}
            </h4>
            <p class="book-author">{{ item.bookAuthor }}</p>
            <p class="book-publisher">{{ item.bookPublisher }}</p>
          </div>
          
          <div class="item-price">
            <span class="price">¥{{ item.bookPrice }}</span>
          </div>
          
          <div class="item-quantity">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="item.maxStock"
              size="small"
              @change="handleQuantityChange(item)"
            />
          </div>
          
          <div class="item-subtotal">
            <span class="subtotal">¥{{ (item.bookPrice * item.quantity).toFixed(2) }}</span>
          </div>
          
          <div class="item-actions">
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="removeItem(item.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 购物车结算 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">
            全选
          </el-checkbox>
          <el-button type="danger" :icon="Delete" @click="clearCart">
            清空购物车
          </el-button>
        </div>
        
        <div class="footer-right">
          <div class="total-info">
            <span class="total-label">已选 {{ selectedCount }} 件商品</span>
            <span class="total-amount">合计：¥{{ selectedTotalAmount.toFixed(2) }}</span>
          </div>
          <el-button
            type="primary"
            size="large"
            :disabled="selectedCount === 0"
            @click="checkout"
            class="checkout-btn"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空购物车 -->
    <div v-else class="empty-cart">
      <el-empty description="购物车空空如也">
        <template #image>
          <el-icon size="100"><ShoppingCart /></el-icon>
        </template>
        <el-button type="primary" @click="$router.push('/books')">
          去逛逛
        </el-button>
      </el-empty>
    </div>

    <!-- 推荐商品 -->
    <div class="recommendations">
      <h2>猜你喜欢</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="book in recommendedBooks" :key="book.id">
          <el-card class="recommend-book-card" shadow="hover">
            <div class="book-cover" @click="$router.push(`/book/${book.id}`)">
              <img :src="book.coverImage || '/default-book.jpg'" :alt="book.title" />
            </div>
            <div class="book-info">
              <h4 class="book-title">{{ book.title }}</h4>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-price">¥{{ book.price }}</p>
              <el-button
                type="primary"
                size="small"
                @click="addToCart(book.id)"
              >
                加入购物车
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, ShoppingCart } from '@element-plus/icons-vue'
import { useBookStore } from '@/stores/book'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const bookStore = useBookStore()
const userStore = useUserStore()

const selectAll = ref(false)
const recommendedBooks = ref([])

// 计算属性
const cartItems = computed(() => bookStore.cartItems)
const cartItemCount = computed(() => bookStore.cartItemCount)
const hasCartItems = computed(() => bookStore.hasCartItems)

const selectedItems = computed(() => {
  return cartItems.value.filter(item => item.selected)
})

const selectedCount = computed(() => selectedItems.value.length)

const selectedTotalAmount = computed(() => {
  return selectedItems.value.reduce((total, item) => {
    return total + (item.bookPrice * item.quantity)
  }, 0)
})

// 全选处理
const handleSelectAll = (checked) => {
  cartItems.value.forEach(item => {
    item.selected = checked
  })
  updateTotal()
}

// 更新总金额
const updateTotal = () => {
  // 自动计算，无需额外操作
}

// 数量改变处理
const handleQuantityChange = async (item) => {
  try {
    await bookStore.updateCartItem(item.id, item.quantity)
    ElMessage.success('数量更新成功')
  } catch (error) {
    ElMessage.error('数量更新失败')
  }
}

// 删除商品
const removeItem = async (itemId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      type: 'warning'
    })
    
    await bookStore.removeCartItem(itemId)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 清空购物车
const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      type: 'warning'
    })
    
    await bookStore.clearCart()
    ElMessage.success('购物车已清空')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 结算
const checkout = () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  const selectedBookIds = selectedItems.value.map(item => item.bookId)
  router.push({
    path: '/order/confirm',
    query: {
      bookIds: selectedBookIds.join(',')
    }
  })
}

// 添加到购物车
const addToCart = async (bookId) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await bookStore.addToCart(bookId, 1)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    ElMessage.error('添加到购物车失败')
  }
}

// 获取推荐图书
const fetchRecommendedBooks = async () => {
  try {
    // 模拟推荐数据
    recommendedBooks.value = [
      {
        id: 4,
        title: '活着',
        author: '余华',
        price: 18.00,
        coverImage: '/default-book.jpg'
      },
      {
        id: 5,
        title: '围城',
        author: '钱钟书',
        price: 25.00,
        coverImage: '/default-book.jpg'
      },
      {
        id: 6,
        title: '百年孤独',
        author: '加西亚·马尔克斯',
        price: 35.00,
        coverImage: '/default-book.jpg'
      },
      {
        id: 7,
        title: '平凡的世界',
        author: '路遥',
        price: 42.00,
        coverImage: '/default-book.jpg'
      }
    ]
  } catch (error) {
    console.error('获取推荐图书失败:', error)
  }
}

onMounted(async () => {
  if (userStore.isLoggedIn) {
    await bookStore.fetchCartList()
  }
  await fetchRecommendedBooks()
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cart-header {
  margin-bottom: 30px;
  text-align: center;
}

.cart-header h1 {
  color: #333;
  margin-bottom: 10px;
}

.cart-summary {
  color: #666;
  font-size: 16px;
}

.item-count {
  color: #f56c6c;
  font-weight: bold;
}

.cart-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 40px;
}

.cart-items {
  margin-bottom: 30px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 20px;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-checkbox {
  flex: 0 0 40px;
}

.item-image {
  flex: 0 0 80px;
}

.book-image {
  width: 80px;
  height: 100px;
  border-radius: 4px;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.book-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  cursor: pointer;
  color: #333;
}

.book-title:hover {
  color: #409eff;
}

.book-author,
.book-publisher {
  font-size: 14px;
  color: #666;
  margin-bottom: 3px;
}

.item-price {
  flex: 0 0 100px;
  text-align: center;
}

.price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.item-quantity {
  flex: 0 0 120px;
}

.item-subtotal {
  flex: 0 0 100px;
  text-align: center;
}

.subtotal {
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

.item-actions {
  flex: 0 0 80px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.total-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.total-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.total-amount {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.checkout-btn {
  width: 120px;
  height: 50px;
  font-size: 16px;
}

.empty-cart {
  text-align: center;
  padding: 100px 0;
  background: white;
  border-radius: 8px;
}

.recommendations {
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.recommendations h2 {
  margin-bottom: 20px;
  color: #333;
}

.recommend-book-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.recommend-book-card:hover {
  transform: translateY(-3px);
}

.recommend-book-card .book-cover img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.recommend-book-card .book-info {
  padding: 10px 0;
}

.recommend-book-card .book-title {
  font-size: 14px;
  margin-bottom: 5px;
}

.recommend-book-card .book-author {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
}

.recommend-book-card .book-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}
</style>