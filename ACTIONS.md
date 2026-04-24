A list of expected actions for each type of user:



Employee:

createLeaveRequest - create a LeaveRequest through a form.

listLeaveRequest - list all LeaveRequest created by this Employee. Only show title and LeaveStatus.

viewLeaveRequest - view individual LeaveRequest with full info displayed. There must be an option to immediately jump to viewApprovalMessage of this particular LeaveRequest, if not "pending".

deleteLeaveRequest - an option inside viewLeaveRequest.

viewApprovalMessage - an option inside viewLeaveRequest.



Approver:

listLeaveRequest - list all LeaveRequest created by every employee junior to the current Approver.

viewLeaveRequest - view individual LeaveRequest. There must be an option to immediately jump to createApprovalMessage if "pending", else - viewApprovalMessage or deleteApprovalMessage.

createApprovalMessage - create an ApprovalMessage through a form.

viewApprovalMessage - an option inside viewLeaveRequest.

deleteApprovalMessage - an option inside viewLeaveRequest. Only available for Approver or Admin. If ApprovalMessage is deleted, make the corresponding changes.



Admin:

every action available for Employee and Approver.

every CRUD operation for every class.
