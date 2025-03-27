# Pet Adoption Management System - Client

## Overview

This is the client component of the Pet Adoption Management System, a Java-based application designed to facilitate pet adoption processes. The client application provides an intuitive graphical user interface built with JavaFX that allows both shelter staff and potential adopters to interact with the system.
![client 1](https://github.com/user-attachments/assets/23270b3d-adf9-44ce-8c58-8d332ad51388)
![client 2](https://github.com/user-attachments/assets/c051d03e-e079-4078-8935-86ce0c79c62e)
![client 3](https://github.com/user-attachments/assets/a42d0789-19d9-4003-9192-194a80fca4a2)
![client 4](https://github.com/user-attachments/assets/42b885de-5d57-4d8e-9eec-c00234cabe21)
![client 6](https://github.com/user-attachments/assets/9d273796-41b5-4441-81a5-5453752a4a14)

## Features

The client application supports two user roles:

### For Adopters
- Browse available pets
- Search for pets based on various criteria
- View pet details
- Register as a new adopter
- Update personal profile and preferences
- Access AI-powered pet matching based on preferences
- Submit adoption applications
- Track adoption application status

### For Shelter Staff
- Manage pet inventory (add, update, delete pets)
- Review and manage adopter profiles
- Process adoption applications
- Track adoption status throughout the workflow

## Architecture

The client application follows the Model-View-Controller (MVC) architectural pattern:

### Model (`model` package)
- `Adopter.java`: Represents a potential pet adopter with their preferences
- `Adoption.java`: Tracks the adoption process between a specific pet and adopter
- `Pet.java`: Represents a pet with attributes like species, breed, age, and gender

### View (`resources/view` package)
- FXML files for different screens organized by user role:
  - `adopter/`: FXML files for adopter-specific screens
  - `staff/`: FXML files for staff-specific screens
  - `WelcomeScreen.fxml`: The main entry point of the application

### Controller (`controller` package)
- `adopter/`: Controllers for adopter-specific screens
  - `AdopterAdoptionsController.java`: Manages adopter's adoption applications
  - `AdopterDashboardController.java`: Main dashboard for adopters
  - `AdopterProfileController.java`: Profile management for adopters
  - `PetBrowseController.java`: Browse and search pets
  - `PetMatchingController.java`: AI-powered pet matching functionality
- `staff/`: Controllers for staff-specific screens
  - `AdopterManagementController.java`: Manage adopter profiles
  - `AdoptionManagementController.java`: Process adoption applications
  - `PetManagementController.java`: Manage pet inventory
  - `StaffDashboardController.java`: Main dashboard for staff
- `MainScreenController.java`: Controller for the welcome screen

### Service (`service` package)
- `AdopterService.java`: Service for managing adopter profiles
- `AdoptionService.java`: Service for managing adoption applications
- `PetService.java`: Service for managing pet inventory
- `ApiClient.java`: Handles communication with the server
- `Request.java`: Represents client requests to the server
- `Response.java`: Represents server responses

### Utility (`util` package)
- `AlertHelper.java`: Utility for displaying alerts and notifications
- `BackButtonManager.java`: Manages navigation between screens
- `ValidationUtils.java`: Utility for input validation

## Communication

The client communicates with the server using a TCP socket-based protocol with JSON for data exchange:

```
Client <--JSON over TCP/IP--> Server
```

The `ApiClient` class handles all communication with the server, constructing requests and processing responses. All data is serialized to and from JSON using the Google Gson library.

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- JavaFX (included in JRE 8, separate download for later versions)
- Network connection to the Pet Adoption Server

## Setup and Configuration

1. Ensure Java is installed on your system
2. Configure the server connection in `ApiClient.java`:
   ```java
   public static String SERVER_HOST = "localhost"; // Change to server IP if needed
   public static int SERVER_PORT = 34567;          // Change if server uses different port
   ```

## Running the Application

To run the application:

1. Make sure the Pet Adoption Server is running
2. Run the `PetAdoptionClientApp` class in the `app` package
3. The welcome screen will appear, allowing you to select your role (staff or adopter)

## User Interface Flow

### Initial Login
1. Select your role (Adopter or Staff) from the welcome screen
2. For adopters:
   - Log in with existing email
   - Register as a new adopter if first time
3. For staff:
   - Access the staff dashboard directly (authentication could be added in future versions)

### Adopter Workflow
1. From the Adopter Dashboard, access:
   - Pet Search: Browse and search for available pets
   - Pet Matching: Get AI-powered pet recommendations
   - Profile Management: Update personal information and preferences
   - Adoption Status: Track submitted adoption applications
2. Submit adoption applications for pets of interest
3. Track application status through the process

### Staff Workflow
1. From the Staff Dashboard, access:
   - Pet Management: Add, update, or remove pets
   - Adopter Management: View and manage adopter profiles
   - Adoption Management: Process adoption applications
2. Process adoption applications:
   - Review new applications
   - Approve or reject applications
   - Complete adoptions

## Design Patterns

The client application implements several design patterns:
- **MVC Pattern**: Separation of model, view, and controller
- **Service Pattern**: Business logic encapsulated in service classes
- **Factory Pattern**: Creation of complex objects
- **Observer Pattern**: Event handling in JavaFX components
- **Strategy Pattern**: Different rendering strategies for different data types

## UI Components

The application uses a combination of custom and standard JavaFX components:
- Custom styled buttons and forms
- Data tables for displaying lists
- Form validation for user input
- Alert dialogs for notifications
- Navigation management between screens

## Styling

The UI styling is implemented through:
- CSS stylesheets in the `resources/css` directory
- Custom control styling in the JavaFX components
- Images and icons in the `resources/images` directory

## Error Handling

The client implements comprehensive error handling:
- Network communication errors
- Server response errors
- Input validation errors
- UI interaction errors

All errors are presented to the user through appropriate alert dialogs, with different styling based on error severity.

## Extensibility

The application is designed for extensibility:
- New pet attributes can be added by extending the Pet model
- New matching criteria can be added to the AI matching algorithm
- Additional screens and functionality can be added by creating new FXML files and controllers

## Contributors

This project was developed as part of the Programming in Internet Environments course at HIT - Holon Institute of Technology.
