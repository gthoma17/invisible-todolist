#!/usr/bin/env bash

URL=${URL:-http://localhost:8000}

pushd src
  if [ -z "$JWT" ]; then
    JWT=$(SECRET=$(cat local_jwtRSA256.key) poetry run python generateJwt.py);
  fi

  curl -H "AUTHORIZATION: $JWT" "http://localhost:8000/app/render/?device-type=BLACK_AND_WHITE_SCREEN_800X480" -o img.jpg
  open img.jpg
