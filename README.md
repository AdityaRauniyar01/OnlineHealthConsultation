# OnlineHealthConsultation

Minimal Java project skeleton for an online health consultation app.

Structure:
- `src/com/healthcare` - main package
  - `models/` - `User`, `Patient`, `Doctor`, `Appointment`
  - `dao/` - data-access skeletons including `DBConnection`
  - `gui/` - Swing frames `LoginFrame`, `PatientDashboard`
  - `utils/` - helper utilities
- `database/schema.sql` - example DB schema

How to use:
- Open the project in your IDE (IntelliJ / Eclipse).
- Implement `DBConnection.getConnection()` and DAO methods.
- Compile with `javac` and run `com.healthcare.Main`.
