# Holiday Planner AI - Deployment Guide

This guide provides comprehensive instructions for deploying and running the Holiday Planner AI application, which consists of a Spring Boot backend and React frontend.

## 🏗️ Architecture Overview

- **Backend**: Spring Boot 3.4.1 with Java 21
- **Frontend**: React 19.1.0 with Vite build tool
- **Database**: H2 in-memory database (for demo/development)
- **APIs**: Weather API integration, Mail service

## 📋 Prerequisites

### System Requirements
- **Java**: JDK 21 or higher
- **Node.js**: Version 18 or higher
- **npm**: Version 8 or higher
- **Maven**: 3.8 or higher (or use included Maven wrapper)

### API Keys (Required for full functionality)
- **OpenWeatherMap API Key**: For weather data
- **Email SMTP Credentials**: For sending notifications

## 🚀 Quick Start (Development)

### 1. Clone and Setup
```bash
git clone <repository-url>
cd agent-ai-holiday-planning
```

### 2. Backend Setup
```bash
cd holiday-planner/backend

# Using Maven wrapper (recommended)
chmod +x mvnw
./mvnw clean install

# Or using system Maven
mvn clean install
```

### 3. Frontend Setup
```bash
cd ../frontend
npm install
```

### 4. Configuration
Update `holiday-planner/backend/src/main/resources/application.properties`:
```properties
# Weather API (replace with your key)
weather.api.key=YOUR_OPENWEATHERMAP_API_KEY

# Email configuration (replace with your SMTP details)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 5. Run the Application

**Terminal 1 - Backend:**
```bash
cd holiday-planner/backend
./mvnw spring-boot:run
```
Backend will be available at: http://localhost:8080

**Terminal 2 - Frontend:**
```bash
cd holiday-planner/frontend
npm run dev
```
Frontend will be available at: http://localhost:5173

## 🔧 Development Features

### Backend Services
- **API Endpoints**: http://localhost:8080/api/*
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:holidayplanner`
  - Username: `sa`
  - Password: `password`
- **Actuator Health**: http://localhost:8080/actuator/health

### Frontend Development
- **Hot Reload**: Enabled with Vite
- **Linting**: `npm run lint`
- **Build**: `npm run build`
- **Preview**: `npm run preview`

## 🏭 Production Deployment

### Option 1: Traditional Server Deployment

#### Backend Production Build
```bash
cd holiday-planner/backend
./mvnw clean package -DskipTests
```
This creates `target/holiday-planner-api-0.0.1-SNAPSHOT.jar`

#### Frontend Production Build
```bash
cd holiday-planner/frontend
npm run build
```
This creates `dist/` directory with optimized assets

#### Run Production Backend
```bash
java -jar target/holiday-planner-api-0.0.1-SNAPSHOT.jar
```

#### Serve Frontend
Serve the `dist/` directory using a web server like Nginx, Apache, or a CDN.

### Option 2: Docker Deployment

#### Backend Dockerfile
Create `holiday-planner/backend/Dockerfile`:
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/holiday-planner-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Frontend Dockerfile
Create `holiday-planner/frontend/Dockerfile`:
```dockerfile
FROM node:18-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Docker Compose
Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  backend:
    build: ./holiday-planner/backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - WEATHER_API_KEY=${WEATHER_API_KEY}
      - SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
      - SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
    networks:
      - holiday-planner-network

  frontend:
    build: ./holiday-planner/frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - holiday-planner-network

networks:
  holiday-planner-network:
    driver: bridge
```

#### Run with Docker Compose
```bash
# Set environment variables
export WEATHER_API_KEY=your_weather_api_key
export SPRING_MAIL_USERNAME=your_email@gmail.com
export SPRING_MAIL_PASSWORD=your_app_password

# Build and run
docker-compose up --build
```

### Option 3: Cloud Deployment

#### Heroku Deployment
1. **Backend**:
```bash
cd holiday-planner/backend
heroku create your-app-backend
heroku config:set WEATHER_API_KEY=your_key
heroku config:set SPRING_MAIL_USERNAME=your_email
heroku config:set SPRING_MAIL_PASSWORD=your_password
git push heroku main
```

2. **Frontend**:
```bash
cd holiday-planner/frontend
# Build and deploy to Netlify, Vercel, or similar
npm run build
```

#### AWS/Azure/GCP
- Use container services (ECS, Container Instances, Cloud Run)
- Deploy JARs to elastic beanstalk/app services
- Host frontend on S3/Blob Storage/Cloud Storage with CDN

## 🔐 Production Configuration

### Backend Production Settings
Update `application.properties` or use environment variables:

```properties
# Use production database (PostgreSQL/MySQL)
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Disable H2 console
spring.h2.console.enabled=false

# Production logging
logging.level.com.holidayplanner=WARN
logging.level.org.springframework.web=WARN

# Security settings
server.error.include-stacktrace=never
server.error.include-message=never
```

### Frontend Production Settings
Update `vite.config.js`:
```javascript
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist',
    sourcemap: false,
    minify: 'terser'
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 🔍 Troubleshooting

### Common Issues

1. **Port Already in Use**:
```bash
# Kill process on port 8080
sudo lsof -t -i:8080 | xargs kill -9
```

2. **Java Version Issues**:
```bash
# Check Java version
java -version
# Should be Java 21+
```

3. **Node.js Version Issues**:
```bash
# Check Node version
node --version
# Should be 18+
```

4. **Database Connection Issues**:
- Check H2 console at http://localhost:8080/h2-console
- Verify JDBC URL: `jdbc:h2:mem:holidayplanner`

5. **API Key Issues**:
- Verify OpenWeatherMap API key is valid
- Check rate limits on weather API

## 📝 Environment Variables

### Backend Environment Variables
```bash
WEATHER_API_KEY=your_openweathermap_api_key
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
DATABASE_URL=jdbc:postgresql://localhost:5432/holidayplanner
DB_USERNAME=dbuser
DB_PASSWORD=dbpassword
SPRING_PROFILES_ACTIVE=production
```

### Frontend Environment Variables
```bash
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=Holiday Planner AI
```

## 🚨 Security Considerations

1. **Never commit API keys** to version control
2. **Use environment variables** for sensitive configuration
3. **Enable HTTPS** in production
4. **Configure CORS** properly for production domains
5. **Use production database** (not H2) for real deployments
6. **Enable security headers** and authentication

## 📊 Monitoring

### Health Checks
- Backend: http://localhost:8080/actuator/health
- Frontend: Check build artifacts and console for errors

### Logs
- Backend: Check application logs for errors
- Frontend: Check browser console and network tab

## 🆘 Support

If you encounter issues:
1. Check the application logs
2. Verify all prerequisites are installed
3. Ensure API keys are configured correctly
4. Check port availability
5. Review environment variable configuration