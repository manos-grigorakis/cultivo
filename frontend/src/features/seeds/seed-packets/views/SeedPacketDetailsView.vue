<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import SeedPacketDetailsCard from '@/features/seeds/seed-packets/components/SeedPacketDetailsCard.vue'
import SeedPacketDetailsGrowingSpecsCard from '@/features/seeds/seed-packets/components/SeedPacketDetailsGrowingSpecsCard.vue'
import SeedPacketDetailsSkeleton from '@/features/seeds/seed-packets/components/SeedPacketDetailsSkeleton.vue'
import SeedPacketDetailsSowingCard from '@/features/seeds/seed-packets/components/SeedPacketDetailsSowingCard.vue'
import { useSeedPackets } from '@/features/seeds/seed-packets/composables/useSeedPackets.ts'
import { applySeedPacketStatusBadgeColor } from '@/features/seeds/seed-packets/utils/apply-seed-packet-status-badge-color.utils.ts'
import { LucidePencil, Trash2 } from '@lucide/vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const packetId = Number(useRoute().params.id)
const {
  isLoading,
  isDeleting,
  errorMessage,
  seedPacket,
  fetchSeedPacketById,
  deleteSeedPacketById,
} = useSeedPackets()
const isDeleteDialogOpen = ref<boolean>(false)

const onSeedPacketDelete = async () => {
  if (isDeleting.value || !seedPacket.value) {
    router.push({ name: 'seed-packet-list' })
    return
  }

  const deleted = await deleteSeedPacketById(packetId)
  if (!deleted) return

  isDeleteDialogOpen.value = false
  router.push({ name: 'seed-packet-list' })
}

onMounted(() => fetchSeedPacketById(packetId))
</script>

<template>
  <AppConfirmDialog
    v-if="isDeleteDialogOpen && seedPacket"
    :text="`Are you sure that you want to delete '${seedPacket.brand} - ${seedPacket.code}'`"
    confirm-button-text="Yes, delete"
    :is-submitting="isDeleting"
    is-danger-operation
    @close="isDeleteDialogOpen = false"
    @confirm="onSeedPacketDelete"
  />

  <section class="mx-auto w-full max-w-5xl">
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="mb-4" />

    <SeedPacketDetailsSkeleton v-if="isLoading" />

    <div v-if="!isLoading && seedPacket">
      <!-- Header -->
      <div class="flex flex-wrap justify-between gap-2">
        <div>
          <h1 class="font-display mb-2 text-2xl">{{ seedPacket.brand }}</h1>

          <div class="flex items-center gap-2">
            <span
              class="rounded-card px-4 py-1 text-sm"
              :class="applySeedPacketStatusBadgeColor(seedPacket.status)"
              >{{ seedPacket.status.replace('_', ' ') }}</span
            >

            <div class="text-content-muted text-sm">
              <span>{{ seedPacket.plantVariety.name }}</span>
              <span class="mx-1">-</span>
              <span>{{ seedPacket.code }}</span>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex items-center gap-2">
          <AppButton :icon="LucidePencil" variant="outline" />
          <AppButton :icon="Trash2" variant="danger" @on-click="isDeleteDialogOpen = true" />
        </div>
      </div>

      <div class="grid grid-cols-1 gap-2 lg:grid-cols-2">
        <SeedPacketDetailsCard :seed-packet="seedPacket" />
        <SeedPacketDetailsGrowingSpecsCard :seed-packet="seedPacket" />
        <SeedPacketDetailsSowingCard :seed-packet="seedPacket" class="lg:col-span-2" />
      </div>
    </div>
    <div v-else>
      <p class="text-center">Seed Packet not found</p>
    </div>
  </section>
</template>

<style scoped></style>
