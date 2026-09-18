# Bus Reservation System

A desktop bus seat reservation application built with JavaFX and MySQL.

![Java](https://img.shields.io/badge/Java-b07219?style=for-the-badge&logo=java&logoColor=white) ![JavaFX](https://img.shields.io/badge/JavaFX-orange?style=for-the-badge&logo=java&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

[![Stars](https://img.shields.io/github/stars/Binidu01/Bus-Reservation-System?style=for-the-badge&logo=github)](https://github.com/Binidu01/Bus-Reservation-System/stargazers)
[![Forks](https://img.shields.io/github/forks/Binidu01/Bus-Reservation-System?style=for-the-badge&logo=github)](https://github.com/Binidu01/Bus-Reservation-System/network/members)
[![Issues](https://img.shields.io/github/issues/Binidu01/Bus-Reservation-System?style=for-the-badge&logo=github)](https://github.com/Binidu01/Bus-Reservation-System/issues)
[![License](https://img.shields.io/github/license/Binidu01/Bus-Reservation-System?style=for-the-badge)](https://github.com/Binidu01/Bus-Reservation-System/blob/main/LICENSE)

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Built With](#built-with)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Features

- Customer registration and login, with passwords hashed using jBCrypt before storage
- Bus search by route and travel details
- Interactive seat selection with a visual seat grid showing available and reserved seats
- Bus registration with route, schedule, seat count, and fare, including input validation (e.g. HH:mm time format)
- Customer dashboard for viewing and managing reservations
- Animated loading screen on startup
- JavaFX UI styled with per-screen CSS (login, dashboard, seat reservation, etc.)

## Architecture

The application is a JavaFX desktop client that connects directly to a local MySQL database via JDBC. There is no separate backend server.

```
src/main/java/com/example/busreservationsystem/
├── Main.java                     # Application entry point (launches the loading screen)
├── DatabaseConnection.java       # JDBC connection to the MySQL "bus-reservation" database
├── controllers/
│   ├── LoadingController.java
│   ├── LoginController.java
│   ├── RegisterCustomerController.java
│   ├── RegisterBusController.java
│   ├── BusSearchController.java
│   ├── ReserveSeatController.java
│   └── CustomerDashboardController.java
├── helpers/
│   └── LoggedCustomerSession.java
└── models/
    └── Bus.java

src/main/resources/               # FXML screens, stylesheets, and seat icons
```

## Installation

### Prerequisites

- JDK 21
- Maven (or use the bundled `mvnw` / `mvnw.cmd` wrapper)
- MySQL server running locally (e.g. via MAMP, XAMPP, or a standalone install)

### 1. Clone the repository

```bash
git clone https://github.com/Binidu01/Bus-Reservation-System.git
cd Bus-Reservation-System
```

### 2. Set up the database

Create a MySQL database named `bus-reservation`, then import the provided
[`bus-reservation.sql`](bus-reservation.sql) file to create the required tables.

```sql
CREATE DATABASE `bus-reservation`;
```

Import the schema using the MySQL CLI:

```bash
mysql -u root -p bus-reservation < bus-reservation.sql
```

Or, if you are using phpMyAdmin (e.g. via MAMP or XAMPP):

1. Open phpMyAdmin and select (or create) the `bus-reservation` database
2. Go to the **Import** tab
3. Choose `bus-reservation.sql` and click **Go**

This creates the `buses`, `customers`, and `reservations` tables, along with the foreign key
relationships between them, and loads a small set of sample data. Update the credentials in
[`DatabaseConnection.java`](src/main/java/com/example/busreservationsystem/DatabaseConnection.java)
if your local MySQL user and password differ from the defaults (`root` / `root`).

### 3. Build the project

```bash
./mvnw clean install
```

## Usage

Run the JavaFX application with the Maven plugin:

```bash
./mvnw javafx:run
```

This launches the loading screen, followed by the login screen. From there you can:

1. Register a new customer account, or log in with an existing one
2. Search for available buses
3. Select a seat from the interactive seat map and confirm the reservation
4. View bookings from the customer dashboard

## Database Schema

The full schema is defined in [`bus-reservation.sql`](bus-reservation.sql) and consists of three tables:

- **`buses`** — bus number, total seats, start/end points, start time, and fare
- **`customers`** — name, mobile, email, city, age, and a BCrypt-hashed password
- **`reservations`** — links a customer to a bus and seat number, with a unique constraint on
  `(bus_id, seat_number)` so a seat cannot be double-booked, and foreign keys back to `buses` and
  `customers` (both with `ON DELETE CASCADE`)

Import the file as described in [Installation](#installation) rather than creating the tables by hand.

## Built With

- Java 21
- JavaFX (`javafx-controls`, `javafx-fxml`, `javafx-web`, `javafx-swing`, `javafx-media`)
- MySQL with JDBC (`mysql-connector-j`)
- jBCrypt for password hashing
- ControlsFX, FormsFX, ValidatorFX, Ikonli, BootstrapFX, TilesFX, FXGL — JavaFX UI enhancement libraries
- Maven as the build tool

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

1. Fork the project
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Commit your changes: `git commit -m "Add some AmazingFeature"`
4. Push to the branch: `git push origin feature/AmazingFeature`
5. Open a pull request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

**Binidu01** - [@Binidu01](https://github.com/Binidu01)

Project Link: [https://github.com/Binidu01/Bus-Reservation-System](https://github.com/Binidu01/Bus-Reservation-System)

---

**[Back to Top](#bus-reservation-system)**
