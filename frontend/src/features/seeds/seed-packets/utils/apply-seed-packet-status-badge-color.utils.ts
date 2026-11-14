import type { SeedPacketStatus } from '@/features/seeds/seed-packets/enums/seed-packet-status.enum.ts'

export function applySeedPacketStatusBadgeColor(status: SeedPacketStatus): string {
  switch (status) {
    case 'STORED':
      return 'bg-primary-500 text-primary-100'
    case 'IN_USE':
      return 'bg-blue-200 text-blue-900'
    case 'EMPTY':
      return 'bg-surface-muted text-content'
    default:
      return 'bg-surface-muted text-content'
  }
}
