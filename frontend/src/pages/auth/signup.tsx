import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../lib/auth.context'
import { Button } from '../../components/ui/button'
import { Input } from '../../components/ui/input'
import { Card } from '../../components/ui/card'
import { useToast } from '../../hooks/use-toast'
import { getThemeClass } from '../../lib/theme'
import { Alert, AlertDescription } from '../../components/ui/alert'

export function SignupPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [formError, setFormError] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const { signup } = useAuth()
  const navigate = useNavigate()
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setFormError('')
    setIsLoading(true)
    try {
      const { session, user } = await signup(email, password)
      
      if (session) {
        // User is signed in immediately
        toast({
          title: 'Success',
          description: 'Account created successfully',
        })
        navigate('/')
      } else {
        // Email confirmation is required
        toast({
          title: 'Success',
          description: 'Please check your email to confirm your account',
        })
        navigate('/login')
      }
    } catch (error: any) {
      const errorMessage = error.message || 'Failed to create account'
      setFormError(errorMessage)
      toast({
        title: 'Error',
        description: errorMessage,
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className={`flex min-h-screen items-center justify-center ${getThemeClass('gradients.primary')}`}>
      <Card className={`w-full max-w-md p-6 ${getThemeClass('components.card.base')}`}>
        <h1 className={`mb-6 text-2xl font-bold ${getThemeClass('components.text.heading')}`}>Sign Up</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={isLoading}
              className={getThemeClass('components.input.base')}
            />
          </div>
          <div>
            <Input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={isLoading}
              className={getThemeClass('components.input.base')}
            />
          </div>
          {formError && (
            <Alert variant="destructive" className="mt-4">
              <AlertDescription>{formError}</AlertDescription>
            </Alert>
          )}
          <Button 
            type="submit" 
            className={`w-full ${getThemeClass('components.button.primary')}`}
            disabled={isLoading}
          >
            {isLoading ? 'Creating account...' : 'Sign Up'}
          </Button>
        </form>
        <p className={`mt-4 text-center ${getThemeClass('components.text.body')}`}>
          Already have an account?{' '}
          <Button 
            variant="link" 
            onClick={() => navigate('/login')} 
            className={getThemeClass('components.button.link')}
            disabled={isLoading}
          >
            Login
          </Button>
        </p>
      </Card>
    </div>
  )
}