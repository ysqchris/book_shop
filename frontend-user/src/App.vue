<template>
  <div id="app">
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/books')">
          <h1>易三定二手图书商店</h1>
        </div>
        <nav class="nav-links">
          <router-link to="/books" class="nav-link" :class="{ active: isBooks }">选书</router-link>
          <router-link to="/cart" class="nav-link" active-class="active">
            购物车
            <span v-if="cartStore.itemCount" class="cart-badge">{{ cartStore.itemCount }}</span>
          </router-link>
          <router-link to="/orders" class="nav-link" active-class="active">我的订单</router-link>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer">
      <div class="footer-content">
        <p>© {{ year }} 易三定二手图书商店 · 下单后联系店家线下付款</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const cartStore = useCartStore()
const year = new Date().getFullYear()
const isBooks = computed(() => route.path === '/books' || route.path.startsWith('/book/'))
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
  cursor: pointer;
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

@media (max-width: 800px) {
  .header {
    height: auto;
    padding: 10px 0;
  }

  .header-content {
    flex-wrap: wrap;
    gap: 10px;
  }

  .logo h1 {
    font-size: 15px;
  }

  .nav-links {
    width: 100%;
    order: 3;
    gap: 8px;
    overflow-x: auto;
  }

  .nav-link {
    min-width: 76px;
    padding: 8px 12px;
    font-size: 14px;
  }

  .header-content,
  .main-content {
    padding-left: 16px;
    padding-right: 16px;
  }

  .main-content {
    padding-top: 14px;
    padding-bottom: 20px;
  }
}
</style>
