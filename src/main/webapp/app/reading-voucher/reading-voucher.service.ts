import axios from 'axios';

export default class ReadingVoucherService {
  claim(): Promise<any> {
    return axios.post('api/reading-vouchers/claim');
  }

  getAll(): Promise<any> {
    return axios.get('api/reading-vouchers');
  }

  available(): Promise<any> {
    return axios.get('api/reading-vouchers/available');
  }

  mine(): Promise<any> {
    return axios.get('api/reading-vouchers/my');
  }
}
