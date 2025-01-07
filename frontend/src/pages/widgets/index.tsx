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

const createWidgetSchema = z.object({
  description: z.string().min(3).max(1000),
  category: z.nativeEnum(WidgetCategory),
  level: z.number().min(1).max(10),
})

type CreateWidgetForm = z.infer<typeof createWidgetSchema>

export default function WidgetsPage() {
  const [widgets, setWidgets] = useState<Widget[]>([])
  const [filters, setFilters] = useState<WidgetFilters>({
    page: 0,
    size: 10,
  })
  const [totalPages, setTotalPages] = useState(0)
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const { toast } = useToast()

  const form = useForm<CreateWidgetForm>({
    resolver: zodResolver(createWidgetSchema),
    defaultValues: {
      description: '',
      category: WidgetCategory.BASIC,
      level: 1,
    },
  })

  const loadWidgets = useCallback(async () => {
    try {
      const response = await widgetService.list(filters)
      setWidgets(response.content)
      setTotalPages(response.totalPages)
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to load widgets',
        variant: 'destructive',
      })
    }
  }, [filters, toast])

  useEffect(() => {
    loadWidgets()
  }, [loadWidgets])

  const onSubmit = async (data: CreateWidgetForm) => {
    try {
      await widgetService.create(data)
      setIsCreateDialogOpen(false)
      form.reset()
      loadWidgets()
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
                      <FormLabel>Description</FormLabel>
                      <FormControl>
                        <Input {...field} />
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
                      <FormLabel>Category</FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger>
                            <SelectValue placeholder="Select a category" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          <SelectItem value="ALL">All Categories</SelectItem>
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
                      <FormLabel>Level</FormLabel>
                      <FormControl>
                        <Input
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
          value={filters.search || ''}
          onChange={(e: ChangeEvent<HTMLInputElement>) =>
            setFilters((prev: WidgetFilters) => ({ ...prev, search: e.target.value, page: 0 }))
          }
        />
        <Select
          value={filters.category || "ALL"}
          onValueChange={(value: string) =>
            setFilters((prev: WidgetFilters) => ({
              ...prev,
              category: value === "ALL" ? undefined : value as WidgetCategory,
              page: 0,
            }))
          }
        >
          <SelectTrigger className="w-[180px]">
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
          onClick={() =>
            setFilters((prev: WidgetFilters) => ({ ...prev, page: prev.page! - 1 }))
          }
          disabled={filters.page === 0}
        >
          Previous
        </Button>
        <Button
          variant="outline"
          onClick={() =>
            setFilters((prev: WidgetFilters) => ({ ...prev, page: prev.page! + 1 }))
          }
          disabled={filters.page === totalPages - 1}
        >
          Next
        </Button>
      </div>
    </div>
  )
} 