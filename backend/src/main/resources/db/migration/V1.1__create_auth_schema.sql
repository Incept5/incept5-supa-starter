-- Note that normally this is a NOOP but when testing we need to create the schema the AUTH schema because GOTRUE is not installed or running

-- Create auth schema
CREATE SCHEMA IF NOT EXISTS auth;

-- Create auth enums
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'aal_level') THEN
        CREATE TYPE auth.aal_level AS ENUM ('aal1', 'aal2', 'aal3');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'code_challenge_method') THEN
        CREATE TYPE auth.code_challenge_method AS ENUM ('s256', 'plain');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'factor_status') THEN
        CREATE TYPE auth.factor_status AS ENUM ('unverified', 'verified');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'factor_type') THEN
        CREATE TYPE auth.factor_type AS ENUM ('totp', 'webauthn');
    END IF;
END $$;

-- Create auth tables
CREATE TABLE IF NOT EXISTS auth.users (
  id uuid NOT NULL PRIMARY KEY,
  instance_id uuid NULL,
  aud varchar(255) NULL,
  role varchar(255) NULL,
  email varchar(255) NULL,
  encrypted_password varchar(255) NULL,
  email_confirmed_at timestamptz NULL,
  invited_at timestamptz NULL,
  confirmation_token varchar(255) NULL,
  confirmation_sent_at timestamptz NULL,
  recovery_token varchar(255) NULL,
  recovery_sent_at timestamptz NULL,
  email_change_token varchar(255) NULL,
  email_change varchar(255) NULL,
  email_change_sent_at timestamptz NULL,
  last_sign_in_at timestamptz NULL,
  raw_app_meta_data jsonb NULL,
  raw_user_meta_data jsonb NULL,
  is_super_admin bool NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL,
  phone text NULL UNIQUE,
  phone_confirmed_at timestamptz NULL,
  phone_change text NULL DEFAULT '',
  phone_change_token varchar(255) NULL DEFAULT '',
  phone_change_sent_at timestamptz NULL,
  confirmed_at timestamptz NULL,
  email_change_confirm_status smallint NULL DEFAULT 0,
  banned_until timestamptz NULL,
  reauthentication_token varchar(255) NULL DEFAULT '',
  reauthentication_sent_at timestamptz NULL,
  is_sso_user boolean NOT NULL DEFAULT false,
  deleted_at timestamptz NULL
);

CREATE TABLE IF NOT EXISTS auth.refresh_tokens (
  id bigserial PRIMARY KEY,
  token varchar(255) NULL,
  user_id varchar(255) NULL,
  revoked boolean NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL,
  parent varchar(255) NULL,
  session_id uuid NULL
);

CREATE TABLE IF NOT EXISTS auth.instances (
  id uuid NOT NULL PRIMARY KEY,
  uuid uuid NULL,
  raw_base_config text NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL
);

CREATE TABLE IF NOT EXISTS auth.audit_log_entries (
  id bigserial PRIMARY KEY,
  payload json NULL,
  created_at timestamptz NULL,
  ip_address varchar(64) NULL DEFAULT ''::character varying
);

CREATE TABLE IF NOT EXISTS auth.schema_migrations (
  version varchar(255) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS auth.mfa_factors (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid NOT NULL,
  friendly_name text NULL,
  factor_type auth.factor_type NOT NULL,
  status auth.factor_status NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  secret text NULL
);

CREATE TABLE IF NOT EXISTS auth.mfa_challenges (
  id uuid NOT NULL PRIMARY KEY,
  factor_id uuid NOT NULL,
  created_at timestamptz NOT NULL,
  verified_at timestamptz NULL,
  ip_address inet NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.mfa_amr_claims (
  id uuid NOT NULL PRIMARY KEY,
  session_id uuid NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  authentication_method text NOT NULL,
  provider text NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.flow_state (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid NULL,
  auth_code text NOT NULL,
  code_challenge_method auth.code_challenge_method NOT NULL,
  code_challenge text NOT NULL,
  provider_type text NOT NULL,
  provider_access_token text NULL,
  provider_refresh_token text NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  authentication_method text NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.sso_providers (
  id uuid NOT NULL PRIMARY KEY,
  resource_id text NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL
);

CREATE TABLE IF NOT EXISTS auth.sso_domains (
  id uuid NOT NULL PRIMARY KEY,
  sso_provider_id uuid NOT NULL,
  domain text NOT NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL,
  CONSTRAINT sso_domains_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auth.saml_providers (
  id uuid NOT NULL PRIMARY KEY,
  sso_provider_id uuid NOT NULL,
  entity_id text NOT NULL,
  metadata_xml text NOT NULL,
  metadata_url text NULL,
  attribute_mapping jsonb NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL,
  CONSTRAINT saml_providers_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auth.saml_relay_states (
  id uuid NOT NULL PRIMARY KEY,
  sso_provider_id uuid NOT NULL,
  request_id text NOT NULL,
  for_email text NULL,
  redirect_to text NULL,
  from_ip_address inet NULL,
  created_at timestamptz NULL,
  updated_at timestamptz NULL,
  CONSTRAINT saml_relay_states_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auth.sso_sessions (
  id uuid NOT NULL PRIMARY KEY,
  session_id uuid NOT NULL,
  sso_provider_id uuid NOT NULL,
  not_before timestamptz NULL,
  not_after timestamptz NULL,
  idp_initiated boolean NULL DEFAULT false,
  created_at timestamptz NULL,
  updated_at timestamptz NULL,
  CONSTRAINT sso_sessions_sso_provider_id_fkey FOREIGN KEY (sso_provider_id) REFERENCES auth.sso_providers(id) ON DELETE CASCADE
);

-- Create necessary indexes
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'users_instance_id_idx') THEN
        CREATE INDEX users_instance_id_idx ON auth.users(instance_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'users_email_idx') THEN
        CREATE INDEX users_email_idx ON auth.users(email);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'refresh_tokens_token_idx') THEN
        CREATE INDEX refresh_tokens_token_idx ON auth.refresh_tokens(token);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'refresh_tokens_user_id_idx') THEN
        CREATE INDEX refresh_tokens_user_id_idx ON auth.refresh_tokens(user_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'refresh_tokens_session_id_idx') THEN
        CREATE INDEX refresh_tokens_session_id_idx ON auth.refresh_tokens(session_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'mfa_factors_user_id_idx') THEN
        CREATE INDEX mfa_factors_user_id_idx ON auth.mfa_factors(user_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'mfa_challenges_factor_id_idx') THEN
        CREATE INDEX mfa_challenges_factor_id_idx ON auth.mfa_challenges(factor_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'flow_state_user_id_idx') THEN
        CREATE INDEX flow_state_user_id_idx ON auth.flow_state(user_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'sso_domains_sso_provider_id_idx') THEN
        CREATE INDEX sso_domains_sso_provider_id_idx ON auth.sso_domains(sso_provider_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'saml_providers_sso_provider_id_idx') THEN
        CREATE INDEX saml_providers_sso_provider_id_idx ON auth.saml_providers(sso_provider_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'saml_relay_states_sso_provider_id_idx') THEN
        CREATE INDEX saml_relay_states_sso_provider_id_idx ON auth.saml_relay_states(sso_provider_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'sso_sessions_session_id_idx') THEN
        CREATE INDEX sso_sessions_session_id_idx ON auth.sso_sessions(session_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'sso_sessions_sso_provider_id_idx') THEN
        CREATE INDEX sso_sessions_sso_provider_id_idx ON auth.sso_sessions(sso_provider_id);
    END IF;
END $$; 