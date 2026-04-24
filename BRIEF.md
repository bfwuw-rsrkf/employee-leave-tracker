Employee Leave Tracker is gonna be a PC application with usage of the following tools:
1) Java 8u461
2) jdk-25
3) SQLite
4) SQLiteJDBC
5) JavaFX
The application tracks employee leave requests and approvals, including vacation, sick leave, and other time-off requests. The company is an IT consulting company.

Expected features:
1) Log-in and sign-up
2) Auto emailing
3) Simple GUI using JavaFX
4) Local database created using SQLite
5) Interactions with database using SQLiteJDBC

What should users be able to do:
1) Log-in of employees, approvers and admin.
2) Sign-up of employees, confirmation of email using auto emailing.
3) Employee must be able to create leaveRequest, view what leaveRequest's instances they created and delete them.
4) Approver must be able to list every leaveRequest of every employee that's junior to them. They may change leaveRequest status, but only when following up with approvalMessage.
5) Admin must have every CRUD operation available for every class, i.e. full control over the application.
