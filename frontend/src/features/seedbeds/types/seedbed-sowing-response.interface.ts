import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import type { SeedPacketSummaryResponse } from '@/features/seedbeds/types/seed-packet-summary-response.interface.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'
import type { SeedBedsSummaryResponse } from '@/features/seedbeds/types/seedbed-summary-response.interface.ts'

export interface SeedbedSowingResponse {
  id: number
  status: SeedbedSowingStatus
  seedCount: number
  germinatedCount: number | null
  transplantedCount: number | null
  notes: string | null
  sownAt: string
  completedAt: string
  createdAt: string
  updatedAt: string | null
  seedbedSlot: SeedbedSlotSummaryResponse
  seedPacket: SeedPacketSummaryResponse
  seedbed: SeedBedsSummaryResponse
}
