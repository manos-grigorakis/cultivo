<script setup lang="ts">
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import SeedbedDetailsFeaturesCard from '@/features/seedbeds/components/SeedbedDetailsFeaturesCard.vue'
import SeedbedDetailsHeader from '@/features/seedbeds/components/SeedbedDetailsHeader.vue'
import SeedbedDetailsRecordCard from '@/features/seedbeds/components/SeedbedDetailsRecordCard.vue'
import SeedbedDetailsSizeCard from '@/features/seedbeds/components/SeedbedDetailsSizeCard.vue'
import SeedbedDetailsSkeleton from '@/features/seedbeds/components/SeedbedDetailsSkeleton.vue'
import SeedbedDetailsSlotsTable from '@/features/seedbeds/components/SeedbedDetailsSlotsTable.vue'
import SeedbedForm from '@/features/seedbeds/components/SeedbedForm.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const seedbedId = Number(useRoute().params.id)
const {
  seedbed,
  seedbedSlotsList,
  isLoading,
  isDeleting,
  errorMessage,
  fetchSeedbedById,
  deleteSeedbedById,
} = useSeedbeds()

const isEditFormOpen = ref<boolean>(false)
const isDeleteDialogOpen = ref<boolean>(false)

const onSeedbedUpdateSuccess = async () => {
  await fetchSeedbedById(seedbedId)
  isEditFormOpen.value = false
}

const onSeedbedDeleteClick = async () => {
  try {
    const deleted = await deleteSeedbedById(seedbedId)
    if (deleted) await router.push({ name: 'seedbed-list' })
  } finally {
    isDeleteDialogOpen.value = false
  }
}

onMounted(() => fetchSeedbedById(seedbedId))
</script>

<template>
  <SeedbedForm
    v-if="isEditFormOpen && seedbed"
    mode="update"
    :data="seedbed"
    @success="onSeedbedUpdateSuccess"
    @close="isEditFormOpen = false"
  />

  <AppConfirmDialog
    v-if="isDeleteDialogOpen"
    text="Are you sure you want to delete this seedbed? This action cannot be undone."
    confirm-button-text="Yes delete"
    :is-submitting="isDeleting"
    is-danger-operation
    @confirm="onSeedbedDeleteClick"
    @close="isDeleteDialogOpen = false"
  />

  <section class="mx-auto w-full max-w-5xl">
    <template v-if="isLoading">
      <SeedbedDetailsSkeleton />
    </template>

    <template v-else>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

      <template v-if="seedbed">
        <!-- Header -->
        <SeedbedDetailsHeader
          :seedbed="seedbed"
          @edit="isEditFormOpen = true"
          @delete="isDeleteDialogOpen = true"
        />

        <!-- Size & Features -->
        <div class="grid grid-cols-1 gap-2 md:grid-cols-2">
          <SeedbedDetailsSizeCard :seedbed="seedbed" />
          <SeedbedDetailsFeaturesCard :seedbed="seedbed" />
        </div>

        <!-- Slots -->
        <SeedbedDetailsSlotsTable :seedbed="seedbed" :seedbed-slots-list="seedbedSlotsList" />

        <!-- Notes -->
        <div class="rounded-card mt-4 bg-white p-4">
          <h2 class="font-display mb-3 text-xl">Notes</h2>
          <p class="text-sm" :class="seedbed.notes ? 'text-content' : 'text-content-muted'">
            {{ seedbed.notes ?? 'No notes yet' }}
          </p>
        </div>

        <!-- Record -->
        <SeedbedDetailsRecordCard :seedbed="seedbed" />
      </template>
    </template>
  </section>
</template>

<style scoped></style>
