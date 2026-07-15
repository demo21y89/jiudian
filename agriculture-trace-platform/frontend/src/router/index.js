import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/products' },

  // 公开页面（无需登录）
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  { path: '/products', name: 'Products', component: () => import('@/views/ProductList.vue') },
  { path: '/products/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetail.vue') },
  { path: '/chat', name: 'Chat', component: () => import('@/views/ChatView.vue') },

  // 消费者/用户页面（需登录）
  { path: '/cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { requiresAuth: true } },
  { path: '/orders', name: 'Orders', component: () => import('@/views/OrderList.vue'), meta: { requiresAuth: true } },
  { path: '/orders/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetail.vue'), meta: { requiresAuth: true } },

  // 管理后台（仅 ADMIN / PRODUCER 角色可访问）
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminDashboard.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN', 'PRODUCER'] }
  },
  {
    path: '/admin/products',
    component: () => import('@/views/admin/AdminProducts.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN', 'PRODUCER'] }
  },
  {
    path: '/admin/orders',
    component: () => import('@/views/admin/AdminOrders.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/admin/trace',
    component: () => import('@/views/admin/AdminTrace.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN', 'PRODUCER'] }
  },
  {
    path: '/admin/knowledge',
    component: () => import('@/views/admin/AdminKnowledge.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/admin/dialogs',
    component: () => import('@/views/admin/AdminDialogs.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 权限控制
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 需要登录但未登录 → 跳转登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // 需要特定角色但角色不匹配 → 跳转商城首页
  if (to.meta.roles && token) {
    if (!to.meta.roles.includes(role)) {
      next('/products')
      return
    }
  }

  next()
})

export default router
