<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel :interval="4000" type="card" height="300px">
      <el-carousel-item v-for="item in carouselItems" :key="item.id">
        <div class="carousel-content">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 平台介绍 -->
    <div class="intro-section">
      <h2>欢迎来到二手书买卖平台</h2>
      <p class="intro-text">
        我们致力于为爱书人士提供一个安全、便捷的二手书交易平台。
        在这里，你可以轻松买卖闲置书籍，让好书继续传递价值。
      </p>
      <div class="features">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="feature-item">
              <el-icon size="40"><Reading /></el-icon>
              <h3>海量图书</h3>
              <p>涵盖文学、科技、教材等各类图书</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="feature-item">
              <el-icon size="40"><Lock /></el-icon>
              <h3>安全交易</h3>
              <p>完善的交易保障机制，确保买卖双方权益</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="feature-item">
              <el-icon size="40"><Truck /></el-icon>
              <h3>便捷物流</h3>
              <p>与主流物流合作，快速送达</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 热门图书 -->
    <div class="hot-books">
      <h2>热门图书推荐</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="book in hotBooks" :key="book.id">
          <el-card class="book-card" shadow="hover" @click="$router.push(`/book/${book.id}`)">
            <div class="book-cover">
              <img :src="book.coverImage || '/default-book.jpg'" :alt="book.title" />
            </div>
            <div class="book-info">
              <h4 class="book-title">{{ book.title }}</h4>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-price">¥{{ book.price }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Reading, Lock, Truck } from '@element-plus/icons-vue'
import { getHotBooksApi } from '@/api/book'

const carouselItems = ref([
  {
    id: 1,
    title: '让好书不再闲置',
    description: '专业的二手书交易平台，让每本书都找到新主人'
  },
  {
    id: 2,
    title: '环保阅读新方式',
    description: '循环利用图书资源，为环保贡献力量'
  },
  {
    id: 3,
    title: '优质图书低价购',
    description: '以实惠的价格购买到心仪的图书'
  }
])

const hotBooks = ref([])

onMounted(async () => {
  try {
    const response = await getHotBooksApi()
    if (response.code === 200) {
      hotBooks.value = response.data
    }
  } catch (error) {
    console.error('获取热门图书失败:', error)
  }
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.carousel-content {
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.carousel-content h3 {
  font-size: 28px;
  margin-bottom: 10px;
}

.carousel-content p {
  font-size: 16px;
}

.intro-section {
  text-align: center;
  margin: 40px 0;
}

.intro-section h2 {
  color: #333;
  margin-bottom: 20px;
}

.intro-text {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 40px;
}

.feature-item {
  text-align: center;
  padding: 20px;
}

.feature-item .el-icon {
  color: #409eff;
  margin-bottom: 10px;
}

.feature-item h3 {
  margin: 10px 0;
  color: #333;
}

.feature-item p {
  color: #666;
  font-size: 14px;
}

.hot-books {
  margin-top: 40px;
}

.hot-books h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.book-card {
  cursor: pointer;
  transition: transform 0.3s;
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
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.book-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}
</style>