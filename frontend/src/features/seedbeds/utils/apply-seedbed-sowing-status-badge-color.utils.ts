import type { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'

export function applySeedbedSowingStatusBadgeColorUtils(status: SeedbedSowingStatus): string {
  switch (status) {
    case 'SOWN':
      return 'bg-primary-500 text-primary-100'
    case 'GERMINATING':
      return 'bg-blue-200 text-blue-900'
    case 'GERMINATED':
      return 'bg-green-200 text-green-900'
    case 'FAILED':
      return 'bg-red-200 text-red-900'
    case 'TRANSPLANTED':
      return 'bg-purple-200 text-purple-900'
    default:
      return 'bg-surface-muted text-content'
  }
}
