# Cultivo

## Overview

Cultivo is a plant lifecycle tracker, that allows users to track their plants by logging their actions into events to create the plant lifecycle history.

## Features

- Plant Management
- Plant Event Tracking

## Tech Stack

- **Frontend:** Vue 3, Tailwind CSS
- **Backend:** Spring Boot
- **Database:** PostgreSQL
- **Infrastructure:** Docker Compose

## Prerequisites

- **Frontend:** Node.js 22.22+, npm 10+
- **Backend:** Java21+, Maven
- **Infrastructure:** Docker & Docker Compose

## Setup

1. Clone the repository

   ```bash
   git clone https://github.com/manos-grigorakis/cultivo.git
   cd cultivo
   ```

2. Copy & Configure environment variables

   ```bash
   cp frontend/.env.example frontend/.env.local
   cp backend/.env.example backend/.env
   ```

3. Start infrastructure services

   ```bash
   docker compose -f backend/docker-compose.dev.yaml up -d
   ```

4. Start backend server
   1. (Recommended) With IntelliJ IDEA \
      Open the `backend` directory in IntelliJ IDEA and the run configuration will automatically load.

   2. With Maven installed

      ```bash
      cd backend
      mvn spring-boot:run
      ```

   3. Without Maven

      ```bash
      cd backend
      ./mvnw spring-boot:run
      ```

5. Install frontend dependencies
   ```bash
   cd frontend
   npm install
   ```
6. Start frontend development server
   ```bash
   npm run dev
   ```

## Documentation

- [Project Specification](/docs/project.md)

<!-- ## Screenshots

### [Caption]

![Caption](/docs/screenshots/[screenshot-name.jpg]) -->

## License

All rights reserved. See [License](/LICENSE) for details.
