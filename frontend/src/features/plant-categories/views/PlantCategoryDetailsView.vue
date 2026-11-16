<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import PlantCategoryForm from '@/features/plant-categories/components/PlantCategoryForm.vue'
import PlantCategorySkeleton from '@/features/plant-categories/components/PlantCategorySkeleton.vue'
import { usePlantCategories } from '@/features/plant-categories/composables/usePlantCategories.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { LucidePencil, LucideTrash } from '@lucide/vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const plantCategoryId = Number(useRoute().params.id)

const {
  plantCategory,
  isLoading,
  isDeleting,
  errorMessage,
  fetchPlantCategoryById,
  deletePlantCategoryById,
} = usePlantCategories()

const isPlantCategoryFormOpen = ref<boolean>(false)
const isDeletePlantCategoryModalOpen = ref<boolean>(false)

const onUpdatedPlantCategory = async () => {
  isPlantCategoryFormOpen.value = false
  await fetchPlantCategoryById(plantCategoryId)
}

const onDeletePlantCategory = async () => {
  try {
    const deleted = await deletePlantCategoryById(plantCategoryId)
    if (deleted) await router.push({ name: 'plant-category-list' })
  } finally {
    isDeletePlantCategoryModalOpen.value = false
  }
}

onMounted(() => fetchPlantCategoryById(plantCategoryId))
</script>

<template>
  <PlantCategoryForm
    v-if="isPlantCategoryFormOpen && plantCategory"
    mode="update"
    :plant-category-id="plantCategoryId"
    :data="plantCategory"
    @close="isPlantCategoryFormOpen = false"
    @success="onUpdatedPlantCategory"
  />

  <AppConfirmDialog
    v-if="isDeletePlantCategoryModalOpen && plantCategory"
    :text="`Are you sure you want to delete ${plantCategory.name}? This action cannot be undone.`"
    confirmButtonText="Delete"
    :isSubmitting="isDeleting"
    isDangerOperation
    @close="isDeletePlantCategoryModalOpen = false"
    @confirm="onDeletePlantCategory"
  />

  <section class="mx-auto w-full max-w-5xl">
    <PlantCategorySkeleton v-if="isLoading" />

    <template v-else>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

      <div v-if="plantCategory">
        <div class="mb-1 flex justify-between gap-2">
          <h1 class="font-display text-2xl capitalize">{{ plantCategory.name }}</h1>

          <div class="flex items-center gap-2">
            <AppButton
              :icon="LucidePencil"
              @on-click="isPlantCategoryFormOpen = true"
              variant="outline"
              :disabled="isLoading"
            />

            <AppButton
              :icon="LucideTrash"
              @on-click="isDeletePlantCategoryModalOpen = true"
              variant="danger"
              :disabled="isLoading"
            />
          </div>
        </div>

        <div class="mt-3">
          <p v-if="plantCategory.description" class="text-content">
            {{ plantCategory.description }}
          </p>
          <p v-else class="text-content-muted">No description provided</p>
        </div>

        <div class="border-content-muted/40 mt-3 flex flex-wrap gap-4 border-t pt-3">
          <div class="flex flex-col text-sm">
            <span class="text-content-muted">Created</span>
            <span> {{ formatDate(plantCategory.createdAt) }} </span>
          </div>

          <div class="flex flex-col text-sm">
            <span class="text-content-muted">Updated</span>
            <span>
              {{ plantCategory.updatedAt !== null ? formatDate(plantCategory.updatedAt) : '-' }}
            </span>
          </div>
        </div>
      </div>

      <div v-else>
        <p class="text-center">Plant Category not found</p>
      </div>
    </template>
  </section>
</template>

<style scoped></style>
