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

    // Plant Categories
    {
      path: '/plant-categories',
      name: 'plant-categories',
      component: () => import('@/views/plant-categories/PlantCategoriesListView.vue'),
    },
    {
      path: '/plant-categories/:id',
      name: 'plant-category-view',
      component: () => import('@/views/plant-categories/PlantCategoryView.vue'),
    },

    // Plant Species
    {
      path: '/plant-species',
      name: 'plant-species',
      component: () => import('@/views/plant-species/PlantSpeciesListView.vue'),
    },
    {
      path: '/plant-species/:id',
      name: 'plant-species-view',
      component: () => import('@/views/plant-species/PlantSpeciesView.vue'),
    },
  ],
})

export default router
