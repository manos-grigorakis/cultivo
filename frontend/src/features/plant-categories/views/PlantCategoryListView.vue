<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import PlantCategoryCard from '@/features/plant-categories/components/PlantCategoryCard.vue'
import PlantCategoryCardSkeleton from '@/features/plant-categories/components/PlantCategoryCardSkeleton.vue'
import PlantCategoryForm from '@/features/plant-categories/components/PlantCategoryForm.vue'
import { usePlantCategories } from '@/features/plant-categories/composables/usePlantCategories.ts'
import { LucidePlus, RefreshCcw } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const { plantCategories, page, isLoading, errorMessage, fetchPlantCategories } =
  usePlantCategories()
const isRefreshing = ref<boolean>(false)

const isCreatePlantCategoryFormOpen = ref<boolean>(false)

const onPlantCategoryCreated = async () => {
  isCreatePlantCategoryFormOpen.value = false
  await fetchPlantCategories()
}

const refreshCategories = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchPlantCategories({ pageNumber: page.value?.number ?? 0 })
  } finally {
    isRefreshing.value = false
  }
}

onMounted(() => {
  fetchPlantCategories()
})
</script>

<template>
  <PlantCategoryForm
    v-if="isCreatePlantCategoryFormOpen"
    mode="create"
    @close="isCreatePlantCategoryFormOpen = false"
    @success="onPlantCategoryCreated"
  />

  <AppSectionWrapper title="Plant Categories">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="RefreshCcw"
            variant="outline"
            @click="refreshCategories()"
            :disabled="isRefreshing"
            :icon-class="isRefreshing ? 'animate-spin' : ''"
          />

          <AppButton
            :icon="LucidePlus"
            label="Add plant category"
            @on-click="isCreatePlantCategoryFormOpen = true"
          />
        </div>
      </div>
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="mt-4" />

    <ul
      v-else-if="isLoading || plantCategories.length > 0"
      class="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
    >
      <template v-if="isLoading">
        <PlantCategoryCardSkeleton v-for="i in 10" :key="i" />
      </template>
      <template v-else>
        <PlantCategoryCard
          v-for="category in plantCategories"
          :key="category.id"
          :plantCategory="category"
        />
      </template>
    </ul>

    <p v-else class="text-center">There are no plant categories recorded yet</p>

    <AppPagination
      :data="page"
      @page-changed="(pageNumber) => fetchPlantCategories({ pageNumber })"
    />
  </AppSectionWrapper>
</template>

<style scoped></style>
