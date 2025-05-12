#Project title
Artis is a desktop application built with Java 17.

##Overview
This projet was developed as part of the coursework PIDEV for third year engineering students at Esprit School of Engineering [Website Link](https://esprit.tn). 
It provides various art related services for professional and social use as well as a real-time responsive interface that is easy to explore.

##Features
User-Friendly Interface: Designed for easy navigation and accessibility.
Real-Time Data Processing: Instantly responds to user inputs and updates.
AI Chatbot: Provides instant assistance and answers to user queries using natural language processing.
Text to Image Generation: Users can input text prompts to generate images, enhancing creativity and visualization.
Text to Speech: Converts text input into spoken words for accessibility and convenience.
Translation Services: Offers real-time translation of text in multiple languages.
Map Integration: Displays interactive maps for location services and navigation.
Online Payment Processing: Facilitates secure online transactions.
QR Code Generation: Allows users to scan QR codes for easy sharing of information.
PDF Import: Enables users to import and interact with PDF documents.
Email Notifications: Utilizes JavaMail services for sending email updates.
SMS Integration: Leverages Twilio for sending SMS notifications directly from the application.
Password Hashing: Secures user passwords using hashing algorithms (e.g., BCrypt).
Calendar Integration:Displays reservations and supports reminders and notifications for upcoming events.

##Tech Stack
###Frontend
JavaFX: Main framework for building the graphical user interface.
FXML: XML-based language used for defining the user interface layout.
###Backend
Java: Core programming language for application logic.

###Other Tools
Maven: Dependency management and build automation tool.
Git: Version control system for tracking changes.
Libraries and APIs:
  Twilio API: For SMS and communication services.
  Javafx lib: For sending email notifications.
  Gemini API: For AI functionalities (chatbot).
  Models Lab API: For generating images from text.
  OpenStreet API: For map integration and location services.
  Stripe API: For online payment processing.
  Itext PDF: For importing and manipulating PDF documents.
  Calendarfx lib: For calendar usage.
  
##Directory Structure
/project-root
│
├── /src
│   ├── /main
│   │   ├── /java
|   |   |   |__ /tn.esprit
|   |   |       ├── /controller
|   |   |       ├── /entities
|   |   |       ├── /gui
|   |   |       ├── /services
|   |   |       ├── /test
|   |   |       |__ /utils
|   |   |
│   │   └── /resources
│   └── /uploads
│
├── /External Libraries
├── pom.xml
└── README.md

##Getting Started
  1-Clone the repository:
  git clone [Visit my GitHub Repository](https://github.com/EmnaMbarki94/ArtisAppJava.git)
  
  2-Navigate to the project directory:
  cd ArtisAppJava
  
  3-Build the project using Maven:
  mvn clean install
  
  4-Run the application:
  mvn javafx:run

##Acknowledgments
This project was compeleted with the help, supervision and guidance of our professor and tutor Ms Emna Charfi (emna.charfi@esprit.tn) at Esprit School of Engineering [Website Link](https://esprit.tn).
