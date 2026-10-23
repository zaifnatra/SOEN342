
# Fully Dressed Use Case: Record Consultation

| Field | Description |
|---|---|
| **ID** | UC-1 |
| **Title** | Record Consultation |
| **Description** | This use case allows the healthcare professional to record a consultation details during an apppointment. The healthcare professionl records observations, diagnoses, treatment recommendations, and follow-up requirements as needed. The system saves  consultation in the patient's record and links to the correspoonding appointment.|
| **Primary Actor** | Healthcare Professional |
| **Stakeholders and Interests** | **Healthcare Professional:** Wants to record all  consultation details <br> **Patient:** Wants their consultation details to be saved as a reference for their treatment plan. |
| **Pre-Conditions** | 1. The appointment already exists and has a unique AppointmentID.<br> 2. The healthcare professional has authorization to record consultations. |
| **Post-Conditions** | **Success:**<br> The consultation details are saved in the system, including observations, diagnoses, treatment recommendations, and follow-up requirements as needed. The consultation is associated with the corresponding appointment and patient. The system displays a confirmation message. <br><br> **Failure:**<br> 1. If the healthcare professional is not authorized to access the selected appointment, the system displays an error message: "Access Denied." No consultation is recorded.<br> 2. If the healthcare professional submits invalid consultation details, the system displays an error message. The invalid details are not saved, and the healthcare professional can correct the information and try again. |

| **Inputs** | 1. appointmentID (Identifier)<br>2. observations (Text)<br>3. diagnoses (Text)<br>4. treatmentRecommendation (Text)<br>5. followUpRequirements (Text, if needed) |
|---|---|
| **Outputs** | **Success:**<br>1. Appointment details (patientID, appointmentDate, status).<br>2. Confirmation message: "Consultation has been recorded."<br><br>**Failure:**<br>1. Error message: "Access Denied" if the healthcare professional is not authorized.<br>2. Error message if the consultation details are invalid. |
| **Main Success Scenario** | 1. The healthcare professional selects an appointment using its appointmentID.<br>2. The system checks that the healthcare professional is authorized to access the appointment.<br>3. The system displays the appointment details, including patientID, appointmentDate, and status.<br> 4. The healthcare professional enters consultation details, including observations, diagnoses, treatment recommendations, and follow-up requirements as needed.<br>5. The healthcare professional submits the consultation details.<br>6. The system checks if the submitted details are valid<br>7. The system saves the consultation and links it with the correct appointment and patient.<br>8. The system displays a confirmation message: "Consultation has been recorded." |
| **Extensions (Alternative / Failure Scenarios)** | **2a. Healthcare professional is not authorized:**<br>1. The system displays an error message: "Access Denied."<br>2. No consultation is will be recorded Use case ends.<br><br>**6a. Invalid consultation details:**<br>1. The system detects that the consultation details are invalid.<br>2. The system displays an error message <br>3. The healthcare professional corrects the consultation details.<br>4. The use case continues at Step 5. |
| **Special Requirements** | 1. Only authorized healthcare professionals can record consultation details. <br>2. Consultations must be linked to the correct patient and appointment.<br>3. Patient information need to be protected from unauthorized access. |
| **Technology and Data Variations List** |  Consultation details will be different based on whether diagnoses, treatment recommendations, or follow-up requirements are applicable.<br>|
| **Open Issues** | 1. What rules will the system use to  decide whether consultation details are valid?<br>2. Can healthcare professionals edit consultation details after they have been saved?<br>|
