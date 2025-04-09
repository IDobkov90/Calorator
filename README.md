# Calorator

## Project Overview

Calorator is a comprehensive nutrition tracking application designed to help users monitor their daily food intake,
track calories, and maintain healthy eating habits. Built with Spring Boot, this application provides an intuitive
interface for logging meals, analyzing nutritional data, and setting dietary goals.

## Key Features

- **Food Logging**: Easily record your daily food consumption with detailed nutritional information
- **Frequent Foods Tracking**: Quick access to your most commonly consumed foods for faster logging
- **Nutritional Analysis**: Detailed breakdown of calories, macronutrients, and other nutritional values
- **User Authentication**: Secure user accounts with Spring Security
- **Responsive Design**: Clean, user-friendly interface built with Thymeleaf templates

## Technology Stack

- **Backend**: Java with Spring Boot, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, HTML, CSS
- **Database**: MySQL with Flyway migrations for production, H2 for development
- **Build Tool**: Maven

## Application Structure

The application follows a clean architecture pattern with separation of concerns:

- Controller layer for handling HTTP requests
- Service layer for business logic
- Repository layer for data access
- Model layer for domain entities
- Thymeleaf templates for view rendering

## Getting Started

1. Clone the repository
   ```sh
   git clone https://github.com/IDobkov90/Calorator.git
   cd Calorator

2. Configure your database settings in application.properties
   ```sh 
   spring.datasource.url=jdbc:mysql://localhost:3306/calorator_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password

3. Run the application using Maven
   ```sh
   ./mvnw spring-boot:run

4. Access the application at
    ```sh 
   http://localhost:8080

## Running Tests
```sh
   ./mvnw test
```
## Features in Detail

### User Management
- Secure registration and login
- Profile management with personal details and dietary preferences
- Role-based access control

### Food Database
- Comprehensive database of common foods with nutritional information
- Ability to add custom foods
- Search and filter functionality

### Meal Tracking
- Daily food journal with timestamps
- Meal categorization (breakfast, lunch, dinner, snacks)
- Visual representation of daily nutritional intake