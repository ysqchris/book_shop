import { onMounted, onUnmounted, ref } from 'vue'

// 手机端判断：宽度 ≤ 768px，或触控设备且宽度 ≤ 1024px
const MOBILE_QUERY =
  '(max-width: 768px), ((hover: none) and (pointer: coarse) and (max-width: 1024px))'

export function useMobile() {
  const isMobile = ref(
    typeof window !== 'undefined' ? window.matchMedia(MOBILE_QUERY).matches : false
  )

  let mq = null

  const update = () => {
    isMobile.value = window.matchMedia(MOBILE_QUERY).matches
  }

  onMounted(() => {
    update()
    mq = window.matchMedia(MOBILE_QUERY)
    if (mq.addEventListener) {
      mq.addEventListener('change', update)
    } else {
      mq.addListener(update)
    }
    window.addEventListener('orientationchange', update)
  })

  onUnmounted(() => {
    if (mq) {
      if (mq.removeEventListener) {
        mq.removeEventListener('change', update)
      } else {
        mq.removeListener(update)
      }
    }
    window.removeEventListener('orientationchange', update)
  })

  return { isMobile }
}
