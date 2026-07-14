import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: JSON.parse(localStorage.getItem('cartItems') || '[]')
  }),

  getters: {
    totalCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    totalAmount: (state) => state.items.reduce((sum, item) => sum + item.price * item.quantity, 0)
  },

  actions: {
    addItem(product) {
      const existing = this.items.find(i => i.id === product.id)
      if (existing) {
        existing.quantity++
      } else {
        this.items.push({
          id: product.id,
          name: product.name,
          price: product.price,
          image: product.imageUrl,
          quantity: 1,
          stock: product.stock
        })
      }
      this.save()
      ElMessage.success('已添加到购物车')
    },

    removeItem(productId) {
      this.items = this.items.filter(i => i.id !== productId)
      this.save()
    },

    updateQuantity(productId, quantity) {
      const item = this.items.find(i => i.id === productId)
      if (item) {
        item.quantity = quantity
        this.save()
      }
    },

    clear() {
      this.items = []
      this.save()
    },

    save() {
      localStorage.setItem('cartItems', JSON.stringify(this.items))
    }
  }
})
