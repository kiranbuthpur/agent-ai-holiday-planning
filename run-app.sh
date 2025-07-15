#!/bin/bash

# Holiday Planner AI - Quick Start Script
# This script sets up and runs both the backend and frontend

set -e

echo "🏖️  Holiday Planner AI - Quick Start"
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check Java version
check_java() {
    if command_exists java; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [[ "$JAVA_VERSION" -ge 21 ]]; then
            echo -e "${GREEN}✓${NC} Java $JAVA_VERSION found"
            return 0
        else
            echo -e "${RED}✗${NC} Java 21 or higher required. Found: $JAVA_VERSION"
            return 1
        fi
    else
        echo -e "${RED}✗${NC} Java not found. Please install Java 21+"
        return 1
    fi
}

# Function to check Node.js version
check_node() {
    if command_exists node; then
        NODE_VERSION=$(node --version | cut -d'.' -f1 | cut -d'v' -f2)
        if [[ "$NODE_VERSION" -ge 18 ]]; then
            echo -e "${GREEN}✓${NC} Node.js $(node --version) found"
            return 0
        else
            echo -e "${RED}✗${NC} Node.js 18 or higher required. Found: $(node --version)"
            return 1
        fi
    else
        echo -e "${RED}✗${NC} Node.js not found. Please install Node.js 18+"
        return 1
    fi
}

# Check prerequisites
echo -e "${BLUE}Checking prerequisites...${NC}"
check_java || exit 1
check_node || exit 1

# Check if npm is available
if ! command_exists npm; then
    echo -e "${RED}✗${NC} npm not found. Please install npm"
    exit 1
fi
echo -e "${GREEN}✓${NC} npm found"

echo ""

# Setup backend
echo -e "${BLUE}Setting up backend...${NC}"
cd holiday-planner/backend

if [[ ! -x "./mvnw" ]]; then
    echo -e "${YELLOW}Making Maven wrapper executable...${NC}"
    chmod +x mvnw
fi

echo -e "${YELLOW}Installing backend dependencies...${NC}"
./mvnw clean install -q
echo -e "${GREEN}✓${NC} Backend setup complete"

# Setup frontend
echo -e "${BLUE}Setting up frontend...${NC}"
cd ../frontend

if [[ ! -d "node_modules" ]]; then
    echo -e "${YELLOW}Installing frontend dependencies...${NC}"
    npm install
else
    echo -e "${GREEN}✓${NC} Frontend dependencies already installed"
fi

echo -e "${GREEN}✓${NC} Frontend setup complete"
echo ""

# Configuration check
echo -e "${BLUE}Configuration Check:${NC}"
BACKEND_CONFIG="../backend/src/main/resources/application.properties"

if grep -q "weather.api.key=demo-key" "$BACKEND_CONFIG"; then
    echo -e "${YELLOW}⚠️${NC}  Weather API key is set to demo value"
    echo -e "   Update weather.api.key in $BACKEND_CONFIG"
fi

if grep -q "spring.mail.username=your-email@gmail.com" "$BACKEND_CONFIG"; then
    echo -e "${YELLOW}⚠️${NC}  Email configuration uses demo values"
    echo -e "   Update mail settings in $BACKEND_CONFIG"
fi

echo ""

# Start applications
echo -e "${BLUE}Starting applications...${NC}"

# Function to cleanup on exit
cleanup() {
    echo ""
    echo -e "${YELLOW}Shutting down applications...${NC}"
    jobs -p | xargs -r kill
    exit 0
}

trap cleanup SIGINT SIGTERM

# Start backend in background
echo -e "${YELLOW}Starting backend on http://localhost:8080...${NC}"
cd ../backend
./mvnw spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!

# Wait for backend to start
echo -e "${YELLOW}Waiting for backend to start...${NC}"
timeout=60
counter=0
while ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    if [[ $counter -ge $timeout ]]; then
        echo -e "${RED}✗${NC} Backend failed to start within $timeout seconds"
        echo -e "${YELLOW}Check backend.log for errors${NC}"
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
    sleep 1
    counter=$((counter + 1))
    echo -n "."
done

echo ""
echo -e "${GREEN}✓${NC} Backend started successfully!"

# Start frontend
echo -e "${YELLOW}Starting frontend on http://localhost:5173...${NC}"
cd ../frontend
npm run dev > frontend.log 2>&1 &
FRONTEND_PID=$!

# Wait a moment for frontend to start
sleep 3

echo ""
echo -e "${GREEN}🎉 Applications are running!${NC}"
echo ""
echo -e "${BLUE}Access your applications:${NC}"
echo -e "  • Frontend: ${GREEN}http://localhost:5173${NC}"
echo -e "  • Backend API: ${GREEN}http://localhost:8080/api${NC}"
echo -e "  • H2 Database Console: ${GREEN}http://localhost:8080/h2-console${NC}"
echo -e "  • Health Check: ${GREEN}http://localhost:8080/actuator/health${NC}"
echo ""
echo -e "${BLUE}Database Console Login:${NC}"
echo -e "  • JDBC URL: ${YELLOW}jdbc:h2:mem:holidayplanner${NC}"
echo -e "  • Username: ${YELLOW}sa${NC}"
echo -e "  • Password: ${YELLOW}password${NC}"
echo ""
echo -e "${YELLOW}Logs are being written to:${NC}"
echo -e "  • Backend: backend.log"
echo -e "  • Frontend: frontend.log"
echo ""
echo -e "${RED}Press Ctrl+C to stop all services${NC}"

# Wait for user to interrupt
wait