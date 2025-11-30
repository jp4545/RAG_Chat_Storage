#!/bin/sh
# wait-for-postgres.sh
# Usage: ./wait-for-postgres.sh <host> <port> <timeout_seconds>

HOST=${1:-postgres}
PORT=${2:-5432}
TIMEOUT=${3:-60}

echo "Waiting for Postgres at Host and Port $HOST:$PORT..."

for i in $(seq 1 $TIMEOUT); do
  # Try to connect and run a simple query
  pg_isready -h "$HOST" -p "$PORT" -U admin >/dev/null 2>&1
  if [ $? -eq 0 ]; then
    echo "Postgres is up and ready!"
    exit 0
  fi
  echo "Postgres is unavailable - sleeping..."
  sleep 1
done

echo "Timed out waiting for Postgres"
exit 1
