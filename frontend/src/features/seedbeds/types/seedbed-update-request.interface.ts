import type { SeedbedLocationType } from '@/features/seedbeds/enums/seedbed-location-type.enum.ts'

export interface SeedbedUpdateRequest {
  name: string
  locationType: SeedbedLocationType
  lengthCm: number | null
  widthCm: number | null
  soilDepthCm: number | null
  hasEnclosure: boolean
  hasHumidityControl: boolean
  hasAutomaticWatering: boolean
  notes: string | null
}
