import { AxiosError } from 'axios'

export function handleDefaultApiError(error: unknown): string {
  return error instanceof AxiosError
    ? 'Server error. Please try again.'
    : 'Unexpected error occurred. Please try again.'
}
