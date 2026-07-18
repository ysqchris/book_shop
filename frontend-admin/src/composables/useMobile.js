import { onMounted, onUnmounted, ref } from 'vue'

// 普通窄屏 + 手机开「电脑版网页」时的触控设备
const MOBILE_QUERY =
  '(max-width: 992px), ((hover: none) and (pointer: coarse) and (max-width: 1280px))'

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
