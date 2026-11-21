import PlantListView from '@/features/plants/views/PlantListView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/plants',
    },

    // Plants
    {
      path: '/plants',
      name: 'plant',
      component: PlantListView,
    },
    {
      path: '/plants/:id',
      component: () => import('@/features/plants/views/PlantDetailsView.vue'),
      children: [
        {
          path: '',
          name: 'plant-details',
          redirect: { name: 'plant-details-overview' },
        },
        {
          path: 'overview',
          name: 'plant-details-overview',
          component: () => import('@/features/plants/views/tabs/PlantOverviewView.vue'),
        },
        {
          path: 'journal',
          name: 'plant-details-journal',
          component: () => import('@/features/plants/views/tabs/PlantJournalView.vue'),
        },
      ],
    },

    // Plant Categories
    {
      path: '/plant-categories',
      name: 'plant-category-list',
      component: () => import('@/features/plant-categories/views/PlantCategoryListView.vue'),
    },
    {
      path: '/plant-categories/:id',
      name: 'plant-category-details',
      component: () => import('@/features/plant-categories/views/PlantCategoryDetailsView.vue'),
    },

    // Plant Species
    {
      path: '/plant-species',
      name: 'plant-species',
      component: () => import('@/features/plant-species/views/PlantSpeciesListView.vue'),
    },
    {
      path: '/plant-species/:id',
      component: () => import('@/features/plant-species/views/PlantSpeciesDetailsView.vue'),
      children: [
        {
          path: '',
          name: 'plant-species-details',
          redirect: { name: 'plant-species-details-overview' },
        },
        {
          path: 'overview',
          name: 'plant-species-details-overview',
          component: () =>
            import('@/features/plant-species/views/tabs/PlantSpeciesDetailsOverviewView.vue'),
        },
        {
          path: 'varieties',
          name: 'plant-species-details-varieties',
          component: () =>
            import('@/features/plant-species/views/tabs/PlantSpeciesDetailsVarietiesView.vue'),
        },
        {
          path: 'plants',
          name: 'plant-species-details-plants',
          component: () =>
            import('@/features/plant-species/views/tabs/PlantSpeciesDetailsPlantsView.vue'),
        },
      ],
    },

    // Plant Varieties
    {
      path: '/plant-varieties',
      name: 'plant-variety-list',
      component: () => import('@/features/plant-varieties/views/PlantVarietyListView.vue'),
    },
    {
      path: '/plant-varieties/:id',
      name: 'plant-variety-details',
      component: () => import('@/features/plant-varieties/views/PlantVarietyDetailsView.vue'),
    },

    // Seed Packets
    {
      path: '/seed-packets',
      name: 'seed-packet-list',
      component: () => import('@/features/seeds/seed-packets/views/SeedPacketListView.vue'),
    },
    {
      path: '/seed-packets/:id',
      name: 'seed-packet-details',
      component: () => import('@/features/seeds/seed-packets/views/SeedPacketDetailsView.vue'),
    },

    // Seedbeds
    {
      path: '/seedbeds',
      name: 'seedbed-list',
      component: () => import('@/features/seedbeds/views/SeedbedListView.vue'),
    },
    {
      path: '/seedbeds/:id',
      component: () => import('@/features/seedbeds/views/SeedbedDetailsView.vue'),
      children: [
        { path: '', name: 'seedbed-details', redirect: { name: 'seedbed-details-overview' } },
        {
          path: 'overview',
          name: 'seedbed-details-overview',
          component: () => import('@/features/seedbeds/views/tabs/SeedbedOverviewView.vue'),
        },
        {
          path: 'sowings',
          name: 'seedbed-details-sowings',
          component: () => import('@/features/seedbeds/views/tabs/SeedbedSowingsView.vue'),
        },
      ],
    },
    // Seedbed Sowings
    {
      path: '/seedbed-sowings/:id',
      name: 'seedbed-sowing-details',
      component: () => import('@/features/seedbeds/views/SeedbedSowingDetailsView.vue'),
    },
  ],
})

export default router
