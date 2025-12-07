# Blog API

Java Spring Boot ile geliştirilmiş temiz, sürdürülebilir ve ölçeklenebilir bir blog REST API'si.

## Teknolojiler

- **Java 8** + **Spring Boot 2.7.18**
- **Spring Data JPA** + **PostgreSQL**
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

## API Endpoints

### Posts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/posts` | Tüm postları listele |
| GET | `/api/posts/{slug}` | Slug ile post detayı |
| GET | `/api/posts/id/{id}` | ID ile post detayı |
| GET | `/api/posts/tag/{tagSlug}` | Tag'e göre postlar |
| POST | `/api/posts` | Yeni post oluştur |
| PUT | `/api/posts/{id}` | Post güncelle |
| DELETE | `/api/posts/{id}` | Post sil |
| POST | `/api/posts/{postId}/tags/{tagId}` | Tag ekle |
| DELETE | `/api/posts/{postId}/tags/{tagId}` | Tag çıkar |

### Comments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/posts/{postId}/comments` | Yorumları listele |
| POST | `/api/posts/{postId}/comments` | Yorum ekle |
| GET | `/api/comments/pending` | Onay bekleyen yorumlar |
| PUT | `/api/comments/{id}/approve` | Yorumu onayla |
| DELETE | `/api/comments/{id}` | Yorum sil |

### Tags
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tags` | Tüm tag'leri listele |
| GET | `/api/tags/{slug}` | Tag detayı |
| POST | `/api/tags` | Yeni tag oluştur |
| DELETE | `/api/tags/{id}` | Tag sil |

## Kurulum

1. **PostgreSQL veritabanı oluştur**:
   ```sql
   CREATE DATABASE blog_db;
   ```

2. **Environment variables ayarla** (opsiyonel):
   ```bash
   export DB_USERNAME=postgres
   export DB_PASSWORD=postgres
   ```

3. **Uygulamayı çalıştır**:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Kullanım Örnekleri

```bash
# Tag oluştur
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
