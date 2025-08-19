import axios from 'axios';

export default class ReadingVoucherService {
  public upload(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);
    return axios.post('api/reading-vouchers/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  }

  public claim(): Promise<any> {
    return axios.post('api/reading-vouchers/claim');
  }

  public retrieve(): Promise<any> {
    return axios.get('api/reading-vouchers');
  }
}
