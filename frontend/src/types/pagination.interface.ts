export interface Page {
  size: number
  number: number
  totalElements: number
  totalPages: number
}

export interface Pagination<T> {
  content: T[]
  page: Page
}
