export enum WidgetCategory {
  BASIC = 'BASIC',
  PREMIUM = 'PREMIUM',
  ENTERPRISE = 'ENTERPRISE'
}

export interface Widget {
  id: string
  description: string
  category: WidgetCategory
  level: number
  userId: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateWidgetRequest {
  description: string
  category: WidgetCategory
  level: number
}

export interface WidgetListResponse {
  content: Widget[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface WidgetFilters {
  page?: number
  size?: number
  category?: WidgetCategory
  search?: string
} 