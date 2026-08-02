# Expense Tracker

A Java + MySQL desktop application for tracking personal expenses, managing spending categories, and monitoring monthly salary vs. savings.

## Tech Stack
- Java (Swing GUI)
- JDBC
- MySQL

## Setup

1. Create the database and tables:
   ```
   mysql -u root -p expense_tracker < schema.sql
   ```

2. Configure your database credentials:
   - Copy `config.properties.example` to `config.properties`
   - On Windows, in PowerShell, from your project root, run `Copy-Item config.properties.example config.properties`
   - Fill in your MySQL username and password

3. Compile:
   ```
   javac -cp "lib/mysql-connector-j-26.7.0.jar" -d src src/Main.java src/db/DBConnection.java src/model/*.java src/dao/*.java src/ui/*.java
   ```

4. Run:
   ```
   java -cp "lib/mysql-connector-j-26.7.0.jar;src" Main
   ```

## Features
- Add, view, and delete expenses
- Manage spending categories with monthly budgets
- Set monthly salary and track savings
- Dashboard overview with live-updating totals
