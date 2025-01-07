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
    
    const createButton = screen.getByText('Create Widget')
    await userEvent.click(createButton)
    
    expect(screen.getByText('Create New Widget')).toBeInTheDocument()
    expect(screen.getByLabelText('Description')).toBeInTheDocument()
    expect(screen.getByLabelText('Category')).toBeInTheDocument()
    expect(screen.getByLabelText('Level')).toBeInTheDocument()
  })

  it('creates a new widget successfully', async () => {
    const newWidget = {
      description: 'New Widget',
      category: WidgetCategory.BASIC,
      level: 1,
    }
    ;(widgetService.create as any).mockResolvedValue({
      id: '3',
      ...newWidget,
      userId: 'user1',
    })

    render(<WidgetsPage />)
    
    // Open create dialog
    const createButton = screen.getByText('Create Widget')
    await userEvent.click(createButton)
    
    // Fill form
    const descriptionInput = screen.getByLabelText('Description')
    await userEvent.type(descriptionInput, newWidget.description)
    
    // Submit form
    const submitButton = screen.getByText('Create')
    await userEvent.click(submitButton)
    
    // Verify service was called
    await waitFor(() => {
      expect(widgetService.create).toHaveBeenCalledWith(newWidget)
      expect(widgetService.list).toHaveBeenCalledTimes(2) // Initial load + after create
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
    
    const categorySelect = screen.getByText('All Categories')
    await userEvent.click(categorySelect)
    
    const basicOption = screen.getByText('BASIC')
    await userEvent.click(basicOption)
    
    await waitFor(() => {
      expect(widgetService.list).toHaveBeenCalledWith(
        expect.objectContaining({
          category: WidgetCategory.BASIC,
          page: 0,
        })
      )
    })
  })

  it('handles pagination', async () => {
    render(<WidgetsPage />)
    
    const nextButton = screen.getByText('Next')
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