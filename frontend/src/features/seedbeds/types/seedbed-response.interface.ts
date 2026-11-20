import { SeedbedLocationType } from '@/features/seedbeds/enums/seedbed-location-type.enum.ts'

export interface SeedbedResponse {
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
}
