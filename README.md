# Human Resources, Payroll, and Performance Management System (HRMS)

An enterprise-grade desktop application designed to manage core human resources, automated payroll processing, and multi-dimensional performance evaluation. Built using robust software engineering principles, the system leverages a strict multi-layered architecture, relational integrity constraints, and database-level automation.

The architectural decisions, schema design, and core algorithms implemented in this project are explicitly designed to align with the technical specifications in **"İK SİSTEMİ.pdf"**.

---

##  Architectural Overview

The application strictly implements a **Layered Architecture (DAO-Service-Controller-View)** combined with the **MVC pattern** to ensure high cohesion, loose coupling, and a clean separation of concerns.
[Presentation Layer (JavaFX)] ──> [Controller Layer] ──> [Service Layer (Business Logic)] ──> [DAO Layer] ──> [MySQL Engine]

*   **Presentation Layer (`view/`):** Built with JavaFX 21, isolating visual components and applying modular CSS styling.
*   **Controller Layer (`controller/`):** Manages user interaction and UI state propagation without directly binding business logic to interface components.
*   **Service Layer (`services/`):** Handles core business logic (e.g., compounding tax models, calculating net wage formulations, and evaluating performance scores).
*   **DAO Layer (`dao/`):** Encapsulates lower-level JDBC connectivity, abstracting data persistence from application routines.
*   **Database Layer (`MySQL`):** Structured storage layer implementing referential integrity constraints, functional stored procedures, and triggers.

---

##  Tech Stack & Dependencies

*   **Runtime Environment:** Java 17 LTS
*   **GUI Framework:** JavaFX 21
*   **Database Server:** MySQL Engine 8.x
*   **Data Access Technology:** Native Java Database Connectivity (JDBC) API
*   **Build Automation:** Maven
*   **Testing Infrastructure:** JUnit 5

---

##  Design Patterns & Engineering Practices

*   **Data Access Object (DAO) Pattern:** Decouples persistence management via custom gateways (`EmployeeDAO`, `PayrollDAO`, `ReviewDAO`) derived from a shared `BaseDAO`.
*   **Singleton-Like Connection Factory:** Centralizes database sessions via `DatabaseConnection`. Enforcing a `private` constructor ensures predictable state tracking.
*   **Auditable Soft Delete Architecture:** Avoids destructive SQL `DELETE` clauses. State changes are managed dynamically via status attributes (`ACTIVE`, `INACTIVE`, `TERMINATED`), preserving historical ledger entries.

