import '@testing-library/jest-dom'
import { afterEach } from 'vitest'
import { cleanup } from '@testing-library/react'
import { vi } from 'vitest'
import React from 'react'

// Mock i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
    i18n: {
      changeLanguage: () => new Promise(() => {}),
    },
  }),
}))

// Mock Supabase
vi.mock('../lib/supabase', () => ({
  supabase: {
    auth: {
      getSession: () => Promise.resolve({ data: { session: null } }),
      onAuthStateChange: () => ({
        data: {
          subscription: {
            unsubscribe: () => {},
          },
        },
      }),
    },
  },
}))

// Mock Radix UI components
vi.mock('../components/ui', async () => {
  const actual = await vi.importActual('../components/ui')
  return {
    ...(actual as any),
    Select: ({ value, onValueChange, children, id, 'aria-label': ariaLabel }: { value: string; onValueChange: (value: string) => void; children: React.ReactNode; id?: string; 'aria-label'?: string }) => {
      return React.createElement('select', { 
        value, 
        onChange: (e: React.ChangeEvent<HTMLSelectElement>) => {
          onValueChange(e.target.value)
        },
        'data-testid': `select-input-${ariaLabel}`,
        id,
        'aria-label': ariaLabel,
        role: 'combobox',
        name: ariaLabel
      }, children)
    },
    SelectTrigger: ({ children, id, 'aria-label': ariaLabel }: { children: React.ReactNode; id?: string; 'aria-label'?: string }) => {
      return null
    },
    SelectValue: ({ children }: { children: React.ReactNode }) => null,
    SelectContent: ({ children }: { children: React.ReactNode }) => children,
    SelectItem: ({ value, children }: { value: string; children: React.ReactNode }) => {
      return React.createElement('option', { value }, children)
    },
    FormField: ({ name, control, render }: any) => {
      const field = {
        value: control?.getValues?.(name) || '',
        onChange: (event: any) => {
          // Handle both direct value changes and event objects
          const value = event?.target?.value ?? event
          if (control?.setValue) {
            control.setValue(name, value, { shouldValidate: true })
          }
        },
        onBlur: () => {},
        name,
        ref: (element: any) => {
          // Set initial value if available
          if (element && control?.getValues) {
            const value = control.getValues(name)
            if (value) {
              element.value = value
            }
          }
        },
      }

      return render({ field })
    },
    Form: ({ children }: { children: React.ReactNode }) => {
      return children
    },
    FormItem: ({ children }: { children: React.ReactNode }) => React.createElement('div', null, children),
    FormLabel: ({ children, htmlFor }: { children: React.ReactNode; htmlFor?: string }) => React.createElement('label', { htmlFor }, children),
    FormControl: ({ children }: { children: React.ReactNode }) => children,
    FormMessage: () => null,
    Dialog: ({ children, open, onOpenChange }: { children: React.ReactNode; open?: boolean; onOpenChange?: (open: boolean) => void }) => {
      return React.createElement('div', null, children)
    },
    DialogTrigger: ({ children, asChild }: { children: React.ReactNode; asChild?: boolean }) => {
      return React.createElement('div', null, children)
    },
    DialogContent: ({ children }: { children: React.ReactNode }) => React.createElement('div', { role: 'dialog' }, children),
    DialogHeader: ({ children }: { children: React.ReactNode }) => React.createElement('div', null, children),
    DialogTitle: ({ children }: { children: React.ReactNode }) => React.createElement('h2', null, children),
    Button: ({ children, onClick, type }: { children: React.ReactNode; onClick?: () => void; type?: string }) => {
      return React.createElement('button', { onClick, type }, children)
    },
  }
})

// Runs a cleanup after each test case
afterEach(() => {
  cleanup()
}) 