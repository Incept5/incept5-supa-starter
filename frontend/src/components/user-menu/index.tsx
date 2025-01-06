import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../lib/auth.context'
import { Button } from '../ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu'
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar'

export function UserMenu() {
  const { isAuthenticated, user, logout } = useAuth()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = React.useState(false)

  const handleLogout = async () => {
    setIsLoading(true)
    try {
      await logout()
      navigate('/login')
    } catch (error) {
      console.error('Error logging out:', error)
    } finally {
      setIsLoading(false)
    }
  }

  if (!isAuthenticated) {
    return (
      <div className="flex gap-2">
        <Button variant="ghost" onClick={() => navigate('/login')} disabled={isLoading}>
          Login
        </Button>
        <Button onClick={() => navigate('/signup')} disabled={isLoading}>
          Sign Up
        </Button>
      </div>
    )
  }

  if (!user) {
    return null
  }

  const userInitial = user.email?.[0]?.toUpperCase() || '?'

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative h-8 w-8 rounded-full">
          <Avatar className="h-8 w-8">
            {user.user_metadata?.avatar_url ? (
              <AvatarImage src={user.user_metadata.avatar_url} alt={user.email || ''} />
            ) : (
              <AvatarFallback>{userInitial}</AvatarFallback>
            )}
          </Avatar>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={handleLogout} disabled={isLoading}>
          {isLoading ? 'Logging out...' : 'Logout'}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}