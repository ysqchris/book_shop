<template>
  <div class="checkout-page page-enter" v-loading="loading">
    <h1>确认订单</h1>
    <p class="offline-tip">暂不支持线上支付。提交后请电话或扫码联系店家，确认品相后线下付款。</p>

    <ShopContactCard
      v-if="shopContact"
      class="shop-block"
      :contact="shopContact"
      title="商家信息"
      lead="实体店承接订单，家长与同学都可扫码加微信或电话联系确认"
    />

    <el-empty v-if="!items.length && !loading" description="没有待结算的商品">
      <el-button type="primary" @click="$router.push('/books')">去选书</el-button>
    </el-empty>

    <template v-else-if="items.length">
      <div class="section">
        <h3>商品清单</h3>
        <div class="item" v-for="item in items" :key="item.bookId">
          <BookCover :src="item.bookCover" :title="item.bookTitle" height="80px" width="60px" />
          <div class="info">
            <div class="title">{{ item.bookTitle }}</div>
            <div class="meta">{{ item.bookAuthor }}</div>
          </div>
          <div class="price">¥{{ Number(item.bookPrice).toFixed(2) }}</div>
          <div class="qty">x{{ item.quantity }}</div>
          <div class="subtotal">¥{{ (Number(item.bookPrice) * item.quantity).toFixed(2) }}</div>
        </div>
        <div class="total">合计：¥{{ totalAmount.toFixed(2) }}</div>
      </div>

      <div class="section">
        <h3>联系信息</h3>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
          <el-form-item label="联系人" prop="receiverName">
            <el-input v-model="form.receiverName" placeholder="您的姓名" maxlength="50" />
          </el-form-item>
          <el-form-item label="联系电话" prop="receiverPhone">
            <el-input v-model="form.receiverPhone" placeholder="手机号，方便店家联系" maxlength="20" />
          </el-form-item>
          <el-form-item label="所在地区" prop="region">
            <el-cascader
              v-model="form.region"
              :options="regionOptions"
              placeholder="请选择省 / 市 / 区"
              clearable
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="详细地址" prop="addressDetail">
            <el-input
              v-model="form.addressDetail"
              placeholder="街道、小区、门牌号等（选填）"
              maxlength="200"
            />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" maxlength="500" />
          </el-form-item>
        </el-form>
      </div>

      <div class="actions">
        <el-button @click="$router.back()">返回</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pcaTextArr } from 'element-china-area-data'
import { createOrderApi, getBookDetailApi } from '@/api/book'
import { getShopContactApi } from '@/api/shop'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import BookCover from '@/components/BookCover.vue'
import ShopContactCard from '@/components/ShopContactCard.vue'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const items = ref([])
const shopContact = ref(null)
const formRef = ref()
const regionOptions = pcaTextArr
const form = reactive({
  receiverName: '',
  receiverPhone: '',
  region: ['湖南省', '株洲市', '攸县'],
  addressDetail: '',
  remark: ''
})

const rules = {
  receiverName: [{ required: true, message: '请填写联系人', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请填写联系电话', trigger: 'blur' },
    { pattern: /^1\d{10}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入有效电话', trigger: 'blur' }
  ],
  region: [{ required: true, type: 'array', min: 3, message: '请选择所在地区', trigger: 'change' }]
}

const buildAddress = () => {
  const regionPart = (form.region || []).join('')
  const detail = (form.addressDetail || '').trim()
  if (regionPart && detail) return `${regionPart}${detail}`
  return regionPart || detail
}

const totalAmount = computed(() =>
  items.value.reduce((sum, item) => sum + Number(item.bookPrice) * item.quantity, 0)
)

const loadShopContact = async () => {
  try {
    const res = await getShopContactApi()
    shopContact.value = res.data
  } catch {
    shopContact.value = null
  }
}

const loadItems = async () => {
  loading.value = true
  try {
    await loadShopContact()
    const checkout = cartStore.getCheckoutItems()
    if (!checkout.length) {
      items.value = []
      return
    }
    const resolved = []
    for (const row of checkout) {
      const res = await getBookDetailApi(row.bookId)
      const book = res.data
      if (!book) continue
      resolved.push({
        bookId: book.id,
        bookTitle: book.title,
        bookAuthor: book.author,
        bookCover: book.coverImage,
        bookPrice: Number(book.price),
        quantity: Math.min(Number(row.quantity) || 1, book.stock || 1),
        maxStock: book.stock
      })
    }
    items.value = resolved
  } catch (e) {
    items.value = []
  } finally {
    loading.value = false
  }
}

const submitOrder = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!items.value.length) {
    ElMessage.warning('没有可下单的商品')
    return
  }

  submitting.value = true
  try {
    const payload = {
      userId: userStore.isLoggedIn ? userStore.userInfo?.id : null,
      receiverName: form.receiverName.trim(),
      receiverPhone: form.receiverPhone.trim(),
      receiverAddress: buildAddress(),
      remark: form.remark,
      items: items.value.map((item) => ({
        bookId: item.bookId,
        quantity: item.quantity
      }))
    }
    const res = await createOrderApi(payload)
    const order = res.data.order
    cartStore.removePurchased(items.value.map((i) => i.bookId))
    cartStore.clearCheckoutItems()
    cartStore.saveGuestOrder({
      id: order.id,
      orderNo: order.orderNo,
      payAmount: order.payAmount,
      status: order.status,
      createTime: order.createTime
    })
    router.replace({
      path: `/order/success/${order.id}`,
      query: { from: 'checkout' }
    })
  } catch {
    // 错误已由拦截器提示
  } finally {
    submitting.value = false
  }
}

onMounted(loadItems)
</script>

<style scoped>
.checkout-page {
  max-width: 880px;
  margin: 0 auto;
  padding-bottom: 40px;
}

h1 {
  margin: 0 0 8px;
  font-size: 26px;
  color: var(--ink);
}

.offline-tip {
  margin: 0 0 16px;
  color: var(--accent-deep);
  background: var(--accent-soft);
  border: 1px solid var(--accent-line);
  border-radius: 8px;
  padding: 12px 14px;
  font-size: 14px;
  line-height: 1.6;
}

.section {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px 18px;
  margin-bottom: 16px;
}

.section h3 {
  margin: 0 0 14px;
  font-size: 16px;
}

.shop-block {
  margin-bottom: 16px;
}

.item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.item:last-of-type {
  border-bottom: none;
}

.info {
  flex: 1;
  min-width: 0;
}

.title {
  font-weight: 600;
  color: #333;
}

.meta {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

.price,
.qty,
.subtotal {
  width: 80px;
  text-align: right;
  color: #555;
}

.subtotal {
  color: var(--price);
  font-weight: 600;
}

.total {
  margin-top: 12px;
  text-align: right;
  font-size: 18px;
  font-weight: 700;
  color: var(--price);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 640px) {
  h1 {
    font-size: 20px;
  }

  .section {
    padding: 14px 14px;
    border-radius: 10px;
  }

  /* 商品行：移动端隐藏单独的价格和数量列，合并到 info 中 */
  .item {
    gap: 10px;
    padding: 10px 0;
  }

  .price,
  .qty {
    display: none;
  }

  .subtotal {
    width: auto;
    font-size: 15px;
  }

  .info .meta::after {
    content: attr(data-qty);
  }

  /* 表单标签上移，输入框全宽（参考小程序 form-item 布局） */
  :deep(.el-form-item) {
    display: block;
    margin-bottom: 14px;
  }

  :deep(.el-form-item__label) {
    float: none;
    display: block;
    width: auto !important;
    text-align: left;
    line-height: 1.4;
    padding: 0 0 6px 0;
    font-size: 13px;
    color: var(--muted);
  }

  :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }

  :deep(.el-cascader) {
    width: 100% !important;
  }

  /* 操作按钮全宽 */
  .actions {
    flex-direction: column;
    gap: 10px;
  }

  .actions .el-button {
    width: 100%;
    margin: 0;
  }
}
</style>
