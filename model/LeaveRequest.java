package model;

import model.enums.LeaveStatus;
import model.enums.RequestType;

import java.time.LocalDateTime;

public class LeaveRequest {

    private int id;
    private int employeeId;
    private RequestType requestType;
    private String title;
    private String content;
    private LocalDateTime leaveStart;
    private LocalDateTime leaveEnd;
    private String supportingDocumentPath;
    private LocalDateTime submissionTime;
    private LeaveStatus status;

    public LeaveRequest() {}

    public LeaveRequest(int employeeId, RequestType requestType, String title,
                        String content, LocalDateTime leaveStart, LocalDateTime leaveEnd,
                        String supportingDocumentPath) {
        this.employeeId = employeeId;
        setRequestType(requestType);
        setTitle(title);
        setContent(content);
        setLeaveStart(leaveStart);
        setLeaveEnd(leaveEnd);
        this.supportingDocumentPath = supportingDocumentPath;
        this.submissionTime = LocalDateTime.now();
        this.status = LeaveStatus.PENDING;
    }

    public boolean isPending() {
        return status == LeaveStatus.PENDING;
    }

    public long getDurationDays() {
        if (leaveStart == null || leaveEnd == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(leaveStart.toLocalDate(), leaveEnd.toLocalDate());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) {
        if (requestType == null)
            throw new IllegalArgumentException("Request type must not be null.");
        this.requestType = requestType;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title must not be empty.");
        this.title = title;
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("Content must not be empty.");
        this.content = content;
    }

    public LocalDateTime getLeaveStart() { return leaveStart; }
    public void setLeaveStart(LocalDateTime leaveStart) {
        if (leaveStart == null)
            throw new IllegalArgumentException("Leave start date must not be null.");
        this.leaveStart = leaveStart;
    }

    public LocalDateTime getLeaveEnd() { return leaveEnd; }
    public void setLeaveEnd(LocalDateTime leaveEnd) {
        if (leaveEnd == null)
            throw new IllegalArgumentException("Leave end date must not be null.");
        if (this.leaveStart != null && leaveEnd.isBefore(this.leaveStart))
            throw new IllegalArgumentException("Leave end date must be after start date.");
        this.leaveEnd = leaveEnd;
    }

    public String getSupportingDocumentPath() { return supportingDocumentPath; }
    public void setSupportingDocumentPath(String supportingDocumentPath) {
        this.supportingDocumentPath = supportingDocumentPath;
    }

    public LocalDateTime getSubmissionTime() { return submissionTime; }
    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) {
        if (status == null)
            throw new IllegalArgumentException("Status must not be null.");
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[LeaveRequest #%d] \"%s\" | %s | %s → %s | Status: %s",
                id, title, requestType, leaveStart, leaveEnd, status);
    }
}
