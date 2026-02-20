import { createRouter, createWebHistory } from 'vue-router'

import Home from '../pages/Home.vue'
import Library from '../pages/Library.vue'
import Favorites from '../pages/Favorites.vue'
import Profile from '../pages/Profile.vue'
import Login from '../pages/Login.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/library', component: Library },
  { path: '/favorites', component: Favorites },
  { path: '/profile', component: Profile },
  { path: '/login', component: Login }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router