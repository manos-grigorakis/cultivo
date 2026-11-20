import { SeedbedLocationType } from '@/features/seedbeds/enums/seedbed-location-type.enum.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'

export interface SeedbedDetailResponse {
  id: number
  name: string
  locationType: SeedbedLocationType
  rowsCount: number
  columnsCount: number
  lengthCm: number | null
  widthCm: number | null
  soilDepthCm: number | null
  hasEnclosure: boolean
  hasHumidityControl: boolean
  hasAutomaticWatering: boolean
  notes: string | null
  createdAt: string
  updatedAt: string | null
  seedbedSlotsSummary: SeedbedSlotSummaryResponse[]
}
