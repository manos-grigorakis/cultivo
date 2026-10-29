interface PlantCategorySummary {
  id: number
  name: string
}

export interface PlantSpeciesResponse {
  id: number
  name: string
  scientificName: string | null
  description: string | null
  lifeCycle: string | null
  lightRequirement: string | null
  waterRequirement: string | null
  createdAt: string
  updatedAt: string | null
  plantCategory: PlantCategorySummary
}
