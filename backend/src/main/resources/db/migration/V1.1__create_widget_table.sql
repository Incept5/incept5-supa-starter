-- Create public schema
CREATE TABLE public.widget (
    id VARCHAR(26) PRIMARY KEY,
    user_id UUID NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    level INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_widget_user_id ON public.widget(user_id);
CREATE INDEX idx_widget_category ON public.widget(category);
