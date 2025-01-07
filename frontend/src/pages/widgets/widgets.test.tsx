import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor, fireEvent } from '../../test/utils'
import userEvent from '@testing-library/user-event'
import WidgetsPage from './index'
import { widgetService } from '../../lib/api/widgetService'
import { WidgetCategory } from '../../types/widget'

// Mock the widget service
vi.mock('../../lib/api/widgetService', () => ({
  widgetService: {
    list: vi.fn(),
    create: vi.fn(),
  },
}))

describe('WidgetsPage', () => {
  const mockWidgets = [
    {
      id: '1',
      description: 'Test Widget 1',
      category: WidgetCategory.BASIC,
      level: 1,
      userId: 'user1',
    },
    {
      id: '2',
      description: 'Test Widget 2',
      category: WidgetCategory.PREMIUM,
      level: 2,
      userId: 'user1',
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
    // Mock successful widget list response
    ;(widgetService.list as any).mockResolvedValue({
      content: mockWidgets,
      totalPages: 1,
      totalElements: 2,
      size: 10,
      number: 0,
    })
  })

  it('renders the widgets page with title', async () => {
    render(<WidgetsPage />)
    expect(screen.getByText('Widgets')).toBeInTheDocument()
  })

  it('displays widgets from the API', async () => {
    render(<WidgetsPage />)
    
    await waitFor(() => {
      expect(screen.getByText('Test Widget 1')).toBeInTheDocument()
      expect(screen.getByText('Test Widget 2')).toBeInTheDocument()
    })
  })

  it('opens create widget dialog when clicking create button', async () => {
    render(<WidgetsPage />)
    
    const createButton = screen.getByRole('button', { name: 'Create Widget' })
    await userEvent.click(createButton)
    
    await waitFor(() => {
      expect(screen.getByText('Create New Widget')).toBeInTheDocument()
      expect(screen.getByRole('textbox', { name: 'Description' })).toBeInTheDocument()
      expect(screen.getByRole('combobox', { name: 'Category' })).toBeInTheDocument()
      expect(screen.getByRole('spinbutton', { name: 'Level' })).toBeInTheDocument()
    })
  })

  it('filters widgets by search term', async () => {
    render(<WidgetsPage />)
    
    const searchInput = screen.getByPlaceholderText('Search widgets...')
    await userEvent.type(searchInput, 'test')
    
    await waitFor(() => {
      expect(widgetService.list).toHaveBeenCalledWith(
        expect.objectContaining({
          search: 'test',
          page: 0,
        })
      )
    })
  })

  it('filters widgets by category', async () => {
    render(<WidgetsPage />)
    
    const select = screen.getByRole('combobox', { name: 'Filter by category' })
    await userEvent.selectOptions(select, 'BASIC')
    
    await waitFor(() => {
      expect(widgetService.list).toHaveBeenCalledWith(
        expect.objectContaining({
          category: 'BASIC',
          page: 0,
        })
      )
    })
  })

  it('handles pagination', async () => {
    // Mock more pages
    ;(widgetService.list as any).mockResolvedValue({
      content: mockWidgets,
      totalPages: 2,
      totalElements: 4,
      size: 10,
      number: 0,
    })

    render(<WidgetsPage />)
    
    const nextButton = screen.getByRole('button', { name: 'Next' })
    await userEvent.click(nextButton)
    
    await waitFor(() => {
      expect(widgetService.list).toHaveBeenCalledWith(
        expect.objectContaining({
          page: 1,
        })
      )
    })
  })
}) 