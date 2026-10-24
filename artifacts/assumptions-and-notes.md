# SSD Assumptions and Notes

Assumptions and notes are grouped by contributor and SSD.

Note that all SSD assume that the actor is already logged in.

## Sia

### Record Consultation

[View SSD PNG](SSDs/RecordConsultation.png)

- We assume healthcare professionals must select an existing appointment before recording a consultation.
- We assume the system checks whether the healthcare professional is authorized to access the selected appointment.
- We assume invalid consultation details can be corrected and submitted again.

### Issue Prescription

[View SSD PNG](SSDs/IssuePrescription.png)

- We assume healthcare professionals can start a prescription from an existing consultation.
- We assume medications are added individually, and the prescription is only issued once the healthcare professional finishes adding medications.
- We assume the system validates medication details before adding them to the prescription.

### Manage Healthcare Professional

[View SSD PNG](SSDs/ManageHealthcareProfessional.png)

- We assume administrative staff are responsible for registering and updating healthcare professional information.
- We assume registering and updating healthcare professionals can be handled within one use case, and administrative staff can choose which action to perform.
- We assume medical specialties and clinic locations already exist in the system and can be assigned to healthcare professionals.

### Request Follow-Up Appointment

[View SSD PNG](SSDs/RequestFollowUp.png)

- We assume healthcare professionals request follow-up appointments through an existing consultation.
- We assume submitting a follow-up request does not automatically schedule an appointment.
- We assume the system validates follow-up requirements and allows invalid requests to be corrected.

## Huzaifa

Draft modelling assumptions for the four SSDs below.

### Shared Assumptions

- The requester's identity and role are already known; login is outside these SSDs.
- Each attempt submits all necessary inputs in one system operation.
- Failed validation returns an error without creating or modifying the patient/appointment record. Activity logging can still occur.
- After a failure, the requester can correct the information and retry, or stop.

### Book Appointment

[View SSD PNG](https://github.com/zaifnatra/SOEN342/blob/c4b0b74dd05a6b7e21ca1e9fd9a6000beb3aa345/artifacts/SSDs/BookAppointment.png)

- The requester supplies the patient, professional, clinic, service, requested date/time, and referral when required in one request.
- Selecting these details happens before the illustrated request; no separate search/select operations are shown.
- The system checks patient existence and authorization during booking, so these can produce failure responses.

### Register Patient

[View SSD PNG](https://github.com/zaifnatra/SOEN342/blob/c4b0b74dd05a6b7e21ca1e9fd9a6000beb3aa345/artifacts/SSDs/RegisterPatient.png)

- The administrative staff member already has permission to register patients.
- The system generates the unique patientID.
- Personal and available insurance information are submitted together.
- Exact required personal fields and validation rules still need team agreement.

### Cancel Appointment

[View SSD PNG](https://github.com/zaifnatra/SOEN342/blob/c4b0b74dd05a6b7e21ca1e9fd9a6000beb3aa345/artifacts/SSDs/CancelAppointment.png)

- The requester already knows which appointment to cancel and supplies its appointmentID.
- Finding/selecting the appointment happens before the illustrated request.
- The system checks that the appointment exists and the requester is allowed to cancel it.

### Reschedule Appointment

[View SSD PNG](https://github.com/zaifnatra/SOEN342/blob/c4b0b74dd05a6b7e21ca1e9fd9a6000beb3aa345/artifacts/SSDs/RescheduleAppointment.png)

- Rescheduling changes only the date/time of the existing appointment.
- The patient, professional, clinic, and service remain the same.
- The requester supplies the appointmentID and proposed new date/time together.
- A failed request preserves the original date/time.
- We have not specified whether rescheduling changes the appointment's status.

### Established Requirements (Not Assumptions)

- Patients or administrative staff can request, cancel, and reschedule appointments.
- Booking checks availability, scheduling constraints, and any required referral, then automatically confirms successful bookings.
- referralID may be omitted when the service does not require a referral.
- Insurance information is recorded when available.
- Cancellation changes the appointment status to cancelled.
- Access is restricted by role, and relevant activity is recorded.

## Maëlys

### Manage Professional Schedule

[View SSD PNG](SSDs/ManageProfessionalSchedule.png)

- We assume the modifications made to a healthcare professional schedule are Add and Delete (modify is Delete, then Add).
- We assume the system is the one validating the addition of a new schedule.

### Create Referral

[View SSD PNG](SSDs/CreateReferral.png)

- We assume that professionalId comes from the healthcare professional loggin.
- We assume that the healthcare professional knows the patientId.
- referredTarget is to designate the referred specialty or professional.
