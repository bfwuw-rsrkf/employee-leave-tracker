### Project Documentation: Employee Leave Tracker

**1. Project Overview and Technology Stack**
The **Employee Leave Tracker** is a PC-based application designed for an **IT consulting company** to manage vacation, sick leave, and other time-off requests. The application is built using **Java (8u461 and JDK-25)** and features a graphical user interface powered by **JavaFX**. It utilizes a local **SQLite** database for data storage, with database interactions managed through **SQLiteJDBC**. Key functional features include a secure log-in and sign-up system, which incorporates **auto-emailing** for tasks such as email confirmation during registration.

**2. System Architecture and Data Entities**
The application’s architecture is defined by a tiered user hierarchy and specific data entities for managing requests:

*   **User Roles and Hierarchy:**
    *   **User (Parent Class):** This is the base class for Employees and Approvers, containing common attributes such as full name, login, password, email, and position (specifically **Middle** or **Senior employee**).
    *   **Employee:** Inherits from the User class and adds a **department** field (IT, Finance, HR, or Management) and specific roles like **Intern** or **Junior employee**.
    *   **Approver:** Also extends the User class and is responsible for managing staff junior to them.
    *   **Admin:** A standalone class for administrators with login and password credentials, possessing full control over the application.
*   **Core Entities:**
    *   **LeaveRequest:** This entity tracks the requesting employee, the type of request, title, content, and the start/end dates of the leave. It also includes a submission timestamp and a status field (defaulting to **pending**, then **approved** or **denied**). The system supports attachments for requests in **PDF, DOCX, or JPEG/IMG** formats.
    *   **ApprovalMessage:** This links to a specific LeaveRequest and stores the identifying Approver, the message title, its content, and the time the approval was granted.

**3. User Actions and Functional Requirements**
System permissions are strictly defined based on the user's role:

*   **Employee Actions:** Employees can **create leave requests** via a form and **list** their existing requests, though the list view is restricted to showing only the title and status. They can view individual requests with full details and have an option to jump directly to an **approval message** if the request is no longer "pending". Employees also have the permission to **delete** their own requests.
*   **Approver Actions:** Approvers can **list and view** every leave request submitted by employees junior to them. They can create **approval messages**, which is the required method for changing a request's status; they also have the authority to view or delete these messages. If an approval message is deleted, the system is designed to make corresponding changes to the request.
*   **Admin Actions:** Administrators have access to **every action** available to both Employees and Approvers. They maintain full administrative control, allowing them to perform **CRUD (Create, Read, Update, Delete)** operations for every class and entity within the application.

Presentation link:
https://drive.google.com/file/d/10aPlu6JDYnO--MRP4ICrEBsJTbrPuCVL/view?usp=sharing
