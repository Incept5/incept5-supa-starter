import { CreateWidgetRequest, Widget, WidgetFilters, WidgetListResponse } from '../../types/widget'
import { api } from './api'

const WIDGETS_BASE_URL = '/api/widgets'

export const widgetService = {
  list: async (filters: WidgetFilters): Promise<WidgetListResponse> => {
    const params = new URLSearchParams()
    if (filters.page !== undefined) params.append('page', filters.page.toString())
    if (filters.size !== undefined) params.append('size', filters.size.toString())
    if (filters.category) params.append('category', filters.category)
    if (filters.search) params.append('search', filters.search)

    const response = await api.get<WidgetListResponse>(`${WIDGETS_BASE_URL}?${params.toString()}`)
    return response.data
  },

  create: async (widget: CreateWidgetRequest): Promise<Widget> => {
    const response = await api.post<Widget>(WIDGETS_BASE_URL, widget)
    return response.data
  },

  getById: async (id: string): Promise<Widget> => {
    const response = await api.get<Widget>(`${WIDGETS_BASE_URL}/${id}`)
    return response.data
  }
} 