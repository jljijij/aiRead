import axios from 'axios';

export default class UserCouponService {
  listMy(page = 0) {
    return axios.get('api/user-coupons/my', { params: { page } });
  }

  use(couponId: number) {
    return axios.post('api/user-coupons/use', { couponId });
  }
}
