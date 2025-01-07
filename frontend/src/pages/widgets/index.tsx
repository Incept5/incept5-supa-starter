import React, { useCallback, useEffect, useState, ChangeEvent } from 'react'
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  Input,
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui'
import { useForm, ControllerRenderProps } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Widget, WidgetCategory, WidgetFilters } from '../../types/widget'
import { widgetService } from '../../lib/api/widgetService'
import { useToast } from '../../components/ui/use-toast'
import { useDebounce } from '../../hooks/useDebounce'

const createWidgetSchema = z.object({
  description: z.string().min(3).max(1000),
  category: z.nativeEnum(WidgetCategory),
  level: z.number().min(1).max(10),
})

type CreateWidgetForm = z.infer<typeof createWidgetSchema>

export default function WidgetsPage() {
  const [widgets, setWidgets] = useState<Widget[]>([])
  const [totalPages, setTotalPages] = useState(0)
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const { toast } = useToast()
  
  const [searchTerm, setSearchTerm] = useState('')
  const debouncedSearch = useDebounce(searchTerm)
  
  const [filters, setFilters] = useState<WidgetFilters>({
    page: 0,
    size: 10,
  })

  const form = useForm<CreateWidgetForm>({
    resolver: zodResolver(createWidgetSchema),
    defaultValues: {
      description: '',
      category: WidgetCategory.BASIC,
      level: 1,
    },
  })

  const loadWidgets = useCallback(async (currentFilters: WidgetFilters) => {
    try {
      const response = await widgetService.list(currentFilters)
      setWidgets(response.content)
      setTotalPages(response.totalPages)
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to load widgets',
        variant: 'destructive',
      })
    }
  }, [toast])

  useEffect(() => {
    loadWidgets(filters)
  }, [])

  useEffect(() => {
    const newFilters = {
      ...filters,
      search: debouncedSearch || undefined,
      page: 0
    }
    setFilters(newFilters)
    loadWidgets(newFilters)
  }, [debouncedSearch])

  const handleCategoryChange = useCallback((value: string) => {
    const newFilters = {
      ...filters,
      category: value === "ALL" ? undefined : value as WidgetCategory,
      page: 0,
    }
    setFilters(newFilters)
    loadWidgets(newFilters)
  }, [filters, loadWidgets])

  const handlePageChange = useCallback((newPage: number) => {
    const newFilters = {
      ...filters,
      page: newPage,
    }
    setFilters(newFilters)
    loadWidgets(newFilters)
  }, [filters, loadWidgets])

  const onSubmit = async (data: CreateWidgetForm) => {
    try {
      await widgetService.create(data)
      setIsCreateDialogOpen(false)
      form.reset()
      loadWidgets(filters)
      toast({
        title: 'Success',
        description: 'Widget created successfully',
      })
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to create widget',
        variant: 'destructive',
      })
    }
  }

  return (
    <div className="container mx-auto py-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Widgets</h1>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>Create Widget</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create New Widget</DialogTitle>
            </DialogHeader>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FormField
                  control={form.control}
                  name="description"
                  render={({ field }: { field: ControllerRenderProps<CreateWidgetForm, 'description'> }) => (
                    <FormItem>
                      <FormLabel htmlFor="description">Description</FormLabel>
                      <FormControl>
                        <Input id="description" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="category"
                  render={({ field }: { field: ControllerRenderProps<CreateWidgetForm, 'category'> }) => (
                    <FormItem>
                      <FormLabel htmlFor="category">Category</FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                        aria-label="Category"
                      >
                        <FormControl>
                          <SelectTrigger id="category" aria-label="Category">
                            <SelectValue placeholder="Select a category" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {Object.values(WidgetCategory).map((category) => (
                            <SelectItem key={category} value={category}>
                              {category}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="level"
                  render={({ field }: { field: ControllerRenderProps<CreateWidgetForm, 'level'> }) => (
                    <FormItem>
                      <FormLabel htmlFor="level">Level</FormLabel>
                      <FormControl>
                        <Input
                          id="level"
                          type="number"
                          {...field}
                          onChange={(e: ChangeEvent<HTMLInputElement>) =>
                            field.onChange(parseInt(e.target.value, 10))
                          }
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <Button type="submit" className="w-full">
                  Create
                </Button>
              </form>
            </Form>
          </DialogContent>
        </Dialog>
      </div>

      <div className="mb-6 flex gap-4">
        <Input
          placeholder="Search widgets..."
          className="max-w-sm"
          value={searchTerm}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setSearchTerm(e.target.value)}
        />
        <Select
          value={filters.category || "ALL"}
          onValueChange={handleCategoryChange}
          aria-label="Filter by category"
        >
          <SelectTrigger className="w-[180px]" aria-label="Filter by category">
            <SelectValue placeholder="All Categories" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">All Categories</SelectItem>
            {Object.values(WidgetCategory).map((category) => (
              <SelectItem key={category} value={category}>
                {category}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {widgets.map((widget) => (
          <Card key={widget.id}>
            <CardHeader>
              <CardTitle>{widget.description}</CardTitle>
            </CardHeader>
            <CardContent>
              <p>Category: {widget.category}</p>
              <p>Level: {widget.level}</p>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="mt-6 flex justify-center gap-2">
        <Button
          variant="outline"
          onClick={() => handlePageChange(filters.page! - 1)}
          disabled={filters.page === 0}
        >
          Previous
        </Button>
        <Button
          variant="outline"
          onClick={() => handlePageChange(filters.page! + 1)}
          disabled={filters.page === totalPages - 1}
        >
          Next
        </Button>
      </div>
    </div>
  )
} 