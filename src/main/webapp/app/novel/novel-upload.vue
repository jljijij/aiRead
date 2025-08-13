<template>
  <div>
    <h2>小说上传解析</h2>
    <input type="file" @change="onFileChange" />
    <button @click="upload" :disabled="!file">上传并解析</button>
  <pre v-if="content">{{ content }}</pre>
</div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import NovelService from './novel.service';

  const novelService = new NovelService();
  const file = ref<File | null>(null);
  const content = ref('');

  const onFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
  file.value = target.files[0];
} else {
  file.value = null;
}
};

  const upload = async () => {
  if (file.value) {
  content.value = await novelService.parse(file.value);
}
};
</script>
