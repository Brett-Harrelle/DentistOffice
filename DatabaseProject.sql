Create Database Dentist;

Use Dentist;



CREATE TABLE Patient (

	PatientID INT PRIMARY KEY AUTO_INCREMENT,
	LastName varchar(50) NOT NULL,
    FirstName varchar(50) NOT NULL,
    DateOfBirth date NOT NULL,
    Address varchar(100),
    PhoneNumber varchar(20),
    MedicalRecordNumber varchar(50) unique NOT NULL,
    InsuranceProvider varchar(50) NOT NULL,
    InsuranceNumber varchar(50),
    EmergencyContactName varchar(50),
    EmergencyContactPhoneNumber varchar(20)
    
    


);

CREATE TABLE Procedures(
	 ProcedureID INT PRIMARY KEY AUTO_INCREMENT,
    ProcedureName varchar(50)
);

    
CREATE TABLE PatientHistory (
	
    HistoryID INT PRIMARY KEY AUTO_INCREMENT,
    PatientID INT NOT NULL,
    ProcedureID INT NOT NULL,
    ProcedureDate date NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patient(PatientID),
    FOREIGN KEY (ProcedureID) REFERENCES Procedures(ProcedureID)
    
    
    

);

