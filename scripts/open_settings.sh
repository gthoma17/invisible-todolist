#!/usr/bin/env bash

URL=${URL:-http://localhost:8000}
pushd src
  JWT=$(SECRET=$(cat local_jwtRSA256.key) poetry run python generateJwt.py)
  TOKEN=$(curl -H "AUTHORIZATION: $JWT" "$URL"/app/get-login-token/ | jq -r '.login_token')
  open "$URL/app/login/?device-id=798ddd80-ff88-4b21-994c-582aaf2a84a9&device-type=BLACK_AND_WHITE_SCREEN_800X480&&login-token=$TOKEN"