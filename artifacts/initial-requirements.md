# Initial Requirements Analysis

## Actors
| Actor | Description |
|---|---|
| Patient | Requests appointments and receives medical services. |
| Administrative Staff | Manages patient registration and appointments. |
| Healthcare Professional | Provides consultations and records clinical information. |
| Referring Professional | Issues consultation requests for patients. |

## Critical Use Cases
| Use Case | Description | Actors |
|---|---|---|
|Register patient | Creates a new patient record containing personal information as well as insurance information. | Administrative Staff | 
|Book appointment | Schedule an appointment while checking the availability of healthcare professionals to avoid any scheduling conflicts.| Adminstrative Staff, Patient* | 
| Cancel an Appointment | Cancels an existing appointment. | Administrative Staff, Patient |
| Reschedule an Appointment | Change an appointment to another available date without scheduling conflicts | Administrative Staff, Patient |
| Referral creation | Creates a consultation request for a patient to see a specialist | Healthcare Professional |
| Record Consultation | Write down observations, diagnoses, treatments, and follow-up instructions | Healthcare Professional |
| Issue Prescription | Creates a prescription for one or more medications when needed. | Healthcare Professional |
| Create Invoice | A record of medical service costs that need to be paid | Administrative Staff |
| Record Payment | Tracks payments received for medical services | Administrative Staff |
* *Patient is a potential direct system actor.


## Non-Critical Use Cases

| Use Case | Description | Actor |
|---|---|---|
| Search for Patient | Searches for an existing patient using their insurance number, date of birth, phone number, or name| Administrative Staff |
| Register Patient Arrival | Records a patient's arrival at the clinic for their appointment. | Administrative Staff |
| Confirm Appointment | Confirms a requested appointment. | Administrative Staff |




## Assumptions and Notes

- Patient participation in appointment-related use cases is described in the requirements but not specificied if patients interact directly with the system or submits requets through administrative staff. 
- For the initial use-case model, direct Patient access remains to be confirmed with the team, so for now patient is a potential actor. 
- Initial requirements will be reviewed by team
- Additional critical and non-critical use cases may be created by team members 
- Exisiting use cases may be modified by team members

