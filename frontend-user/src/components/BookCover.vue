<template>
  <div class="book-cover" :style="{ height, width: width || '100%' }">
    <img
      v-if="currentSrc && !failed"
      :src="currentSrc"
      :alt="title"
      @error="onError"
    />
    <div v-else class="cover-placeholder" :style="placeholderStyle">
      <span>{{ letter }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  title: { type: String, default: '书' },
  height: { type: String, default: '200px' },
  width: { type: String, default: '' }
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
  ['#1f6b5c', '#7fafa3'],
  ['#24586b', '#7ea8b8'],
  ['#3d4f54', '#91a1a6'],
  ['#4a5d43', '#9bb092'],
  ['#6b5344', '#b8a08e'],
  ['#2f4858', '#7d98a8'],
  ['#5c4f3a', '#b0a184'],
  ['#3a4f4a', '#8eaaa3']
]

const placeholderStyle = computed(() => {
  const code = [...(props.title || '')].reduce((sum, ch) => sum + ch.charCodeAt(0), 0)
  const [from, to] = palette[code % palette.length]
  return {
    background: `linear-gradient(155deg, ${from}, ${to})`
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
  background: #eef2f0;
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
  color: rgba(255, 255, 255, 0.95);
  font-family: var(--font-display);
  font-size: 42px;
  font-weight: 700;
}
</style>
