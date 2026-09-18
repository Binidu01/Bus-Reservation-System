-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 18, 2026 at 10:20 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bus-reservation`
--

-- --------------------------------------------------------

--
-- Table structure for table `buses`
--

CREATE TABLE `buses` (
  `id` int(11) NOT NULL,
  `bus_number` varchar(50) NOT NULL,
  `total_seats` int(11) NOT NULL,
  `start_point` varchar(100) NOT NULL,
  `end_point` varchar(100) NOT NULL,
  `start_time` varchar(10) NOT NULL,
  `fare` double NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `buses`
--

INSERT INTO `buses` (`id`, `bus_number`, `total_seats`, `start_point`, `end_point`, `start_time`, `fare`, `created_at`) VALUES
(1, 'NB 0001', 50, 'Matara', 'Colombo', '12:30', 700, '2026-09-17 18:21:08');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `mobile` varchar(15) NOT NULL,
  `email` varchar(150) NOT NULL,
  `city` varchar(100) NOT NULL,
  `age` int(11) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `name`, `mobile`, `email`, `city`, `age`, `password`, `created_at`) VALUES
(1, 'Binidu', '0787296686', 'rbinidu@gmail.com', 'Matara', 22, '$2a$10$J1J1RTR4wiANA6.06tVy7uaPSME0dV3N7rPu1MM8Jzf8iI3xE5kHW', '2026-09-17 18:20:00');

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `bus_id` int(11) NOT NULL,
  `seat_number` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'reserved',
  `reserved_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`id`, `bus_id`, `seat_number`, `customer_id`, `status`, `reserved_at`) VALUES
(1, 1, 48, 1, 'reserved', '2026-09-17 18:21:46');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `buses`
--
ALTER TABLE `buses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `bus_number` (`bus_number`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_bus_seat` (`bus_id`,`seat_number`),
  ADD KEY `fk_reservation_customer` (`customer_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `buses`
--
ALTER TABLE `buses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `fk_reservation_bus` FOREIGN KEY (`bus_id`) REFERENCES `buses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reservation_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
