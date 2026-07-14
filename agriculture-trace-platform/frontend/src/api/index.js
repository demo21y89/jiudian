import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器 - 添加 Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return res
  },
  error => {
    ElMessage.error(error.response?.data?.msg || '请求失败')
    return Promise.reject(error)
  }
)

// ===== 认证 API =====
export const authApi = {
  login(data) { return api.post('/auth/login', data) },
  register(data) { return api.post('/auth/register', data) }
}

// ===== 商品 API =====
export const productApi = {
  page(params) { return api.get('/public/products/page', { params }) },
  getById(id) { return api.get(`/public/products/${id}`) },
  search(keyword) { return api.get('/public/products/search', { params: { keyword } }) },
  findByBatchNo(batchNo) { return api.get(`/public/products/batch/${batchNo}`) }
}

// ===== AI 对话 API =====
export const chatApi = {
  send(data) { return api.post('/chat/send', data) }
}

// ===== 订单 API =====
export const orderApi = {
  create(data) { return api.post('/user/orders/create', data) },
  list() { return api.get('/user/orders/list') },
  getById(id) { return api.get(`/user/orders/${id}`) },
  pay(id) { return api.post(`/user/orders/${id}/pay`) },
  cancel(id) { return api.post(`/user/orders/${id}/cancel`) }
}

// ===== 后台管理 API =====
export const adminApi = {
  // 商品
  products: {
    page(params) { return api.get('/admin/products/page', { params }) },
    save(data) { return api.post('/admin/products', data) },
    update(data) { return api.put('/admin/products', data) },
    delete(id) { return api.delete(`/admin/products/${id}`) },
    list() { return api.get('/admin/products/list') }
  },
  // 订单
  orders: {
    page(params) { return api.get('/admin/orders/page', { params }) },
    getById(id) { return api.get(`/admin/orders/${id}`) },
    ship(id, data) { return api.post(`/admin/orders/${id}/ship`, data) },
    complete(id) { return api.post(`/admin/orders/${id}/complete`) }
  },
  // 溯源
  trace: {
    getBatch(batchNo) { return api.get(`/admin/trace/batch/${batchNo}`) },
    createBatch(data) { return api.post('/admin/trace/batch', data) },
    addRecord(data) { return api.post('/admin/trace/record', data) },
    addPesticide(data) { return api.post('/admin/trace/pesticide', data) }
  },
  // 知识库
  knowledge: {
    list() { return api.get('/admin/knowledge/list') },
    save(data) { return api.post('/admin/knowledge', data) },
    delete(id) { return api.delete(`/admin/knowledge/${id}`) },
    search(keyword) { return api.get('/admin/knowledge/search', { params: { keyword } }) }
  },
  // 仪表盘
  dashboard: {
    stats() { return api.get('/admin/dashboard/stats') },
    salesReport(period) { return api.get('/admin/dashboard/sales-report', { params: { period } }) }
  }
}

export default api
