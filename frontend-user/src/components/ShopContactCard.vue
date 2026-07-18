<template>
  <div v-if="contact" class="shop-contact-card" :class="variant">
    <div class="card-head">
      <h3>{{ title }}</h3>
      <p v-if="lead" class="lead">{{ lead }}</p>
    </div>

    <div class="card-body">
      <ul class="info-list">
        <li v-if="contact.name">
          <span>店家</span>
          <strong>{{ contact.name }}</strong>
        </li>
        <li v-if="contact.phone">
          <span>电话</span>
          <a :href="`tel:${contact.phone}`">{{ contact.phone }}</a>
        </li>
        <li v-if="contact.address">
          <span>地址</span>
          <em>{{ contact.address }}</em>
        </li>
        <li v-if="contact.wechat">
          <span>微信</span>
          <strong>{{ contact.wechat }}</strong>
        </li>
      </ul>

      <div v-if="contact.wechatQrcode" class="qrcode-box">
        <img :src="contact.wechatQrcode" alt="店家微信二维码" />
        <p>微信扫一扫，添加店家</p>
      </div>
    </div>

    <p v-if="contact.tip" class="tip">{{ contact.tip }}</p>
  </div>
</template>

<script setup>
defineProps({
  contact: { type: Object, default: null },
  title: { type: String, default: '商家信息' },
  lead: { type: String, default: '' },
  variant: { type: String, default: 'default' }
})
</script>

<style scoped>
.shop-contact-card {
  border-radius: 8px;
  padding: 16px 18px;
  border: 1px solid var(--border);
  background: var(--surface);
}

.shop-contact-card.banner,
.shop-contact-card.success {
  background: var(--surface);
}

.card-head h3 {
  margin: 0;
  font-size: 17px;
  color: var(--ink);
}

.lead {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--muted);
  line-height: 1.5;
}

.card-body {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  margin-top: 14px;
}

.info-list {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  min-width: 0;
}

.info-list li {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 8px 0;
  border-top: 1px solid var(--border);
  font-size: 15px;
  color: var(--ink);
}

.info-list li:first-child {
  border-top: none;
  padding-top: 0;
}

.info-list span {
  width: 40px;
  flex-shrink: 0;
  color: var(--muted);
}

.info-list strong {
  font-weight: 700;
}

.info-list a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
}

.info-list a:hover {
  text-decoration: underline;
}

.info-list em {
  font-style: normal;
  line-height: 1.5;
}

.qrcode-box {
  flex: 0 0 auto;
  width: 150px;
  padding: 10px;
  border-radius: 8px;
  background: var(--surface-raised);
  border: 1px solid var(--border);
  text-align: center;
}

.qrcode-box img {
  width: 128px;
  height: 128px;
  object-fit: contain;
  display: block;
  margin: 0 auto;
  background: var(--surface-raised);
}

.qrcode-box p {
  margin: 8px 0 0;
  font-size: 12px;
  color: var(--ink-soft);
  line-height: 1.4;
}

.tip {
  margin: 14px 0 0;
  padding-top: 12px;
  border-top: 1px dashed var(--accent-line);
  font-size: 13px;
  color: var(--ink-soft);
  line-height: 1.5;
}

@media (max-width: 640px) {
  .card-body {
    flex-direction: column;
  }

  .qrcode-box {
    width: 100%;
  }
}
</style>
