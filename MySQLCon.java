package Project1;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MySQLCon {

	public static void main(String args[]) {
		Connection con = null;
		Scanner scanner = new Scanner(System.in);
		String dob = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:330/dentist?useSSL=false", "root", "SwaggyGopher47!");


			MySQLCon app = new MySQLCon();

			Statement stmt = con.createStatement();

			//Auto starting increment patientID
			ResultSet rs = stmt.executeQuery("SELECT MAX(PatientID) FROM Patient");
			int maxPatientId = 0;
			if (rs.next()) {
				maxPatientId = rs.getInt(1);
			}
			int nextAutoIncrementValue = maxPatientId + 1;
			String alterQuery = "ALTER TABLE Patient AUTO_INCREMENT = " + nextAutoIncrementValue;
			stmt.executeUpdate(alterQuery);
			System.out.println("AUTO_INCREMENT set to: " + nextAutoIncrementValue);

			//Auto starting increment procedureID
			rs = stmt.executeQuery("SELECT MAX(ProcedureID) FROM Procedures");
			int maxProcedureId = 0;
			if (rs.next()) {
				maxProcedureId = rs.getInt(1);
			}
			nextAutoIncrementValue = maxProcedureId + 1;
			alterQuery = "ALTER TABLE Procedures AUTO_INCREMENT = " + nextAutoIncrementValue;
			stmt.executeUpdate(alterQuery);
			System.out.println("AUTO_INCREMENT for Procedures set to: " + nextAutoIncrementValue);

			//Auto starting increment historyID
			rs = stmt.executeQuery("SELECT MAX(HistoryID) FROM PatientHistory");
			int maxHistoryId = 0;
			if (rs.next()) {
				maxHistoryId = rs.getInt(1);
			}
			nextAutoIncrementValue = maxHistoryId + 1;
			alterQuery = "ALTER TABLE PatientHistory AUTO_INCREMENT = " + nextAutoIncrementValue;
			stmt.executeUpdate(alterQuery);
			System.out.println("AUTO_INCREMENT for PatientHistory set to: " + nextAutoIncrementValue);

			while (true) {
				//Display menu
				System.out.println("\nMenu:");
				System.out.println("1. Add a Patient");
				System.out.println("2. Remove a Patient");
				System.out.println("3. Add a Procedure");
				System.out.println("4. Remove a Procedure");
				System.out.println("5. Add a Patient History");
				System.out.println("6. Remove a Patient History");
				System.out.println("7. Display All Patients");
				System.out.println("8. Display All Procedures");
				System.out.println("9. Display All Patient History");
				System.out.println("10. Exit");
				System.out.print("Choose an option: ");
				int choice = -1; //default
				try {
					choice = scanner.nextInt();
					scanner.nextLine();
				} catch (InputMismatchException e) {
					//incase non digit is entered
					System.out.println("Invalid option. Please enter a valid number.");
					scanner.nextLine();
					continue;
				}
				switch (choice) {
					case 1: //Add a patient

						while (true) {
							System.out.println("Please enter the following details:");

							//enter last name
							System.out.print("Last Name: ");
							String lastName = scanner.nextLine();
							while (lastName.trim().isEmpty() || !lastName.matches("[a-zA-Z]+([\\s][a-zA-Z]+)*")) {  //makes sure name is only letters and spaces and not empty
								System.out.println("Invalid last name. Please enter only letters and spaces, and ensure it is not empty."); //message for clarity to user
								System.out.print("Last Name: ");
								lastName = scanner.nextLine();
							}

							//enter first name
							System.out.print("First Name: ");
							String firstName = scanner.nextLine();
							while (firstName.trim().isEmpty() || !firstName.matches("[a-zA-Z]+([\\s][a-zA-Z]+)*")) {  //makes sure name is only letters and spaces and not empty
								System.out.println("Invalid first name. Please enter only letters and spaces, and ensure it is not empty."); //message for clarity to user
								System.out.print("First Name: ");
								firstName = scanner.nextLine();
							}

							//enter date of birth
							while (true) {
								System.out.print("Date of Birth (YYYY-MM-DD): ");
								dob = scanner.nextLine();
								if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) { //check if user entered date of birth in valid format
									System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format."); //message for clarity to user
									continue;
								}
								String[] dateParts = dob.split("-");  //split date of birth when "-" is present
								int year = Integer.parseInt(dateParts[0]);  //first split represents the year
								int month = Integer.parseInt(dateParts[1]);  //second split represents the month
								int day = Integer.parseInt(dateParts[2]);  //third split represents the day
								//Check if input is a valid date of birth
								if (year > 2024) {  //year check
									System.out.println("Year cannot be greater than 2024. Please try again.");  //message for clarity to user
								} else if (month < 1 || month > 12) {  //month check
									System.out.println("Month must be between 01 and 12. Please try again.");  //message for clarity to user
								} else if (day < 1 || day > 31) { //day check
									System.out.println("Day must be between 01 and 31. Please try again.");  //message for clarity to user
								} else if (!isValidDayForMonth(year, month, day)) {  //check if day works for that month and with leap year
									System.out.println("Invalid day for the given month. Please try again.");  //message for clarity to user
								} else {
									System.out.println("Date of Birth is valid: " + dob);  //valid date of birth
									break;
								}
							}

							//enter address
							System.out.print("Address: ");
							String address = scanner.nextLine();
							while (address.trim().isEmpty()){  //check if address is empty or only spaces
								System.out.println("Address cannot be empty. Please enter a valid address");  //message for clarity to user
								System.out.print("Address: ");
								address = scanner.nextLine();
							}

							//enter phone number
							System.out.print("Phone Number: ");
							String phoneNumber = scanner.nextLine().replaceAll("[^\\d]", ""); //get rid of non digits characters
							while (phoneNumber.length() != 10) { //check to make sure phone number is exactly 10 digits
								System.out.println("Invalid phone number. Please enter exactly 10 digits.");  //message for clarity to user
								System.out.print("Phone Number: ");
								phoneNumber = scanner.nextLine().replaceAll("[^\\d]", ""); //get rid of non digit characters
							}
							String formattedPhoneNumber = String.format("(%s)-%s-%s",  //format for user to be put into database
									phoneNumber.substring(0, 3),   //first 3 digits
									phoneNumber.substring(3, 6),   //4th-6th digit
									phoneNumber.substring(6, 10)); //Last 4 digits
							System.out.println("Formatted Phone Number: " + formattedPhoneNumber); //displays phone number to user formatted

							//enter medical record number
							System.out.print("Medical Record Number: ");
							String medicalRecordNumber = scanner.nextLine();
							while (true) {
								//check if correct format
								if (!medicalRecordNumber.matches("\\d{6,10}")) {  //only 6 to 10 digits
									System.out.println("Invalid Medical Record Number. It must be between 6 and 10 digits.");  //message for clarity to user
									System.out.print("Medical Record Number: ");
									medicalRecordNumber = scanner.nextLine();
									continue;
								}
								//check database if medical record number already exists
								if (!isMedicalRecordNumberUnique(con, medicalRecordNumber)) {
									System.out.println("This Medical Record Number already exists. Please enter a unique one.");  //message for clarity to user
									System.out.print("Medical Record Number: ");
									medicalRecordNumber = scanner.nextLine();
									continue;
								}
								break;
							}

							//enter insurance provider
							System.out.print("Insurance Provider: ");
							String insuranceProvider = scanner.nextLine();
							//check to see is entry is empty or has non letters
							while (insuranceProvider.trim().isEmpty() ||!insuranceProvider.matches("[a-zA-Z ]+")) {
								System.out.println("Inusrance provider cannot be empty and only contain letters and spaces.");  //message for clarity to user
								System.out.print("Insurance Provider: ");
								insuranceProvider = scanner.nextLine();
							}

							//enter insurance number
							System.out.print("Insurance Number: ");
							String insuranceNumber = scanner.nextLine().toUpperCase();  //makes input uppercase
							//check to ensure input is only letter and numbers and has between 8 and 10 characters
							while (!insuranceNumber.matches("[A-Z0-9]{8,10}")) {
								System.out.println("Invalid insurance number. It must contain between 8 to 10 characters letters and digits.");  //message for clarity to user
								System.out.print("Insurance Number: ");
								insuranceNumber = scanner.nextLine().toUpperCase(); //makes input upper case
							}

							//enter emergeny contact name
							System.out.print("Emergency Contact Full Name: ");
							String emergencyContactName = scanner.nextLine();
							//check to make see if input is empty or has non letters
							while (emergencyContactName.trim().isEmpty() || !emergencyContactName.matches("[a-zA-Z]+([\\s][a-zA-Z]+)*")) {
								System.out.println("Invalid emergency contact name. Please enter only letters and spaces, and ensure it is not empty.");  //message for clarity to user
								System.out.print("Emergency Contact Full Name: ");
								emergencyContactName = scanner.nextLine();
							}

							//enter emergency contact phone number
							System.out.print("Emergency Contact Phone Number: ");
							String emergencyContactPhone = scanner.nextLine().replaceAll("[^\\d]", ""); //get rid of non digit characters
							//check to see if emergency contact phone number is exactly 10 digits
							while (emergencyContactPhone.length() != 10) {
								System.out.println("Invalid emergency contact phone number. Please enter exactly 10 digits.");  //message for clarity to user
								System.out.print("Emergency Contact Phone Number: ");
								emergencyContactPhone = scanner.nextLine().replaceAll("[^\\d]", ""); //get rid of non digit characters
							}
							String formattedEmergencyContactPhone = String.format("(%s)-%s-%s",  //format for user to be put into database
									emergencyContactPhone.substring(0, 3),   //first 3 digits
									emergencyContactPhone.substring(3, 6),   //4th-6th digit
									emergencyContactPhone.substring(6, 10)); // Last 4 digits
							System.out.println("Formatted Emergency Contact Phone Number: " + formattedEmergencyContactPhone);

							//Display entered information
							System.out.println("\nYou entered the following information:");
							System.out.println("Last Name: " + lastName);
							System.out.println("First Name: " + firstName);
							System.out.println("Date of Birth: " + dob);
							System.out.println("Address: " + address);
							System.out.println("Phone Number: " + formattedPhoneNumber);
							System.out.println("Medical Record Number: " + medicalRecordNumber);
							System.out.println("Insurance Provider: " + insuranceProvider);
							System.out.println("Insurance Number: " + insuranceNumber);
							System.out.println("Emergency Contact Name: " + emergencyContactName);
							System.out.println("Emergency Contact Phone: " + formattedEmergencyContactPhone);
							System.out.print("\nIs this information correct? (yes/no): ");  //check with user to ensure all info is correct
							String confirmation = scanner.nextLine();

							//add if yes
							if (confirmation.equalsIgnoreCase("yes")) {
								app.addPatient(con, lastName, firstName, dob, address, formattedPhoneNumber, medicalRecordNumber,
										insuranceProvider, insuranceNumber, emergencyContactName, formattedEmergencyContactPhone);
								break;
							} else { //if not yes, then restart add a patient
								System.out.println("Let's try again.");
							}
						}
						break;

					case 2:  //Remove a patient

						while (true) {
							int patientId = -1;  //default patientId


							while (true) {
								System.out.print("Enter the PatientID of the patient to remove: ");
								String patientInput = scanner.nextLine();  //user enters digit of id they want removed

								//Check if input is a digit
								if (patientInput.matches("\\d+")) {
									patientId = Integer.parseInt(patientInput);

									//Check if the Patient ID exists
									if (!app.patientExists(con, patientId)) {
										System.out.println("PatientID does not exist. Please enter a valid PatientID.");  //message for clarity to user
									} else {
										break;
									}
								} else {  //if non digit is entered then try again
									System.out.println("Invalid input. Please enter a valid PatientID (digits only).");  //message for clarity to user
								}
							}
							//Confirm removing a patient
							System.out.print("Are you sure you want to delete this patient? (yes/no): ");
							String confirmPatient = scanner.nextLine();
							//if not yes then operation is canceled and sent to menu
							if (!confirmPatient.equalsIgnoreCase("yes")) {
								System.out.println("Operation canceled.");
								break;
							}
							//remove patient
							app.removePatient(con, patientId);
							//Reindex PatientIDs so all id are updated
							System.out.println("Reindexing PatientIDs...");
							app.reindexPatientIDs(con);
							System.out.println("Reindexing completed.");  //let user know ids have been updated
							break;
						}
						break;

					case 3: //enter a procedure

						while (true) {
						System.out.print("Enter a procedure you want to add: ");
						String procedureName = scanner.nextLine();

						//check if input is empty or contains something other than letters and spaces
						while (procedureName.trim().isEmpty() || !procedureName.matches("[a-zA-Z\\s]+")) {
							System.out.println("Invalid procedure name. Please enter a name containing only letters and spaces.");  //message for clarity to user
							System.out.print("Enter a procedure you want to add: ");
							procedureName = scanner.nextLine();
						}
						//Confirm procedure info for user to see if correct
						System.out.println("You entered: \"" + procedureName + "\". Is this correct? (yes/no): ");
						String confirmCorrectProcedure = scanner.nextLine();
						if (confirmCorrectProcedure.equalsIgnoreCase("yes")) {
							app.addProcedure(con, procedureName); //if yes then add the procedure
							break;
						} else { //if not yes restart add a procedure
							System.out.println("Let's try again.");
						}
					}
						break;

					case 4:  //remove a procedure

						while (true) {
							int procedureId = -1;  //default

							while (true) {
								System.out.print("Enter the ProcedureID of the procedure to remove: ");
								String procedureInput = scanner.nextLine();  //user enter digit of id wanting to be removed

								//Check if input is a digit
								if (procedureInput.matches("\\d+")) {
									procedureId = Integer.parseInt(procedureInput);

									//Check if the Procedure ID exists
									if (!app.procedureExists(con, procedureId)) {
										System.out.println("ProcedureID does not exist. Please enter a valid ProcedureID.");  //message for clarity to user
									} else {
										break;
									}
								} else {
									System.out.println("Invalid input. Please enter a valid ProcedureID (digits only).");  //message for clarity to user
								}
							}
							//Confirm removing a procedure
							System.out.print("Are you sure you want to delete this procedure? (yes/no): ");
							String confirmProcedure = scanner.nextLine();
							//if not yes, then operation canceled and sent to menu
							if (!confirmProcedure.equalsIgnoreCase("yes")) {
								System.out.println("Operation canceled.");
								break;
							}
							//remove the procedure
							app.removeProcedure(con, procedureId);
							//Reindex ProcedureIDs
							System.out.println("Reindexing ProcedureIDs...");
							app.reindexProcedureIDs(con);
							System.out.println("Reindexing completed.");  //let user know ids have been updated
							break;

						}
						break;

					case 5:  //Add a Patient History

						while (true) {
							int patientID = -1;  //default
							while (true) {
								System.out.print("Enter PatientID: ");
								String patientInput = scanner.nextLine();
								//Check if input is a digit
								if (patientInput.matches("\\d+")) {
									patientID = Integer.parseInt(patientInput);
									//Check if the PatientID exists
									if (!app.patientExists(con, patientID)) {
										System.out.println("PatientID does not exist. Please enter a valid PatientID.");  //message for clarity to user
									} else {
										break;
									}
								} else {
									System.out.println("Invalid input. Please enter a valid PatientID (digits only).");  //message for clarity to user
								}
							}
							int procedureID = -1;  //default
							while (true) {
								System.out.print("Enter ProcedureID: ");
								String procedureInput = scanner.nextLine();
								//Check if input is a digit
								if (procedureInput.matches("\\d+")) {
									procedureID = Integer.parseInt(procedureInput);
									//Check if the ProcedureID exists
									if (!app.procedureExists(con, procedureID)) {
										System.out.println("ProcedureID does not exist. Please enter a valid ProcedureID.");  //message for clarity to user
									} else {
										break;
									}
								} else {
									System.out.println("Invalid input. Please enter a valid ProcedureID (digits only).");  //message for clarity to user
								}
							}
							String procedureDate = "";  //default
							while (true) {
								System.out.print("Enter Procedure Date (YYYY-MM-DD): ");  //message for clarity to user
								procedureDate = scanner.nextLine();

								//Check if format is YYYY-MM-DD
								if (!procedureDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
									System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");  //message for clarity to user
									continue;
								}
								//Split the date when - appears
								String[] dateParts = procedureDate.split("-");
								int year = Integer.parseInt(dateParts[0]);  //year
								int month = Integer.parseInt(dateParts[1]);  //month
								int day = Integer.parseInt(dateParts[2]);  //day

								//Check if input is valid date for procedure
								if (year > 2024) {  //check year
									System.out.println("Year cannot be greater than 2024. Please try again.");  //message for clarity to user
								} else if (month < 1 || month > 12) {  //check month
									System.out.println("Month must be between 01 and 12. Please try again.");  //message for clarity to user
								} else if (day < 1 || day > 31) {  //check day
									System.out.println("Day must be between 01 and 31. Please try again.");  //message for clarity to user
								} else if (!isValidDayForMonth(year, month, day)) {  //check if day is valid for month including leap years
									System.out.println("Invalid day for the given month. Please try again.");  //message for clarity to user
								} else {
									System.out.println("Procedure Date is valid: " + procedureDate);  //valid procedure date
									break;
								}
							}
							//display input for user to confrim if correct
							System.out.println("You are adding the following Patient History:");
							System.out.println("PatientID: " + patientID + ", ProcedureID: " + procedureID + ", Procedure Date: " + procedureDate);
							System.out.print("Is this correct? (yes/no): ");
							String confirmHistory = scanner.nextLine();
							//if not yes, then restart add a patient history
							if (confirmHistory.equalsIgnoreCase("yes")) {
								try {
									app.addPatientHistory(con, patientID, procedureID, procedureDate);  //add a patient history
									break;
								} catch (Exception e) {
									System.out.println("An error occurred while adding the patient history: " + e.getMessage());
								}
							} else {
								System.out.println("Let's try again.");
							}
						}
						break;

					case 6: //remove a patient history

						while (true) {
							int patientHistoryId = -1;
							while (true) {
								System.out.print("Enter the PatientHistoryID of the patient history record to remove: "); //message for clarity to user
								String patientHistoryInput = scanner.nextLine();
								//Check if input is a digit
								if (patientHistoryInput.matches("\\d+")) {
									patientHistoryId = Integer.parseInt(patientHistoryInput);
									//Check if Patient History ID exists
									if (!app.patientHistoryExists(con, patientHistoryId)) {
										System.out.println("Patient HistoryID does not exist. Please enter a valid Patient HistoryID."); //message for clarity to user
									} else {
										break;
									}
								} else {
									System.out.println("Invalid input. Please enter a valid Patient HistoryID (digits only)."); //message for clarity to user
								}
							}

							//Confirm remove the patient history
							System.out.print("Are you sure you want to delete this patient history record? (yes/no): ");
							String confirmHistory = scanner.nextLine();
							//if not yes, then cancel operation and go back to menu
							if (!confirmHistory.equalsIgnoreCase("yes")) {
								System.out.println("Operation canceled.");
								break;
							}
							//remove patient history
							app.removePatientHistory(con, patientHistoryId);
							System.out.println("Reindexing PatientHistoryIDs...");
							app.reindexHistoryIDs(con);
							System.out.println("Reindexing completed.");  //let user know reindex is complete
							break;
						}
						break;

					case 7:  //Display all patients

						System.out.println("\nAll Patients:");
						app.displayAllPatients(con);
						break;

					case 8:  //Display all procedures

						System.out.println("\nAll Procedures:");
						app.displayAllProcedures(con);
						break;

					case 9:  //Display all Patient History

						System.out.println("\nAll Patient History:");
						app.displayAllHistory(con);
						break;

					case 10:
						//Exit the program
						System.out.println("Exiting the program. Goodbye!");
						return;

					default:  //if invalud input tells user to try again

						System.out.println("Invalid option. Please try again.");  //message for clarity to user
						break;
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				if (con != null) con.close();
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
	}

	//Method to add a patient
	public static void addPatient(Connection con, String lastName, String firstName, String dob, String address,
						   String phoneNumber, String medicalRecordNumber, String insuranceProvider,
						   String insuranceNumber, String emergencyContactName, String emergencyContactPhone) {
		String query = "INSERT INTO Patient (LastName, FirstName, DateOfBirth, Address, PhoneNumber, MedicalRecordNumber, "
				+ "InsuranceProvider, InsuranceNumber, EmergencyContactName, EmergencyContactPhoneNumber) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setString(1, lastName);
			pstmt.setString(2, firstName);
			pstmt.setDate(3, Date.valueOf(dob));
			pstmt.setString(4, address);
			pstmt.setString(5, phoneNumber);
			pstmt.setString(6, medicalRecordNumber);
			pstmt.setString(7, insuranceProvider);
			pstmt.setString(8, insuranceNumber);
			pstmt.setString(9, emergencyContactName);
			pstmt.setString(10, emergencyContactPhone);
			int rowsInserted = pstmt.executeUpdate();
			System.out.println(rowsInserted + " patient(s) added.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to remove a patient
	public static void removePatient(Connection con, int patientId) {
		String query = "DELETE FROM Patient WHERE PatientID = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, patientId);
			int rowsDeleted = pstmt.executeUpdate();
			System.out.println(rowsDeleted + " patient(s) deleted.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to reindex PatientID
	public static void reindexPatientIDs(Connection con) {
		try {
			//Order PatientID
			String query = "SELECT PatientID FROM Patient ORDER BY PatientID";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int newPatientId = 1;
			while (rs.next()) {
				int currentPatientId = rs.getInt("PatientID");
				//Update PatientID
				if (currentPatientId != newPatientId) {
					String updateQuery = "UPDATE Patient SET PatientID = ? WHERE PatientID = ?";
					try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
						pstmt.setInt(1, newPatientId);
						pstmt.setInt(2, currentPatientId);
						pstmt.executeUpdate();
						System.out.println("PatientID " + currentPatientId + " reassigned to " + newPatientId);
					}
				}
				newPatientId++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to display all patients
	public static void displayAllPatients(Connection con) {
		String query = "SELECT LastName, FirstName, Address FROM Patient ORDER BY LastName";
		try (Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				String lastName = rs.getString("LastName");
				String firstName = rs.getString("FirstName");
				String address = rs.getString("Address");
				System.out.println("Last Name: " + lastName + ", First Name: " + firstName + ", Address: " + address);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to check if day is valid
	public static boolean isValidDayForMonth(int year, int month, int day) {
		int daysInMonth;

		switch (month) {
			case 1: case 3: case 5: case 7: case 8: case 10: case 12:
				daysInMonth = 31;
				break;
			case 4: case 6: case 9: case 11:
				daysInMonth = 30;
				break;
			case 2:
				//Check for leap year for February
				if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
					daysInMonth = 29; //Leap year
				} else {
					daysInMonth = 28; //Non-leap year
				}
				break;
			default:
				return false; //Invalid month
		}
		return day <= daysInMonth;
	}

	//Method for Unique MRN Check
	public static boolean isMedicalRecordNumberUnique(Connection con, String medicalRecordNumber) {
		String query = "SELECT COUNT(*) FROM Patient WHERE MedicalRecordNumber = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setString(1, medicalRecordNumber);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				return count == 0;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	//Method to add a procedure
	public static void addProcedure(Connection con, String procedureName) {
		String query = "INSERT INTO Procedures (ProcedureName) VALUES (?)";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setString(1, procedureName);
			int rowsInserted = pstmt.executeUpdate();
			System.out.println(rowsInserted + " procedure(s) added.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to remove a procedure
	public static void removeProcedure(Connection con, int procedureId) {
		String query = "DELETE FROM Procedures WHERE ProcedureID = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, procedureId);
			int rowsDeleted = pstmt.executeUpdate();
			System.out.println(rowsDeleted + " procedure(s) deleted.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to reindex PatientID
	public static void reindexProcedureIDs(Connection con) {
		try {
			//Order ProcedureID
			String query = "SELECT ProcedureID FROM Procedures ORDER BY ProcedureID";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int newProcedureId = 1;
			while (rs.next()) {
				int currentProcedureId = rs.getInt("ProcedureID");
				//Update ProcedureID
				if (currentProcedureId != newProcedureId) {
					String updateQuery = "UPDATE Procedures SET ProcedureID = ? WHERE ProcedureID = ?";
					try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
						pstmt.setInt(1, newProcedureId);
						pstmt.setInt(2, currentProcedureId);
						pstmt.executeUpdate();
						System.out.println("ProcedureID " + currentProcedureId + " reassigned to " + newProcedureId);
					}
				}
				newProcedureId++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to display all procedures
	public static void displayAllProcedures(Connection con) {
		String query = "SELECT ProcedureName FROM Procedures ORDER BY ProcedureName";
		try (Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				String procedureName = rs.getString("ProcedureName");
				System.out.println("Procedure Name: " + procedureName);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to add a patient history
	public static void addPatientHistory(Connection con, int patientID, int procedureID, String procedureDate) {
		String query = "INSERT INTO PatientHistory (PatientID, ProcedureID, ProcedureDate) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, patientID);
			pstmt.setInt(2, procedureID);
			pstmt.setDate(3, Date.valueOf(procedureDate));
			int rowsInserted = pstmt.executeUpdate();
			System.out.println(rowsInserted + " patient history record(s) added.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to check if patient exists
	public static boolean patientExists(Connection con, int patientID) {
		String query = "SELECT COUNT(*) FROM Patient WHERE PatientID = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, patientID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	//Method to check if procedure exists
	public static boolean procedureExists(Connection con, int procedureID) {
		String query = "SELECT COUNT(*) FROM Procedures WHERE ProcedureID = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, procedureID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	//Method to remove a patient history
	public static void removePatientHistory(Connection con, int historyID) {
		String query = "DELETE FROM PatientHistory WHERE HistoryID = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, historyID);
			int rowsDeleted = pstmt.executeUpdate();
			System.out.println(rowsDeleted + " patient history record(s) deleted.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to check if patient history exists
	public static boolean patientHistoryExists(Connection con, int patientHistoryId) {
		String query = "SELECT COUNT(*) FROM PatientHistory WHERE HistoryID = ?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setInt(1, patientHistoryId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				return count > 0;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	//Method to reindex HistoryID
	public static void reindexHistoryIDs(Connection con) {
		try {
			//Order HistoryID
			String query = "SELECT HistoryID FROM PatientHistory ORDER BY HistoryID";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int newHistoryId = 1;
			while (rs.next()) {
				int currentHistoryId = rs.getInt("HistoryID");
				//Update HistoryID
				if (currentHistoryId != newHistoryId) {
					String updateQuery = "UPDATE PatientHistory SET HistoryID = ? WHERE HistoryID = ?";
					try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
						pstmt.setInt(1, newHistoryId);
						pstmt.setInt(2, currentHistoryId);
						pstmt.executeUpdate();
						System.out.println("HistoryID " + currentHistoryId + " reassigned to " + newHistoryId);
					}
				}
				newHistoryId++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Method to display all patient history
	public static void displayAllHistory(Connection con) {
		String query = "SELECT HistoryID, PatientID, ProcedureID, ProcedureDate FROM PatientHistory ORDER BY ProcedureDate";
		try (Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int historyID = rs.getInt("HistoryID");
				int patientID = rs.getInt("PatientID");
				int procedureID = rs.getInt("ProcedureID");
				Date procedureDate = rs.getDate("ProcedureDate");
				System.out.println("HistoryID: " + historyID + ", PatientID: " + patientID +
						", ProcedureID: " + procedureID + ", ProcedureDate: " + procedureDate);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}