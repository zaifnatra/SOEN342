# Initial Requirements Analysis

## Actors
| Actor | Description |
|---|---|
| Patient | Requests appointments and receives medical services. |
| Administrative Staff | Manages patient registration and appointments. |
| Healthcare Professional | Provides consultations and records clinical information. |

## Critical Use Cases
| Use Case | Description | Actors |
|---|---|---|
|Register patient | Creates a new patient record with a unique patient identifier and personal information. RAMQ or private insurance informatiin is recorded (if available).  | Administrative Staff | 
|Request/Book appointment | Requests and schedules an appointment after verifying the healthcare professional's availability, applicable scheduling constraints, and whether a medical referral is required. If all requirements are satisfied, the system automatically confirms the appointment and updates its status to confirmed.| Administrative Staff, Patient | 
| Cancel an Appointment | Cancels an existing appointment and updates its status to cancelled in the system. | Administrative Staff, Patient |
| Reschedule an Appointment |Changes an existing appointment to another available date or time after checking the healthcare professional's availability and applicable scheduling constraints.| Administrative Staff, Patient |
| Referral creation | Creates a specialist consultation request identifying the patient, referring professional, reason for referral, requested specialty or profession, priority, and referral status. A referral may result in one or more appointments. | Healthcare Professional |
| Record Consultation | Records observations, diagnoses, treatments, and follow-up instructions in the patient's clinical record. Can include prescriptions and recommendations for follow-up appointments when necessary. | Healthcare Professional |
| Issue Prescription | Creates a prescription for a patient containing one or more medications when needed. | Healthcare Professional |
| Manage Healthcare Professional | Registers or updates healthcare professional information, including specialties and clinic locations. | Administrative Staff |
| Manage Professional Schedule | Defines healthcare professional availability by location and checks for scheduling conflicts across locations. | Administrative Staff |
| Request Follow-Up Appointment | Allows a healthcare professional to request a follow-up appointment after a consultation. The appointment is scheduled through the Book Appointment process. | Healthcare Professional |



## Non-Critical Use Cases

| Use Case | Description | Actor |
|---|---|---|
| Search for Patient | Searches for an existing patient using their insurance number, date of birth, phone number, or partial first or last name| Administrative Staff |
| Register Patient Arrival | Records a patient's arrival at the clinic for their appointment. | Administrative Staff |
| View Patient Clinical Record | Allows authorized healthcare professionals to view a patient's clinical information based on their access permissions. | Healthcare Professional |
| View Administrative Information | Allows administrative staff to view patient registration and appointment information based on their access permissions. | Administrative Staff |

## Notes: 
- Access to clinical and administrative information is restricted based on the user's role.
- Relevant activities recorded by system, to support accountability and protection of confidentiality of patient’s information.

## Assumptions: 
 - Manage Healthcare Professional Assumptions for the SSD: 
    The system description states that healthcare professionals have professional information, specilties, schedules and clinic locations. However, there is no explicit mention of who manages this information so for now we assume: 
    1. Admin Staff is authrorized to manage healthcare professional information
    2. Admin Staff can register new heatlhcare professionals and update existing healthcare professional information. 
    3. Registering and updating healthcare professionals are grouped under the ManageHealthcareProfessional use case and admin staff can perform either action independently. 



