package model;

import model.enums.LeaveStatus;

import java.time.LocalDateTime;

public class ApprovalMessage {

    private int id;
    private int leaveRequestId;
    private int approverId;
    private String title;
    private String content;
    private LocalDateTime approvalTime;
    private LeaveStatus decisionMade;

    public ApprovalMessage() {}

    public ApprovalMessage(int leaveRequestId, int approverId,
                           String title, String content, LeaveStatus decisionMade) {
        this.leaveRequestId = leaveRequestId;
        this.approverId = approverId;
        setTitle(title);
        setContent(content);
        setDecisionMade(decisionMade);
        this.approvalTime = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLeaveRequestId() { return leaveRequestId; }
    public void setLeaveRequestId(int leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public int getApproverId() { return approverId; }
    public void setApproverId(int approverId) {
        this.approverId = approverId;
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

    public LocalDateTime getApprovalTime() { return approvalTime; }
    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public LeaveStatus getDecisionMade() { return decisionMade; }
    public void setDecisionMade(LeaveStatus decisionMade) {
        if (decisionMade == LeaveStatus.PENDING)
            throw new IllegalArgumentException("An approval message must carry a final decision.");
        this.decisionMade = decisionMade;
    }

    @Override
    public String toString() {
        return String.format("[ApprovalMessage #%d] Request #%d | Decision: %s | \"%s\"",
                id, leaveRequestId, decisionMade, title);
    }
}
