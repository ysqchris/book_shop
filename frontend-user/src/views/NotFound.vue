<template>
  <div class="not-found">
    <el-result icon="warning" title="404" sub-title="页面不存在">
      <template #extra>
        <el-button type="primary" @click="$router.push('/books')">返回图书浏览</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
</script>
