A simple architecture for the application outlined
(not following any real syntax, just for visualisation):

{
    Admin:
    "login" = String,
    "password" = String
}

{
    User (parent class for Employee and Approver, no real usage):
    "fullName" = String,
    "login" = String,
    "password" = String,
    "email" = email,
    "position" = ChoiceField (Middle employee, Senior employee)
}

{
    Employee extends User:
    "department" = ChoiceField (IT, Finance, HR, Management),
    "position" = ChoiceField (added Intern, Junior employee)
}

{
    Approver extends User:
}

{
    leaveRequest:
    "employee" = ForeignKey,
    "requestType" = ChoiceField,
    "title" = String,
    "content" = String,
    "leaveStart" = datetime,
    "leaveEnd" = datetime,
    "supportingDocument" = file (pdf, docx or jpeg/img),
    "submissionTime" = datetime,
    "status" = ChoiceField (by default - pending, then - approved or denied)
}

{
    approvalMessage:
    "leaveRequest" = ForeignKey,
    "approver" = ForeignKey,
    "title" = String,
    "content" = String,
    "approvalTime" = datetime
}
