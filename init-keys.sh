#!/bin/sh

export $(grep -v '^#' .env | xargs)

KEY_DIR=${KEY_DIR}

echo "Checking RSA keys..."

if [ ! -f "$KEY_DIR/private.pem" ]; then
  echo "Generating RSA keys..."

  openssl genrsa -out $KEY_DIR/private.pem 2048
  openssl rsa -in $KEY_DIR/private.pem -pubout -out $KEY_DIR/public.pem

  echo "RSA keys generated."
else
  echo "RSA keys already exist."
fi

exec "$@"