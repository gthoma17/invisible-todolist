docker-compose up -d

pushd src
  DATABASE_URL="postgres://postgres:image_gallery@localhost:5432/image_gallery" \
  LOCAL_DEV_MODE=True \
  MY_DEVELOPER_ID=some-developer-id \
  DJANGO_SECRET_KEY="aKK3nKr9ON6VjobuhCVrZvQGLgQrqz9jIU07UQhIiHTGnnAfti" \
  poetry run python manage.py runserver