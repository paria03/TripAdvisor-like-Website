# TravelHelper - Hotel Review Website

## Overview

TravelHelper is a web-based application inspired by platforms like TripAdvisor and Hotels.com. It allows users to search for hotels, read and write reviews, and interact with dynamic hotel information. The project emphasizes secure user management, a clean and professional interface, and robust back-end functionality.

## Features

* ### User Management

  * User registration with secure password storage (salted and hashed).
  * Login and logout functionality with session persistence.
  * Displays the user’s last login time (not the current session).

* ### Hotel Search and Information

  * Search hotels by ID or keywords in their names.
  * Display detailed hotel information, including:
    * Hotel name, address, and average rating.
    * List of reviews.
    * Direct links to Expedia pages for each hotel.

* ### Review Management

  * Add, edit, or delete hotel reviews.
  * Reviews include a title, content, rating, and timestamp.
  * Reviews dynamically update without reloading the page using AJAX.

* ### Favorites and History

  * Users can mark hotels as favorites.
  * Record and display a history of visited Expedia links, with the option to clear history.

* ### Interactive Features

  * Pagination for hotel reviews to improve readability.
  * Like functionality for reviews, tracking how many users found a review helpful.

* ### Professional UI

  * Clean and consistent design using Bootstrap components.
  * Dynamic page generation with Velocity templates.

* ###   Database Integration

  * MySQL database to store all data, including users, hotels, reviews, favorites, and history.
  * Populate tables with initial data from a JSON dataset.
  * All database operations are performed securely with JDBC.

###   Technologies Used

*   Backend: Java (Jetty/Servlets, JDBC)
*   Frontend: JavaScript (Fetch API), HTML, CSS, Bootstrap 
*   Database: MySQL

### Setup

1. Clone the repository 
2. Set up the MySQL database:
   1. Import the provided schema and data into your MySQL server.
    2. Update database configurations in the project properties file.
3. Build and run the project
       
4. Open your browser and visit http://localhost:8080.

### Credits

Developed as part of CS601 at the University of San Francisco.