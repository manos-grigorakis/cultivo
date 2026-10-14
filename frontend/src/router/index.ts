import PlantsListView from '@/views/plants/PlantsListView.vue'
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

      component: () => import('@/views/plants/PlantDetailsView.vue'),
      children: [
        {
          path: '',
          name: 'plant-details',
          redirect: { name: 'plant-details-overview' },
        },
        {
          path: 'overview',
          name: 'plant-details-overview',
          component: () => import('@/views/plants/PlantDetailsOverviewView.vue'),
        },
        {
          path: 'journal',
          name: 'plant-details-journal',
          component: () => import('@/views/plants/PlantDetailsJournalView.vue'),
        },
      ],
    },
  ],
})

export default router
