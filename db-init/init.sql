DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'admin') THEN

      CREATE ROLE admin WITH
      LOGIN
      PASSWORD 'admin'
      SUPERUSER
      CREATEDB
      CREATEROLE
      INHERIT
      REPLICATION
      BYPASSRLS;

   END IF;
END
$do$;

-- ============================
-- GRANTS TO ADMIN USER
-- ============================

-- Allow connect to DB
GRANT CONNECT ON DATABASE chatdb TO admin;

-- Switch to database
\c chatdb

-- Full schema access
GRANT ALL ON SCHEMA public TO admin;

-- Full access to all existing tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO admin;

-- Full access to all sequences
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO admin;

-- Full access to all functions
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO admin;

-- Auto grant on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON TABLES TO admin;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON SEQUENCES TO admin;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON FUNCTIONS TO admin;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text,
  api_key_hash text NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE TABLE documents (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  title text,
  content text,
  embedding vector(4096),
  created_at timestamptz DEFAULT now()
);

CREATE TABLE sessions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title text,
  is_favorite boolean DEFAULT false,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE messages (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id uuid NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
  sender text NOT NULL,
  content text NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_documents_embedding ON documents USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);