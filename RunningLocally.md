* Start docker compose `docker-compose up -d`
* Add poetry to $PATH `export PATH=~/.local/bin:$PATH`
* Generate JWT signing keys
  * `ssh-keygen -t rsa -b 4096 -m PEM -f jwtRS256.key` then
  * `openssl rsa -in jwtRS256.key -pubout -outform PEM -out jwtRS256.key.pub`
* Run migrations `poetry run python manage.py migrate`
* Start the app `MY_DEVELOPER_ID=some-developer-id poetry run python manage.py runserver`
* Generate a jwt key with algo RS256
  * `SECRET=$(cat ../jwtRS256.key) poetry run python generateJwt.py > ../jwt`
* Load Settings Page
  * GET AUTH_TOKEN `curl -H 'AUTHORIZATION: <generated jwt>' http://127.0.0.1:8000/app/get-login-token/`
  * Open settings page `open "http://localhost:8000/app/login/?device-id=<device id from JWT>&device-type=BLACK_AND_WHITE_SCREEN_800X480&&login-token=<login token>"`
* Load Render and open the resulting image
  * curl -H 'AUTHORIZATION: <generated jwt>' "http://localhost:8000/app/render/?device-type=BLACK_AND_WHITE_SCREEN_800X480" -o img.jpg && open img.jpg
