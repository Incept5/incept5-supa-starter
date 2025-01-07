import React, { ReactNode } from 'react'
import { LanguageSelector } from '../language-selector'
import { UserMenu } from '../user-menu'
import { useTranslation } from 'react-i18next'
import { Link, useLocation } from 'react-router-dom'
import { cn } from '../../lib/utils'

interface IProps {
  leftNode?: ReactNode
}

export function Header(props: IProps) {
  const { t } = useTranslation()
  const location = useLocation()

  const navLinks = [
    { href: '/', label: t('home') },
    { href: '/widgets', label: t('widgets') },
  ]

  return (
    <div className="fixed left-0 top-0 flex w-full items-center justify-between border bg-slate-50 bg-opacity-70 px-4 py-4 md:px-12">
      <div className="flex items-center gap-8">
        <Link to="/" className="flex items-center gap-2">
          <img 
            src="https://pbs.twimg.com/profile_images/1285208896060129281/kpFksf-W_400x400.jpg" 
            alt="Incept5 Logo" 
            className="h-8 w-8 rounded-full"
          />
          <span className="text-xs md:text-base">{t('title')}</span>
        </Link>
        <nav className="hidden md:flex items-center gap-4">
          {navLinks.map((link) => (
            <Link
              key={link.href}
              to={link.href}
              className={cn(
                'text-sm font-medium transition-colors hover:text-primary',
                location.pathname === link.href
                  ? 'text-foreground'
                  : 'text-foreground/60'
              )}
            >
              {link.label}
            </Link>
          ))}
        </nav>
      </div>
      <div className="flex items-center gap-4">
        <LanguageSelector />
        <UserMenu />
      </div>
    </div>
  )
}
