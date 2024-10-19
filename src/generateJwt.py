import jwt
import os

developerId = os.getenv("DEVELOPER_ID", default="some-developer-id")
secret = os.environ["SECRET"]

payload = {
    "user_id": "1a07eba0-cc68-4780-ad98-61ca431e527b",
    "developer_id": developerId,
    "device_id": "798ddd80-ff88-4b21-994c-582aaf2a84a9",
    "installation_id": "87f06b41-9fca-49ba-a62a-87da4ed73845"
}
encoded_jwt = jwt.encode(payload, secret, algorithm="RS256")
print(encoded_jwt)
