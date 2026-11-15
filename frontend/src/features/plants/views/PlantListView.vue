<script lang="ts" setup>
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import PlantCard from '@/features/plants/components/PlantCard.vue'
import PlantCardSkeleton from '@/features/plants/components/PlantCardSkeleton.vue'
import PlantForm from '@/features/plants/components/PlantForm.vue'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import { LucidePlus, RefreshCcw } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const { plants, isLoading, errorMessage, page, fetchPlants } = usePlants()

const isCreatePlantFormOpen = ref<boolean>(false)
const showArchived = ref<boolean>(false)
const isRefreshing = ref<boolean>(false)

const onPlantCreated = async () => {
  isCreatePlantFormOpen.value = false
  await fetchPlants({ archived: showArchived.value })
}

const toggleArchived = async () => {
  showArchived.value = !showArchived.value
  await fetchPlants({ archived: showArchived.value })
}

const refreshPlants = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchPlants({ pageNumber: page.value?.number ?? 0, archived: showArchived.value })
  } finally {
    isRefreshing.value = false
  }
}

onMounted(() => {
  fetchPlants({})
})
</script>

<template>
  <PlantForm
    v-if="isCreatePlantFormOpen"
    mode="create"
    @close="isCreatePlantFormOpen = false"
    @success="onPlantCreated"
  />

  <AppSectionWrapper title="Plants">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="RefreshCcw"
            variant="outline"
            @click="refreshPlants()"
            :disabled="isRefreshing"
            :icon-class="isRefreshing ? 'animate-spin' : ''"
          />

          <AppButton
            :icon="LucidePlus"
            label="Add plant"
            @on-click="isCreatePlantFormOpen = true"
          />
        </div>
      </div>

      <!-- Filters -->
      <div>
        <AppButton
          :label="showArchived ? 'Show Plants' : 'Show Archived'"
          variant="outline"
          @on-click="toggleArchived"
        />
      </div>

      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <ul
        v-else-if="isLoading || plants.length > 0"
        class="grid grid-cols-2 gap-4 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
      >
        <template v-if="isLoading">
          <PlantCardSkeleton v-for="i in 10" :key="i" />
        </template>

        <template v-else>
          <PlantCard v-for="plant of plants" :key="plant.id" :plant="plant" />
        </template>
      </ul>

      <p v-else class="text-center">There are no plants recorded yet</p>

      <AppPagination
        :data="page"
        @page-changed="(pageNumber) => fetchPlants({ pageNumber, archived: showArchived })"
      />
    </div>
  </AppSectionWrapper>
</template>
