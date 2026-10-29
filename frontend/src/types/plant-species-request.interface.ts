export interface PlantSpeciesRequest {
  name: string
  scientificName: string | null
  description: string | null
  lifeCycle: string | null
  lightRequirement: string | null
  waterRequirement: string | null
  plantCategoryId: number
}
