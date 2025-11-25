📋 Online Health Consultation System

A Java Swing + MySQL based desktop application for booking and managing doctor–patient appointments.
This project demonstrates GUI development, JDBC operations, DAO patterns, and database interaction.

---

## 📂 Project Structure

OnlineHealthConsultation/  
│── src/  
│   └── com/healthcare/  
│       ├── dao/  
│       ├── gui/  
│       ├── models/  
│       ├── utils/  
│       └── Main.java  
│── database/  
│   └── schema.sql  
│── lib/  
│   └── mysql-connector-j-9.5.0.jar  
│── out/ (compiled .class files)  
└── README.md  

---

## 🗄️ Database Setup (MySQL)

1. Open MySQL Workbench or Command Line  
2. Create database:
   
      CREATE DATABASE healthcare_db;
   
      USE healthcare_db;
   
      SOURCE database/schema.sql;

4. Update DB credentials inside:

`src/com/healthcare/utils/DBConfig.java`

Example:

       public static final String URL = "jdbc:mysql://localhost:3306/healthcare_db";
       
       public static final String USER = "root";
       
       public static final String PASSWORD = "your_password";

       
---

## ▶️ Running the Project

### **Compile**
            javac -cp "lib/mysql-connector-j-9.5.0.jar" -d out $(Get-ChildItem -Recurse src/*.java)

### **Run**
            java -cp "out;lib/mysql-connector-j-9.5.0.jar" com.healthcare.Main

---

### Test Login Accounts

**Patient Login**
- Username: patient
- Password: pass

**Another Patient**
- Username: patient2
- Password: pass2

**Doctor Login**
- Username: drsmith
- Password: pass123


## ✔️ Features

### **Patient**
- Login  
- Book a doctor appointment  
- View appointment history  

### **Doctor**
- Login  
- See assigned appointments  
- Mark appointments as completed  

---

## 🛠 Technologies Used
- Java 17+  
- Swing (GUI)  
- MySQL 8+  
- JDBC + DAO Pattern  

---

## 🚀 Future Enhancements
- Admin module  
- Appointment cancellation/reschedule  
- Prescription module  
- Email/SMS notifications  

            
    
