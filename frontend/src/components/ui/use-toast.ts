import { toast } from 'sonner'

export const useToast = () => ({
  toast: (options: { title: string; description?: string; variant?: 'default' | 'destructive' }) => {
    if (options.variant === 'destructive') {
      toast.error(options.title, {
        description: options.description,
      })
    } else {
      toast.success(options.title, {
        description: options.description,
      })
    }
  },
}) 