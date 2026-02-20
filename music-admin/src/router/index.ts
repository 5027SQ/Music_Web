import { createRouter, createWebHistory } from 'vue-router';
import Login from '../pages/Login.vue';
import Dashboard from '../pages/Dashboard.vue';
import ArtistList from '../pages/ArtistList.vue';
import SongList from '../pages/SongList.vue';
import UserList from '../pages/UserList.vue';
import AdminLayout from '../layouts/AdminLayout.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    {
      path: '/',
      component: AdminLayout,
      children: [
        { path: '', component: Dashboard },
        { path: 'artists', component: ArtistList },
        { path: 'songs', component: SongList },
        { path: 'users', component: UserList }
      ]
    }
  ]
});

// 简单登录守卫
router.beforeEach((to) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && !token) return '/login';
});

export default router;