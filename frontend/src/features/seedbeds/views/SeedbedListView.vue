<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import SeedbedCard from '@/features/seedbeds/components/SeedbedCard.vue'
import SeedbedCardSkeleton from '@/features/seedbeds/components/SeedbedCardSkeleton.vue'
import SeedbedForm from '@/features/seedbeds/components/SeedbedForm.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import { LucidePlus, RefreshCcw } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const { seedbedsList, page, isLoading, errorMessage, fetchSeedbeds } = useSeedbeds()

const isRefreshing = ref<boolean>(false)
const isCreateFormOpen = ref<boolean>(false)

const onRefreshClick = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchSeedbeds({ pageNumber: page.value?.number ?? 0 })
  } finally {
    isRefreshing.value = false
  }
}

const onSeedbedCreateSuccess = async () => {
  await fetchSeedbeds({ pageNumber: page.value?.number ?? 0 })
  isCreateFormOpen.value = false
}

onMounted(() => fetchSeedbeds())
</script>

<template>
  <SeedbedForm
    v-if="isCreateFormOpen"
    mode="create"
    @success="onSeedbedCreateSuccess"
    @close="isCreateFormOpen = false"
  />

  <AppSectionWrapper title="Seedbeds">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="RefreshCcw"
            variant="outline"
            @click="onRefreshClick"
            :disabled="isRefreshing"
            :icon-class="isRefreshing ? 'animate-spin' : ''"
          />

          <AppButton :icon="LucidePlus" label="Add seedbed" @on-click="isCreateFormOpen = true" />
        </div>
      </div>

      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <ul
        v-else-if="isLoading || seedbedsList.length > 0"
        class="mt-4 grid grid-cols-2 gap-4 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
      >
        <template v-if="isLoading">
          <SeedbedCardSkeleton v-for="i in 10" :key="i" />
        </template>

        <template v-else>
          <SeedbedCard v-for="seedbed in seedbedsList" :key="seedbed.id" :seedbed="seedbed" />
        </template>
      </ul>

      <p v-else class="text-center">There are no seedbeds recorded yet</p>

      <AppPagination :data="page" @page-changed="(pageNumber) => fetchSeedbeds({ pageNumber })" />
    </div>
  </AppSectionWrapper>
</template>

<style scoped></style>
