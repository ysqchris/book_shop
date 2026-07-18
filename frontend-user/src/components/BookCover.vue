<template>
  <div class="book-cover" :style="{ height: height }">
    <img
      v-if="currentSrc && !failed"
      :src="currentSrc"
      :alt="title"
      @error="onError"
    />
    <div v-else class="cover-placeholder" :style="placeholderStyle">
      {{ letter }}
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  title: { type: String, default: '书' },
  height: { type: String, default: '200px' }
})

const failed = ref(false)
const currentSrc = computed(() => props.src || '')

watch(
  () => props.src,
  () => {
    failed.value = false
  }
)

const letter = computed(() => (props.title || '书').trim().charAt(0) || '书')

const palette = [
  ['#3d5a80', '#98c1d9'],
  ['#2f4858', '#86bbd8'],
  ['#4a5568', '#a0aec0'],
  ['#1b4332', '#74c69d'],
  ['#5c4b51', '#c9ada7'],
  ['#283618', '#a3b18a'],
  ['#3c096c', '#9d4edd'],
  ['#7f4f24', '#ddb892']
]

const placeholderStyle = computed(() => {
  const code = [...(props.title || '')].reduce((sum, ch) => sum + ch.charCodeAt(0), 0)
  const [from, to] = palette[code % palette.length]
  return {
    background: `linear-gradient(145deg, ${from}, ${to})`
  }
})

const onError = () => {
  failed.value = true
}
</script>

<style scoped>
.book-cover {
  width: 100%;
  overflow: hidden;
  border-radius: 4px;
  background: #f3f4f6;
}

.book-cover img,
.cover-placeholder {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.cover-placeholder {
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 42px;
  font-weight: 700;
}
</style>
