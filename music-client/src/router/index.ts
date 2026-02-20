import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../pages/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('../pages/Login.vue') },
  { path: '/library', name: 'Library', component: () => import('../pages/Library.vue') },
  { path: '/favorites', name: 'Favorites', component: () => import('../pages/Favorites.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../pages/Profile.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router