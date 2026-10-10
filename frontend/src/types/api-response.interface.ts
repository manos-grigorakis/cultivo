import type { ErrorResponse } from './error-response.interface'

export interface ApiResponse<T> {
  transaction: string
  data: T
  timestamp: string
  error: ErrorResponse
}
