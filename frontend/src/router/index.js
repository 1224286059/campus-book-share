import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { guestOnly: true, hideHeader: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue'),
    meta: { guestOnly: true, hideHeader: true }
  },
  {
    path: '/books/:id',
    name: 'book-detail',
    component: () => import('../views/BookDetail.vue')
  },
  {
    path: '/books/publish',
    name: 'publish-book',
    component: () => import('../views/PublishBook.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my/books',
    name: 'my-books',
    component: () => import('../views/MyBooks.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my/owned-books',
    name: 'my-owned-books',
    component: () => import('../views/MyOwnedBooks.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my/orders',
    name: 'my-orders',
    component: () => import('../views/MyOrders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my/borrows',
    name: 'my-borrows',
    component: () => import('../views/MyBorrows.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my/points',
    name: 'my-points',
    component: () => import('../views/MyPoints.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true },
    redirect: '/admin/books',
    children: [
      {
        path: 'users',
        name: 'admin-users',
        component: () => import('../views/admin/AdminUsers.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true }
      },
      {
        path: 'books',
        name: 'admin-books',
        component: () => import('../views/admin/AdminBooks.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true }
      },
      {
        path: 'categories',
        name: 'admin-categories',
        component: () => import('../views/admin/AdminCategories.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true }
      },
      {
        path: 'orders',
        name: 'admin-orders',
        component: () => import('../views/admin/AdminOrders.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true }
      },
      {
        path: 'evaluations',
        name: 'admin-evaluations',
        component: () => import('../views/admin/AdminEvaluations.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true }
      },
      {
        path: 'reports',
        name: 'admin-reports',
        component: () => import('../views/admin/AdminReports.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, hideHeader: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: routes,
  scrollBehavior: function () {
    return { top: 0 }
  }
})

router.beforeEach(async function (to) {
  var userStore = useUserStore()
  if (userStore.token && !userStore.profile && !userStore.loadingProfile) {
    try {
      await userStore.fetchProfile(true)
    } catch (error) {
      userStore.logout()
    }
  }

  if (to.meta.guestOnly && userStore.isLoggedIn) {
    return '/'
  }

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath
      }
    }
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return '/'
  }

  return true
})

export default router
