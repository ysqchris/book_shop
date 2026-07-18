<template>
  <div class="cart-page page-enter">
    <div class="cart-header">
      <h1>购物车</h1>
      <p class="cart-summary">
        共 <span class="item-count">{{ cartItemCount }}</span> 件 · 浏览下单无需登录
      </p>
    </div>

    <div class="cart-content" v-if="hasCartItems">
      <div class="cart-items">
        <div class="cart-item" v-for="item in cartItems" :key="item.id">
          <div class="item-checkbox">
            <el-checkbox v-model="item.selected" @change="persistSelection" />
          </div>

          <div class="item-image" @click="$router.push(`/book/${item.bookId}`)">
            <BookCover :src="item.bookCover" :title="item.bookTitle" height="100px" width="80px" />
          </div>

          <div class="item-info">
            <h4 class="book-title" @click="$router.push(`/book/${item.bookId}`)">
              {{ item.bookTitle }}
            </h4>
            <p class="book-author">{{ item.bookAuthor }}</p>
            <p class="book-publisher">{{ item.bookPublisher }}</p>
          </div>

          <div class="item-price">
            <span class="price">¥{{ Number(item.bookPrice).toFixed(2) }}</span>
          </div>

          <div class="item-quantity">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="item.maxStock || 99"
              size="small"
              @change="(val) => handleQuantityChange(item, val)"
            />
          </div>

          <div class="item-subtotal">
            <span class="subtotal">¥{{ (Number(item.bookPrice) * item.quantity).toFixed(2) }}</span>
          </div>

          <div class="item-actions">
            <el-button type="danger" size="small" :icon="Delete" @click="removeItem(item.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
          <el-button type="danger" :icon="Delete" @click="clearCart">清空购物车</el-button>
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
            class="checkout-btn"
            @click="checkout"
          >
            去下单
          </el-button>
        </div>
      </div>
    </div>

    <div v-else class="empty-cart">
      <el-empty description="购物车空空如也">
        <template #image>
          <el-icon size="100"><ShoppingCart /></el-icon>
        </template>
        <el-button type="primary" @click="$router.push('/books')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, ShoppingCart } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import BookCover from '@/components/BookCover.vue'

const router = useRouter()
const cartStore = useCartStore()
const selectAll = ref(false)

const cartItems = computed(() => cartStore.items)
const cartItemCount = computed(() => cartStore.itemCount)
const hasCartItems = computed(() => cartStore.hasItems)

const selectedItems = computed(() => cartItems.value.filter((item) => item.selected))
const selectedCount = computed(() => selectedItems.value.length)
const selectedTotalAmount = computed(() =>
  selectedItems.value.reduce((total, item) => total + Number(item.bookPrice) * item.quantity, 0)
)

watch(
  cartItems,
  (items) => {
    selectAll.value = items.length > 0 && items.every((item) => item.selected)
  },
  { deep: true, immediate: true }
)

const persistSelection = () => {
  cartStore.persist()
}

const handleSelectAll = (checked) => {
  cartItems.value.forEach((item) => {
    item.selected = checked
  })
  cartStore.persist()
}

const handleQuantityChange = (item, val) => {
  cartStore.updateQuantity(item.id, val)
}

const removeItem = async (itemId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', { type: 'warning' })
    cartStore.removeItem(itemId)
    ElMessage.success('删除成功')
  } catch {
    // cancel
  }
}

const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', { type: 'warning' })
    cartStore.clear()
    ElMessage.success('购物车已清空')
  } catch {
    // cancel
  }
}

const checkout = () => {
  if (!selectedCount.value) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  cartStore.setCheckoutItems(
    selectedItems.value.map((item) => ({
      bookId: item.bookId,
      quantity: item.quantity
    }))
  )
  router.push('/checkout')
}
</script>

<style scoped>
.cart-page {
  max-width: 980px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.cart-header {
  margin-bottom: 20px;
}

.cart-header h1 {
  color: var(--ink);
  margin: 0 0 8px;
  font-size: 26px;
}

.cart-summary {
  color: var(--muted);
  font-size: 14px;
  margin: 0;
}

.item-count {
  color: var(--accent);
  font-weight: 700;
}

.cart-content {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 16px;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-checkbox {
  flex: 0 0 40px;
}

.item-image {
  flex: 0 0 80px;
  cursor: pointer;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.book-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 6px;
  cursor: pointer;
  color: #333;
}

.book-title:hover {
  color: var(--accent);
}

.book-author,
.book-publisher {
  font-size: 13px;
  color: #666;
  margin: 0 0 3px;
}

.item-price,
.item-subtotal {
  flex: 0 0 90px;
  text-align: center;
}

.price,
.subtotal {
  font-size: 16px;
  color: var(--price);
  font-weight: bold;
}

.item-quantity {
  flex: 0 0 120px;
}

.item-actions {
  flex: 0 0 80px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--surface-raised);
  border: 1px solid var(--border);
  border-radius: 8px;
  margin-top: 12px;
  gap: 16px;
  flex-wrap: wrap;
}

.footer-left,
.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.total-label {
  font-size: 13px;
  color: #666;
}

.total-amount {
  font-size: 18px;
  color: var(--price);
  font-weight: bold;
}

.checkout-btn {
  min-width: 110px;
}

.empty-cart {
  text-align: center;
  padding: 80px 0;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
}

@media (max-width: 768px) {
  .cart-page {
    padding-bottom: 0;
  }

  .cart-content {
    padding: 10px 12px;
    border-radius: 0;
    border-left: none;
    border-right: none;
  }

  /* 移动端购物车条目：参考小程序 cart.wxss，image + body 横排 */
  .cart-item {
    flex-wrap: nowrap;
    align-items: flex-start;
    gap: 10px;
    padding: 14px 0;
  }

  .item-checkbox {
    flex: 0 0 28px;
    padding-top: 4px;
  }

  .item-image {
    flex: 0 0 68px;
  }

  /* info 区域占满剩余空间，内部纵向排列 */
  .item-info {
    flex: 1;
  }

  .book-title {
    font-size: 14px;
    white-space: normal;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  /* 价格 / 数量控制行 */
  .item-price,
  .item-subtotal {
    display: none;
  }

  .item-quantity {
    flex: none;
    margin-top: 8px;
  }

  /* 删除按钮放到 item-info 右上角 */
  .item-actions {
    flex: 0 0 auto;
    align-self: flex-start;
    padding-top: 2px;
  }

  /* 把小计跟在书名下面显示 */
  .item-info::after {
    display: none;
  }

  /* ---- 底部结算栏：固定在 Tab 栏上方 ---- */
  .cart-footer {
    position: fixed;
    bottom: calc(56px + env(safe-area-inset-bottom, 0px));
    left: 0;
    right: 0;
    margin: 0;
    border-radius: 0;
    border-left: none;
    border-right: none;
    border-bottom: none;
    padding: 10px 14px;
    z-index: 50;
    box-shadow: 0 -2px 8px rgba(26,35,50,0.10);
    flex-wrap: nowrap;
    gap: 10px;
  }

  .footer-left {
    gap: 10px;
  }

  .total-amount {
    font-size: 15px;
  }

  .checkout-btn {
    min-width: 90px;
  }
}
</style>