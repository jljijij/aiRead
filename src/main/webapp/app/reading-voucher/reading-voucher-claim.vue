<template>
  <div>
    <button @click="claim">抢卷</button>
    <div v-if="voucher">
      <pre>{{ voucher }}</pre>
    </div>
    <div v-else-if="hasError">暂无可用阅读券</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ReadingVoucherService from './reading-voucher.service';

const voucher = ref<any | null>(null);
const hasError = ref(false);
const readingVoucherService = new ReadingVoucherService();

const claim = async () => {
  try {
    const res = await readingVoucherService.claim();
    voucher.value = res.data ? res.data : res;
    hasError.value = false;
  } catch (err) {
    console.error(err);
    voucher.value = null;
    hasError.value = true;
  }
};
</script>
