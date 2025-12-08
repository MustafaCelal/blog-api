# Blog API - Security Test Script

## Test 1: Login as Admin
echo "=== Test 1: Login as Admin ==="
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }' | jq

## Test 2: Try to create post without authentication (should fail)
echo -e "\n=== Test 2: Create post without auth (should fail with 403) ==="
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Post",
    "summary": "This should fail",
    "content": "No authentication provided"
  }' | jq

## Test 3: Get all posts (should work without auth)
echo -e "\n=== Test 3: Get all posts (public endpoint) ==="
curl -X GET http://localhost:8080/api/posts | jq

echo -e "\n\n=== Tests Complete ==="
echo "Next: Save the JWT token from Test 1 and use it in Authorization header"
echo "Example: curl -H 'Authorization: Bearer YOUR_TOKEN' ..."
