export interface SeedPacketCreateRequest {
  brand: string
  supplier: string | null
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
