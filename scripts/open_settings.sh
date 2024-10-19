pushd src
  RSA_KEY=$(cat jwtRS256.key)
  JWT=$(SECRET=$RSA_KEY poetry run python generateJwt.py)
  TOKEN=$(curl -H "AUTHORIZATION: $JWT" http://127.0.0.1:8000/app/get-login-token/ | jq -r '.login_token')
  echo $TOKEN
  open "http://localhost:8000/app/login/?device-id=798ddd80-ff88-4b21-994c-582aaf2a84a9&device-type=BLACK_AND_WHITE_SCREEN_800X480&&login-token=$TOKEN"