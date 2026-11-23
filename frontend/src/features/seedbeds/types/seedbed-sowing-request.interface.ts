export interface SeedbedSowingRequest {
  seedCount: number
  germinatedCount: number | null
  transplantedCount: number | null
  notes: string | null
  sownAt: string
  seedPacketId: number
}
