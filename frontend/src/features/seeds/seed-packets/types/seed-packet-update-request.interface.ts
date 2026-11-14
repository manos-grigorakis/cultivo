import type { SeedPacketStatus } from '@/features/seeds/seed-packets/enums/seed-packet-status.enum.ts'

export interface SeedPacketUpdateRequest {
  brand: string
  supplier: string | null
  status: SeedPacketStatus
  code: string | null
  heightCm: number | null
  weightG: number | null
  plantSpacingCm: number | null
  rowSpacingCm: number | null
  sowingDepthCm: number | null
  germinationDays: number | null
  startHarvestDays: number | null
  sowingOutdoorMonths: number[] | null
  sowingSeedbedMonths: number[] | null
  purchasedAt: string | null
  plantVarietyId: number
}
