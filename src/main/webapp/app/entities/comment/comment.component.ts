import { defineComponent, inject, onMounted, ref } from 'vue';
import type CommentService from './comment.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Comment',
  setup() {
    const commentService = inject('commentService') as () => CommentService;
    const comments = ref<any[]>([]);

    const loadAll = () => {
      commentService()
        .retrieve()
        .then(res => {
          comments.value = res.data;
        });
    };

    onMounted(() => {
      loadAll();
    });

    return { comments, loadAll };
  },
});
