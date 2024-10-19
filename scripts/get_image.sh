pushd src
  RSA_KEY=$(cat jwtRS256.key)
  JWT=$(SECRET=$RSA_KEY poetry run python generateJwt.py)
  curl -H "AUTHORIZATION: $JWT" "http://localhost:8000/app/render/?device-type=BLACK_AND_WHITE_SCREEN_800X480" -o img.jpg
  open img.jpg
