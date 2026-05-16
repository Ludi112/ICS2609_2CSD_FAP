
package Model;

import java.sql.Timestamp;

public class ReportHistory {
    private int reportId;
    private String generatedBy;
    private String reportType;
    private Timestamp generatedDate;
    private String filePath;

    public ReportHistory(int reportId, String generatedBy, String reportType, Timestamp generatedDate, String filePath) {
        this.reportId = reportId;
        this.generatedBy = generatedBy;
        this.reportType = reportType;
        this.generatedDate = generatedDate;
        this.filePath = filePath;
    }

    public int getReportId() { return reportId; }
    public String getGeneratedBy() { return generatedBy; }
    public String getReportType() { return reportType; }
    public Timestamp getGeneratedDate() { return generatedDate; }
    public String getFilePath() { return filePath; }

    public void setReportId(int reportId) { this.reportId = reportId; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public void setGeneratedDate(Timestamp generatedDate) { this.generatedDate = generatedDate; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
