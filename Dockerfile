ARG DJANGO_SECRET_KEY
ARG PYTHON_VERSION=3.13-slim

FROM python:${PYTHON_VERSION}

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# install psycopg2 dependencies.
RUN apt-get update && apt-get install -y \
    libpq-dev \
    gcc \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /project

WORKDIR /project

RUN pip install poetry
COPY pyproject.toml poetry.lock /project/
RUN poetry config virtualenvs.create false
RUN poetry install --only main --no-root --no-interaction
COPY . /project

ENV SECRET_KEY "aKK3nKr9ON6VjobuhCVrZvQGLgQrqz9jIU07UQhIiHTGnnAfti"
WORKDIR /project/src
RUN python manage.py collectstatic --noinput

EXPOSE 8000

CMD ["gunicorn","--bind",":8000","--workers","2","image_gallery.wsgi"]
