import axios from 'axios';

export default class ReadingVoucherService {
  upload(file: File, type: string, novelId?: number, chapterId?: number, packageId?: number): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    if (novelId !== undefined) formData.append('novelId', novelId.toString());
    if (chapterId !== undefined) formData.append('chapterId', chapterId.toString());
    if (packageId !== undefined) formData.append('packageId', packageId.toString());
    return axios.post('api/reading-vouchers/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
  }

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
