<template>
  <div id="app" :class="{ 'is-mobile': isMobile }">
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/books')">
          <img class="logo-img" src="/shop-sign.png" alt="三定圆梦书店" />
          <h1>三定圆梦书店</h1>
        </div>
        <!-- 桌面端导航 -->
        <nav class="nav-links" v-if="!isMobile">
          <router-link to="/books" class="nav-link" :class="{ active: isBooks }">选书</router-link>
          <router-link to="/cart" class="nav-link" active-class="active">
            购物车
            <span v-if="cartStore.itemCount" class="cart-badge">{{ cartStore.itemCount }}</span>
          </router-link>
          <router-link to="/orders" class="nav-link" active-class="active">我的订单</router-link>
          <router-link to="/consult" class="nav-link" active-class="active">找书咨询</router-link>
        </nav>

        <!-- 移动端：选书页右上角视图切换 -->
        <div v-if="isMobile && isBooks" class="mobile-view-toggle">
          <button
            class="view-toggle-btn"
            :class="{ active: viewModeStore.mode === 'cover' }"
            @click="viewModeStore.setMode('cover')"
            aria-label="卡片视图"
          >⊞</button>
          <button
            class="view-toggle-btn"
            :class="{ active: viewModeStore.mode === 'text' }"
            @click="viewModeStore.setMode('text')"
            aria-label="列表视图"
          >☰</button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer" v-if="!isMobile">
      <div class="footer-content">
        <p>© {{ year }} 三定圆梦书店 · 下单后联系店家线下付款</p>
      </div>
    </footer>

    <!-- 移动端底部 Tab 栏（参考小程序 tabBar 布局） -->
    <nav class="mobile-tabbar" v-if="isMobile">
      <router-link to="/books" class="tab-item" :class="{ active: isBooks }">
        <span class="tab-icon">📚</span>
        <span class="tab-label">选书</span>
      </router-link>
      <router-link to="/cart" class="tab-item" :class="{ active: isCart }">
        <span class="tab-icon tab-icon--cart">
          🛒
          <span v-if="cartStore.itemCount" class="tab-badge">{{ cartStore.itemCount }}</span>
        </span>
        <span class="tab-label">购物车</span>
      </router-link>
      <router-link to="/orders" class="tab-item" :class="{ active: isOrders }">
        <span class="tab-icon">📋</span>
        <span class="tab-label">我的订单</span>
      </router-link>
      <router-link to="/consult" class="tab-item" :class="{ active: isConsult }">
        <span class="tab-icon">💬</span>
        <span class="tab-label">找书咨询</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useViewModeStore } from '@/stores/viewMode'
import { useMobile } from '@/composables/useMobile'

const route = useRoute()
const cartStore = useCartStore()
const viewModeStore = useViewModeStore()
const { isMobile } = useMobile()
const year = new Date().getFullYear()
const isBooks = computed(() => route.path === '/books' || route.path.startsWith('/book/'))
const isCart = computed(() => route.path === '/cart')
const isOrders = computed(() => route.path === '/orders')
const isConsult = computed(() => route.path === '/consult')
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: sticky;
  top: 0;
  z-index: 20;
  height: 72px;
  padding: 0;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 40px;
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 0 28px;
  height: 100%;
  box-sizing: border-box;
  width: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
  min-width: 0;
}

.logo-img {
  height: 48px;
  width: 108px;
  object-fit: cover;
  object-position: center 28%;
  border-radius: 6px;
  border: 1px solid var(--border);
  background: #f5d84a;
  display: block;
  flex-shrink: 0;
}

.logo h1 {
  margin: 0;
  color: var(--ink);
  font-size: 17px;
  line-height: 1.2;
  white-space: nowrap;
  font-weight: 600;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-width: 82px;
  justify-content: center;
  padding: 10px 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--surface-raised);
  color: var(--ink-soft);
  text-decoration: none;
  font-size: 15px;
  white-space: nowrap;
  transition: color 0.2s, background 0.2s;
}

.nav-link:hover {
  color: var(--accent);
  background: var(--accent-soft);
  border-color: var(--accent-line);
}

.nav-link.active {
  color: #fff;
  background: var(--accent);
  border-color: var(--accent);
  font-weight: 600;
}

.cart-badge {
  display: inline-block;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: var(--accent);
  color: #fff;
  font-size: 11px;
  line-height: 18px;
  text-align: center;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 24px 28px 32px;
  box-sizing: border-box;
}

.main-content > * {
  flex: 1;
  width: 100%;
  max-width: 100%;
  min-width: 0;
}

.footer {
  text-align: center;
  padding: 20px 16px;
  border-top: 1px solid var(--border);
  background: var(--surface);
}

.footer-content p {
  margin: 0;
  color: var(--muted);
  font-size: 12px;
  line-height: 1.6;
}

/* ===== 移动端底部 Tab 栏（参考小程序 tabBar） ===== */
.mobile-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  height: 56px;
  background: var(--surface);
  border-top: 1px solid var(--border);
  padding-bottom: env(safe-area-inset-bottom, 0px);
  box-shadow: 0 -2px 8px rgba(26, 35, 50, 0.08);
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  text-decoration: none;
  color: var(--muted);
  font-size: 10px;
  transition: color 0.18s;
  padding-bottom: 2px;
}

.tab-item.active {
  color: var(--accent);
}

.tab-icon {
  font-size: 20px;
  line-height: 1;
  position: relative;
  display: inline-block;
}

.tab-icon--cart {
  position: relative;
}

.tab-badge {
  position: absolute;
  top: -4px;
  right: -8px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: #e53e3e;
  color: #fff;
  font-size: 10px;
  line-height: 16px;
  text-align: center;
  font-style: normal;
}

.tab-label {
  font-size: 10px;
  line-height: 1.2;
}

/* ===== 移动端 main-content 留出底部 Tab 栏空间 ===== */
.is-mobile .main-content {
  padding-bottom: calc(56px + env(safe-area-inset-bottom, 0px) + 12px);
}

/* ===== 移动端顶部视图切换按钮 ===== */
.mobile-view-toggle {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
  background: var(--surface-raised);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 3px;
}

.view-toggle-btn {
  width: 30px;
  height: 30px;
  border: none;
  background: transparent;
  color: var(--ink-soft);
  font-size: 16px;
  line-height: 1;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s, color 0.2s;
}

.view-toggle-btn.active {
  background: var(--accent);
  color: #fff;
}

@media (max-width: 768px) {
  .header {
    height: 52px;
  }

  .header-content {
    padding: 0 16px;
    gap: 10px;
  }

  .logo h1 {
    font-size: 15px;
  }

  .logo-img {
    height: 36px;
    width: 80px;
  }

  .main-content {
    padding: 14px 16px calc(56px + env(safe-area-inset-bottom, 0px) + 12px);
  }
}
</style>