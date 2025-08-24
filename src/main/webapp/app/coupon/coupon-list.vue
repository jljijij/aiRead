<template>
  <div>
    <h2>我的优惠券</h2>
    <div v-if="coupons.length === 0">暂无优惠券</div>
    <table v-else class="table table-striped">
      <thead>
        <tr>
          <th>券码</th>
          <th>过期时间</th>
          <th>状态</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="coupon in coupons" :key="coupon.id">
          <td>{{ coupon.code }}</td>
          <td>{{ formatDateShort(coupon.expireTime) }}</td>
          <td>{{ coupon.used ? '已使用' : isExpired(coupon) ? '已过期' : '未使用' }}</td>
          <td>
            <button v-if="!coupon.used && !isExpired(coupon)" class="btn btn-primary btn-sm" @click="useCoupon(coupon.id)">使用</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import UserCouponService from './user-coupon.service';
import { useDateFormat } from '@/shared/composables/date-format';

const service = new UserCouponService();
const coupons = ref<any[]>([]);
const { formatDateShort } = useDateFormat();

const load = async () => {
  const res = await service.listMy();
  coupons.value = res.data;
};

const useCoupon = async (id: number) => {
  await service.use(id);
  await load();
};

const isExpired = (coupon: any) => coupon.expireTime && new Date(coupon.expireTime) < new Date();

onMounted(load);
</script>
