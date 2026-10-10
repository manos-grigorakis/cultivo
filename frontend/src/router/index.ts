import PlantsListView from '@/views/PlantsListView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/plants',
    },
    {
      path: '/plants',
      name: 'plants',
      component: PlantsListView,
    },
    {
      path: '/plants/:id',
      name: 'plant-details',
      component: () => import('@/views/PlantDetailsView.vue'),
    },
  ],
})

export default router
