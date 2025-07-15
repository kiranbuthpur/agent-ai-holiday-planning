# Holiday Planner AI 🏖️

An AI-powered holiday planning application with weather integration, built with Spring Boot backend and React frontend.

## 🚀 Quick Start

### Option 1: One-Click Setup (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd agent-ai-holiday-planning

# Run the automated setup script
./run-app.sh
```

The script will:
- ✅ Check prerequisites (Java 21+, Node.js 18+)
- ✅ Install all dependencies
- ✅ Build and start both backend and frontend
- ✅ Provide access URLs and configuration info

### Option 2: Manual Setup
```bash
# Backend (Terminal 1)
cd holiday-planner/backend
chmod +x mvnw
./mvnw spring-boot:run

# Frontend (Terminal 2)
cd holiday-planner/frontend
npm install
npm run dev
```

## 📍 Access Points

Once running, access your application at:

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **Database Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health

## 🏗️ Architecture

- **Backend**: Spring Boot 3.4.1 with Java 21
- **Frontend**: React 19.1.0 with Vite
- **Database**: H2 in-memory (development)
- **Features**: Weather API integration, Email notifications, Security

## 📚 Complete Documentation

For comprehensive deployment instructions, production setup, Docker deployment, cloud deployment, and troubleshooting, see:

**[📖 DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)**

## ⚙️ Configuration

Before running, optionally update `holiday-planner/backend/src/main/resources/application.properties`:

```properties
# Weather API (get key from openweathermap.org)
weather.api.key=YOUR_OPENWEATHERMAP_API_KEY

# Email configuration
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## 🔧 Development

### Prerequisites
- Java 21+
- Node.js 18+
- Maven 3.8+ (or use included wrapper)

### Database Access
- **URL**: `jdbc:h2:mem:holidayplanner`
- **Username**: `sa`
- **Password**: `password`

## 🆘 Need Help?

1. **Quick Issues**: Check the [Troubleshooting section](./DEPLOYMENT_GUIDE.md#-troubleshooting) in the deployment guide
2. **Logs**: Check `backend.log` and `frontend.log` if using the run script
3. **Dependencies**: Ensure Java 21+ and Node.js 18+ are installed

## 🎯 Features

- AI-powered holiday planning
- Real-time weather integration
- Email notifications
- User authentication and security
- Responsive React frontend
- RESTful API backend
- In-memory database for quick setup

---

**Getting Started**: Run `./run-app.sh` and open http://localhost:5173