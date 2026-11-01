import type { PlantReferenceResponse } from '@/features/plants/types/plant-reference-response.interface.ts'

export interface PlantResponse {
  id: number
  label: string
  status: string
  archivedAt: string | null
  createdAt: string
  updatedAt: string | null
  species: PlantReferenceResponse | null
  variety: PlantReferenceResponse | null
}
