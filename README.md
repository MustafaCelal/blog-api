# Blog API

Java Spring Boot ile geliştirilmiş temiz, sürdürülebilir ve ölçeklenebilir bir blog REST API'si.

## Teknolojiler

- **Java 21** + **Spring Boot 3.3.6**
- **Spring Data JPA** + **PostgreSQL**
- **Spring Security** + **JWT Authentication**
- **Flyway** - Database Migration & Versioning
- **Lombok** + **Hibernate Validator**

## Proje Yapısı

```
src/main/java/com/raptiye/blog/
├── BlogApplication.java          # Ana uygulama
├── domain/                        # Entity'ler (Post, Comment, Tag)
├── repository/                    # Data Access Layer
├── service/                       # Business Logic
├── controller/                    # REST API
├── dto/                           # Request/Response DTOs
├── mapper/                        # Entity ↔ DTO
└── exception/                     # Error Handling
```

## Database Schema

```
POST ||--o{ COMMENT : has
POST }o--o{ TAG : has

POST: id, title, slug, summary, content, published, created_at, updated_at
COMMENT: id, author_name, author_email, content, approved, post_id, created_at
TAG: id, name, slug, created_at
```

## API Documentation (Swagger UI)

API dokümantasyonuna ve test arayüzüne aşağıdaki adresten erişebilirsiniz:
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Kullanıcı kaydı | No |
| POST | `/api/auth/login` | Kullanıcı girişi | No |

### Posts
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/posts` | Tüm postları listele | No |
| GET | `/api/posts/{slug}` | Slug ile post detayı | No |
| GET | `/api/posts/id/{id}` | ID ile post detayı | No |
| GET | `/api/posts/tag/{tagSlug}` | Tag'e göre postlar | No |
| POST | `/api/posts` | Yeni post oluştur | ✅ ADMIN |
| PUT | `/api/posts/{id}` | Post güncelle | ✅ ADMIN |
| DELETE | `/api/posts/{id}` | Post sil | ✅ ADMIN |
| POST | `/api/posts/{postId}/tags/{tagId}` | Tag ekle | ✅ ADMIN |
| DELETE | `/api/posts/{postId}/tags/{tagId}` | Tag çıkar | ✅ ADMIN |

### Comments
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/posts/{postId}/comments` | Yorumları listele | No |
| POST | `/api/posts/{postId}/comments` | Yorum ekle | No |
| GET | `/api/comments/pending` | Onay bekleyen yorumlar | ✅ ADMIN |
| PUT | `/api/comments/{id}/approve` | Yorumu onayla | ✅ ADMIN |
| DELETE | `/api/comments/{id}` | Yorum sil | ✅ ADMIN |

### Tags
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/tags` | Tüm tag'leri listele | No |
| GET | `/api/tags/{slug}` | Tag detayı | No |
| POST | `/api/tags` | Yeni tag oluştur | ✅ ADMIN |
| DELETE | `/api/tags/{id}` | Tag sil | ✅ ADMIN |

## Kurulum

1. **PostgreSQL veritabanı oluştur**:
   ```sql
   CREATE DATABASE blog_db;
   ```

2. **Environment variables ayarla** (opsiyonel):
   ```bash
   export DB_USERNAME=postgres
   export DB_PASSWORD=postgres
   export JWT_SECRET=your-secret-key-here
   ```

3. **Java 21 kullanılıyor olduğundan emin ol**:
   ```bash
   export JAVA_HOME=/path/to/java-21
   ```

4. **Uygulamayı çalıştır**:
   ```bash
   mvn spring-boot:run
   ```

5. **Default Kullanıcılar**:
   - Admin: `username: admin`, `password: admin123`
   - User: `username: user`, `password: user123`

## API Kullanım Örnekleri

### Authentication

```bash
# Kullanıcı kaydı
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'

# Kullanıcı girişi
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "type": "Bearer",
#   "username": "admin",
#   "email": "admin@blog.com",
#   "role": "ADMIN"
# }
```

### Authenticated Requests

```bash
# JWT token'ı değişkene kaydet
TOKEN="your-jwt-token-here"

# Tag oluştur (ADMIN gerekli)
curl -X POST http://localhost:8080/api/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Java"}'

# Post oluştur (ADMIN gerekli)
curl -X POST http://localhost:8080/api/tags \
  -H "Content-Type: application/json" \
  -d '{"name": "Java"}'

# Post oluştur
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"title": "İlk Post", "summary": "Özet", "content": "İçerik"}'

# Postları listele
curl http://localhost:8080/api/posts

# Yorum ekle
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -d '{"authorName": "Ali", "authorEmail": "ali@test.com", "content": "Güzel yazı!"}'
```

## Lisans

MIT
