# Vinyl Records Database Query System

## Contents
1. [Overview](#overview)
2. [Problem Statement](#problem-statement)
3. [Database Setup](#database-setup)
4. [Class Descriptions](#class-descriptions)
    1. [Credentials.java](#credentialsjava)
    2. [RecordsDatabaseServer.java](#recordsdatabase-serverjava)
    3. [RecordsDatabaseService.java](#recordsdatabase-servicejava)
    4. [RecordsDatabaseClient.java](#recordsdatabase-clientjava)
5. [Requirements](#requirements)
6. [Adding PostgreSQL Driver to IntelliJ IDEA](#adding-postgresql-driver-to-intellij-idea)
7. [Running the Project](#running-the-project)
8. [Project Structure](#project-structure)
9. [Sample Outputs](#sample-outputs)
    1. [Server Output](#server-output)
    2. [Client Output](#client-output)

## Overview
This project is designed to integrate a database server with a front-end interface into a complete software stack. Key skills include JavaFX-based front-end creation, SQL querying, thread management, client-server architecture using TCP, and JDBC.

## Problem Statement
The project involves developing a 3-tier TCP-based networking multi-threaded client-server application to query a database about vinyl records. The client features a JavaFX-based GUI to request services and communicate with a server that processes queries. The server, located on the localhost, consists of two parts: the main server (handling incoming requests) and the service provider (connecting to the database via JDBC, retrieving query results, and sending them back to the client).

### Service Specification
Given an artist’s last name and a record shop’s city, the query retrieves the list of records available from that artist at the specified shop. Each record's details, such as title, music label, genre, recommended retail price, and number of copies available, are included in the results. Records with zero copies available should not be listed. The system handles queries with no results gracefully and is robust against SQL injection.

## Database Setup
1. Open a PostgreSQL shell and connect with your credentials.
2. Import the provided database using:
    ```sql
    \i records.sql
    ```
3. List all tables with:
    ```sql
    \dt
    ```
4. View the contents of each table:
    ```sql
    SELECT * FROM tablename;
    ```

## Class Descriptions

### Credentials.java
Contains the database (username, password, URL) and server connection (host address, port number) credentials. Replace the placeholder credentials with your own.

### RecordsDatabaseServer.java
Main server class providing access to the records database.
- **Constructor**: Initializes the server socket and reads connection credentials.
- **executeServiceLoop()**: Listens for incoming client requests and creates service threads to handle them.
- **main()**: Starts the server.

### RecordsDatabaseService.java
Service provider class responsible for handling client requests, connecting to the database, executing queries, and returning results.
- **Constructor**: Initializes the server service socket and starts the service thread.
- **retrieveRequest()**: Parses the client's request message.
- **attendRequest()**: Connects to the database, executes the query, processes results, and closes the connection.
- **returnServiceOutcome()**: Sends the query results back to the client.
- **run()**: Executes the service thread.

### RecordsDatabaseClient.java
Client application with a JavaFX GUI for querying the records database.
- **Constructor**: Initializes the application.
- **initializeSocket()**: Sets up the client socket.
- **requestService()**: Sends the service request to the server.
- **reportServiceOutcome()**: Receives and displays the query results.
- **execute()**: Handles the button press event to send a new query.
- **start()**: Creates the GUI.
- **main()**: Launches the client application.

## Requirements
To run this project, you need:
- Java Development Kit (JDK) 21.0.3
- IntelliJ IDEA
- PostgreSQL 13
- PostgreSQL JDBC Driver (`postgresql-42.6.0.jar`)

## Adding PostgreSQL Driver to IntelliJ IDEA
1. Open your project in IntelliJ IDEA.
2. Right-click on the `lib` folder and select `Add as Library`.
3. Go to `File > Project Structure`.
4. Under `Project Settings`, select `Libraries`.
5. Click on the `+` icon and select `Java`.
6. Navigate to the `lib` folder and select `postgresql-42.6.0.jar`.
7. Click `OK` to add the library to your project.

## Running the Project
To run the project, you need to start both the server and the client applications. Follow these steps:

1. **Run the Server**:
    - Open `RecordsDatabaseServer.java` in your IDE.
    - Run the `main` method to start the server. The server will listen for incoming client requests.

2. **Run the Client**:
    - Open `RecordsDatabaseClient.java` in your IDE.
    - Run the `main` method to start the client application. The client will connect to the server and provide a GUI for querying the database.

Make sure the server is running before starting the client to ensure successful connection and communication.

## Project Structure

The project is organized as follows:

```plaintext
vinylqueryapp
│
├── .idea
├── .mvn
├── DBFile
│   └── records.sql
├── img
│   └── Exemplary client console output.jpg 
│   └── Exemplary server console output.jpg
│   └── Exemplary query output to input artist Beyonce and record shop in London.jpg
│   └── Exemplary query output to input artist Sheeran and record shop in Cardiff.jpg
├── lib
│   └── postgresql-42.6.0.jar
├── src
│   ├── main
│       └── java
│           └── com.ayush.vinylrecords.vinylqueryapp
│               ├── Credentials.java
│               ├── RecordsDatabaseClient.java
│               ├── RecordsDatabaseServer.java
│               ├── RecordsDatabaseService.java
│               └── module-info.java
├── target
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── Readme.md
```

### Important Notes
- The PostgreSQL driver (`postgresql-42.6.0.jar`) is located in the `lib` folder.

## Sample Outputs

### Server Output
The server output is displayed in the console, showing the handling of service requests.

![Exemplary server console output](img/Exemplary%20server%20console%20output..jpg)

### Client Output
The client output is displayed both in the GUI and the console. Below are examples of different scenarios:

- **No Records Found**:
    ```
    Client: Requesting records database service for user command
    Franklin;Berlin
    **** []
    ```
  ![No records found output](img/Exemplary%20server%20console%20output..jpg)

- **Records Found**:
    - For artist "Beyonce" in London:
      ![Query output for Beyonce in London](img/Exemplary%20query%20output%20to%20input%20artist%20Beyonce%20and%20record%20shop%20in.jpg)
    - For artist "Sheeran" in Cardiff:
      ![Query output for Sheeran in Cardiff](img/Exemplary%20query%20output%20to%20input%20artist%20Sheeran%20and%20record%20shop%20in.jpg)

This project demonstrates the integration of various software components into a cohesive full-stack application. It provides practical experience with database querying, client-server communication, multi-threading, and JavaFX for GUI development.
