<script setup lang="ts">
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'

defineProps<{ seedPacket: SeedPacketResponse }>()

const months: string[] = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
]
</script>

<template>
  <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
    <h3 class="text-primary-900 font-display mb-2 text-xl">Sowing Calendar</h3>

    <div class="overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr>
            <th scope="col" class="p-1"></th>
            <th v-for="month in months" :key="month" scope="col" class="p-1 font-medium">
              {{ month }}
            </th>
          </tr>
        </thead>

        <tbody>
          <tr>
            <th scope="row" class="p-1">Seedbed</th>
            <td v-for="i in 12" :key="i" class="p-1">
              <div
                class="rounded-card flex h-6 w-8 items-center justify-center"
                :class="
                  seedPacket.sowingSeedbedMonths?.includes(i)
                    ? 'bg-primary-700'
                    : 'bg-content-muted/40'
                "
              >
                <div
                  v-if="seedPacket.sowingSeedbedMonths?.includes(i)"
                  class="size-2 rounded-full bg-white"
                />
              </div>
            </td>
          </tr>

          <tr>
            <th scope="row" class="p-1">Outdoor</th>
            <td v-for="i in 12" :key="i" class="p-1">
              <div
                class="rounded-card flex h-6 w-8 items-center justify-center"
                :class="
                  seedPacket.sowingOutdoorMonths?.includes(i)
                    ? 'bg-accent-500'
                    : 'bg-content-muted/40'
                "
              >
                <div
                  v-if="seedPacket.sowingOutdoorMonths?.includes(i)"
                  class="size-2 rounded-full bg-white"
                />
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="mt-3 flex flex-wrap space-y-1 space-x-6 text-sm">
      <p>
        <b>Seedbed: </b
        >{{ seedPacket.sowingSeedbedMonths?.map((m) => months[m - 1]).join(', ') || '-' }}
      </p>
      <p>
        <b>Outdoor: </b
        >{{ seedPacket.sowingOutdoorMonths?.map((m) => months[m - 1]).join(', ') || '-' }}
      </p>
    </div>
  </div>
</template>

<style scoped></style>
