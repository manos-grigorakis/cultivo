export interface PlantSpeciesRequest {
  name: string
  description: string | null
  lifeCycle: string | null
  lightRequirement: string | null
  waterRequirement: string | null
  plantCategoryId: number
}
