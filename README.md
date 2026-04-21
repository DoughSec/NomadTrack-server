# NomadTrack Server

NomadTrack Server is the Spring Boot backend for the NomadTrack travel application. It handles user accounts, JWT-based authentication, profile updates, trips, wishlists, follows, comments, likes, trip photos, AI-powered destination recommendations, and S3 upload presigning for images.

Most API routes live under `/nomadTrack`. The image upload presign route lives under `/uploads`.

## What this backend does

- Registers users and logs them in with JWT bearer tokens.
- Stores user profiles in MySQL.
- Lets users create, update, list, and delete trips.
- Lets users create and manage travel wishlists.
- Supports social features: following users, liking trips, and commenting on trips.
- Stores trip photo metadata and can generate S3 presigned upload URLs.
- Calls a separate AI recommendation service to return destination suggestions.

## Tech stack

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- MySQL
- JWT via `jjwt`
- AWS SDK for Java v2 (S3 presigner)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Main features by area

### Authentication

- `POST /nomadTrack/auth/register`
- `POST /nomadTrack/auth/login`
- `GET /nomadTrack/auth/me`

Login returns a JWT and an expiration value. The token lifetime is 1 hour.

### Users

- `GET /nomadTrack/users/search`
- `GET /nomadTrack/users/search/{firstName}`
- `GET /nomadTrack/users/{userId}`
- `PUT /nomadTrack/users/me`
- `GET /nomadTrack/users`

The last route is annotated for admin-only access.

### Trips

- `POST /nomadTrack/trips`
- `GET /nomadTrack/trips`
- `GET /nomadTrack/trips/user/{userId}`
- `GET /nomadTrack/trips/{countryName}`
- `GET /nomadTrack/trips/map/locations`
- `PUT /nomadTrack/trips/{tripId}`
- `DELETE /nomadTrack/trips/{tripId}`

Trips can also have:

- Comments: `/{tripId}/comments`
- Likes: `/{tripId}/likes`
- Photos: `/{tripId}/photos`

### Wishlists

- `POST /nomadTrack/wishlists`
- `GET /nomadTrack/wishlists`
- `GET /nomadTrack/wishlists/{targetCountry}`
- `PATCH /nomadTrack/wishlists/{wishlistId}/complete`
- `PUT /nomadTrack/wishlists/{wishlistId}`
- `DELETE /nomadTrack/wishlists/{wishlistId}`

### Follows

- `POST /nomadTrack/follows/{userId}`
- `DELETE /nomadTrack/follows/{userId}`
- `GET /nomadTrack/follows/following`
- `GET /nomadTrack/follows/followers`
- `GET /nomadTrack/follows/{userId}/following`
- `GET /nomadTrack/follows/{userId}/followers`

### Recommendations

- `POST /nomadTrack/recommendations`

This route forwards a request to a separate AI recommendation service.

### Uploads

- `POST /uploads/presign`

This route generates a presigned S3 upload URL plus the final public file URL.

## Project structure

```text
src/main/java/com/nomadtrack/nomadtrackserver
|- controller      REST endpoints
|- service         Business logic
|- repository      Spring Data repositories
|- model           JPA entities
|- model/dto       API request/response DTOs
|- security        JWT filter, CORS, security config, S3 config
|- configAI        AI service client configuration

src/main/resources
|- application.properties

src/test/java
|- service-level unit tests
```

## Prerequisites

Before you run the backend, make sure you have:

1. Java 25 installed and active in `JAVA_HOME`
2. MySQL running locally
3. An empty database created for NomadTrack
4. AWS credentials and region values available as environment variables
5. The AI recommendation service running if you want recommendation routes to work

Important:

- This project is configured with `<java.version>25</java.version>` in `pom.xml`.
- Java 21 is not enough. Maven compilation fails with `release version 25 not supported`.
- Spring Boot is not automatically loading a `.env` file in this project. You must set environment variables in your shell, OS, or IDE run configuration yourself.

## Step-by-step local setup

### 1. Clone the repository and open the backend directory

```powershell
git clone <repo-url>
cd NomadTrack\server
```

### 2. Create the MySQL database

Open MySQL and create the database:

```sql
CREATE DATABASE nomadTrackDB;
```

The default local JDBC URL used by this project is:

```text
jdbc:mysql://localhost:3306/nomadTrackDB
```

If you want a different database name, host, or port, change `DB_URL` accordingly.

### 3. Set the required environment variables

In PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/nomadTrackDB"
$env:DB_USERNAME="your_mysql_username"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="replace-this-with-a-secret-at-least-32-characters-long"
$env:AWS_ACCESS_KEY_ID="your_aws_access_key_id"
$env:AWS_SECRET_ACCESS_KEY="your_aws_secret_access_key"
$env:AWS_REGION="us-east-2"
```

What each variable is used for:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: MySQL connection
- `JWT_SECRET`: JWT signing key; it must be at least 32 characters
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`: S3 presigning

Notes:

- The upload controller currently builds public URLs for the bucket `nomadtrack-images` in `us-east-2`.
- If you want `/uploads/presign` to work without code changes, your AWS credentials must be able to access that bucket.
- If you are only working on non-upload features, you should still set the AWS variables so property resolution is not blocked.

### 4. Start the AI recommendation service

The backend expects the AI service here by default:

```text
http://localhost:8001/recommend
```

Those values come from `src/main/resources/application.properties`:

```properties
ai.service.base-url=http://localhost:8001
ai.service.recommend-path=/recommend
```

If the AI service is not running, the backend can still start, but `POST /nomadTrack/recommendations` will fail when called.

### 5. Start the backend

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

By default, Spring Boot serves the API at:

```text
http://localhost:8080
```

### 6. Let Hibernate create or update the schema

This project uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

That means Spring Boot will create missing tables and update the schema automatically when the app starts.

### 7. Smoke-test the server

There is no dedicated health endpoint in this codebase, so use a lightweight route. For example:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/nomadTrack/users/search"
```

A fresh database will usually return an empty array.

## End-to-end walkthrough

This is the simplest full flow for local development.

### 1. Register a user

```powershell
$baseUrl = "http://localhost:8080"

$registerBody = @{
  email = "demo@example.com"
  password = "Password123!"
  firstName = "Demo"
  lastName = "User"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/nomadTrack/auth/register" `
  -ContentType "application/json" `
  -Body $registerBody
```

### 2. Log in and save the token

```powershell
$loginBody = @{
  email = "demo@example.com"
  password = "Password123!"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/nomadTrack/auth/login" `
  -ContentType "application/json" `
  -Body $loginBody

$token = $loginResponse.token
$headers = @{ Authorization = "Bearer $token" }
```

### 3. Confirm the current logged-in user

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "$baseUrl/nomadTrack/auth/me" `
  -Headers $headers
```

### 4. Create a trip

```powershell
$tripBody = @{
  title = "Toronto Weekend"
  city = "Toronto"
  country = "Canada"
  startDate = "2026-05-10"
  endDate = "2026-05-13"
  notes = "Food, museums, and waterfront."
  latitude = 43.6532
  longitude = -79.3832
  visibility = "Public"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/nomadTrack/trips" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $tripBody
```

### 5. Create a wishlist item

```powershell
$wishlistBody = @{
  title = "Visit Kyoto"
  description = "See temples and cherry blossoms."
  targetCountry = "Japan"
  targetCity = "Kyoto"
  deadline = "2026-10-01"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/nomadTrack/wishlists" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $wishlistBody
```

### 6. Ask for travel recommendations

This only works if the AI service is running.

```powershell
$recommendationBody = @{
  budget = "medium"
  climate = "warm"
  tripStyle = "balanced"
  activities = @("food", "hiking", "culture")
  region = "europe"
  tripType = "couple"
  tripLength = 7
  topN = 5
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/nomadTrack/recommendations" `
  -ContentType "application/json" `
  -Body $recommendationBody
```

### 7. Generate an upload URL for an image

This only works if AWS credentials are valid and can access the configured bucket.

```powershell
$presignBody = @{
  fileName = "toronto.jpg"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/uploads/presign" `
  -ContentType "application/json" `
  -Body $presignBody
```

The response includes:

- `uploadUrl`: use this to upload the file directly to S3
- `fileUrl`: save this URL in a trip photo record later

### 8. Save the uploaded image against a trip

After the file is uploaded to S3, store the image metadata:

```powershell
$photoBody = @{
  url = "https://nomadtrack-images.s3.us-east-2.amazonaws.com/listings/example.jpg"
  caption = "Toronto skyline"
  sortOrder = 1
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/nomadTrack/trips/1/photos" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $photoBody
```

Replace `1` with the real trip id from your create-trip response.

## Authentication and authorization behavior

The backend uses JWT bearer tokens in the `Authorization` header:

```text
Authorization: Bearer <token>
```

Current behavior in this codebase:

- `POST /nomadTrack/auth/register` and `POST /nomadTrack/auth/login` are public.
- Many create, update, and delete routes rely on the JWT filter plus `SecurityUtils.getCurrentUserId()`.
- If you call those routes without a valid token, they will fail at runtime even though the route-level security configuration is currently permissive.
- `GET /nomadTrack/auth/me` and `PUT /nomadTrack/users/me` also require a bearer token.

In practice, assume any user-specific write operation requires a token.

## CORS

The backend currently allows these frontend origins:

- `http://localhost:8000`
- `http://localhost:5173`
- `https://nomadtrack.net`
- `https://www.nomadtrack.net`

## Request DTOs you will use most often

### Register

```json
{
  "email": "demo@example.com",
  "password": "Password123!",
  "firstName": "Demo",
  "lastName": "User"
}
```

### Login

```json
{
  "email": "demo@example.com",
  "password": "Password123!"
}
```

### Create trip

```json
{
  "title": "Toronto Weekend",
  "city": "Toronto",
  "country": "Canada",
  "startDate": "2026-05-10",
  "endDate": "2026-05-13",
  "notes": "Food, museums, and waterfront.",
  "latitude": 43.6532,
  "longitude": -79.3832,
  "visibility": "Public"
}
```

### Create wishlist

```json
{
  "title": "Visit Kyoto",
  "description": "See temples and cherry blossoms.",
  "targetCountry": "Japan",
  "targetCity": "Kyoto",
  "deadline": "2026-10-01"
}
```

### Recommendation request

```json
{
  "budget": "medium",
  "climate": "warm",
  "tripStyle": "balanced",
  "activities": ["food", "hiking", "culture"],
  "region": "europe",
  "tripType": "couple",
  "tripLength": 7,
  "topN": 5
}
```

## Error handling

The backend returns structured JSON errors through a global exception handler for:

- `400 Bad Request`
- `403 Forbidden`
- `404 Not Found`
- `500 Internal Server Error`

## Testing

Run tests with:

```powershell
.\mvnw.cmd test
```

Important:

- Tests and compilation require Java 25 because of the Maven compiler configuration.
- If you run this with Java 21, Maven fails with `release version 25 not supported`.

## Generated API docs

Javadoc-style generated docs are checked into:

- `docs/api/index.html`

## Troubleshooting

### Maven says `release version 25 not supported`

Your active JDK is too old. Install Java 25 and point `JAVA_HOME` to it.

### App fails to start because database settings are missing

Make sure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` are set in the environment before you run the app.

### JWT startup error

`JWT_SECRET` must be at least 32 characters long or the app will fail when JWT utilities initialize.

### Recommendation endpoint fails

Make sure the AI service is running on `http://localhost:8001/recommend`, or change the AI service properties.

### Upload presign fails

Make sure:

- AWS credentials are valid
- `AWS_REGION` matches the bucket region
- the bucket `nomadtrack-images` exists or the controller is updated to use your bucket

## Development notes

- The database schema is managed automatically on startup with `ddl-auto=update`.
- Most local frontend work should come from `http://localhost:8000` or `http://localhost:5173` because those origins are already whitelisted in CORS.
- If you want cleaner environment setup, add a local mechanism to load a `.env` file into the process before startup. As written, the application depends on real environment variables being present.
