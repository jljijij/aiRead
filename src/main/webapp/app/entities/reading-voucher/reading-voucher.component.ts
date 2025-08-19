import { defineComponent, inject, ref } from 'vue';
import type ReadingVoucherService from './reading-voucher.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReadingVoucher',
  setup() {
    const readingVoucherService = inject('readingVoucherService') as () => ReadingVoucherService;
    const file = ref<File | null>(null);
    const claimedCode = ref('');

    const upload = () => {
      if (file.value) {
        readingVoucherService()
          .upload(file.value)
          .then(() => {
            file.value = null;
          });
      }
    };

    const claimVoucher = () => {
      readingVoucherService()
        .claim()
        .then(res => {
          claimedCode.value = res.data.code;
        });
    };

    return { file, upload, claimVoucher, claimedCode };
  },
});
