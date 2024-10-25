#!/usr/bin/env bash

docker-compose start -d

# wait for the DB to come up
lsof -i :5432
while [ $? -ne 0 ]; do
    lsof -i :5432
done

pushd src
  B64_JWT_PUBLIC_KEY=$(base64 -i local_jwtRSA256.key.pub) \
  DATABASE_URL="postgres://postgres:image_gallery@localhost:5432/image_gallery" \
  LOCAL_DEV_MODE=True \
  MY_DEVELOPER_ID=some-developer-id \
  DJANGO_SECRET_KEY="aKK3nKr9ON6VjobuhCVrZvQGLgQrqz9jIU07UQhIiHTGnnAfti" \
  poetry run python manage.py runserver