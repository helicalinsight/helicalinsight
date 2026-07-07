package com.helicalinsight.admin.management;

public class EFWReportVO implements Comparable<EFWReportVO> {
    private String reportPath;
    private long lastModified;
    private String title;
    private String dir;
    private String file;
    private String logicalPath;

    @Override
    public String toString() {
        return "EFWReportVO{" +
                "reportPath='" + reportPath + '\'' +
                ", lastModified=" + lastModified +
                ", title='" + title + '\'' +
                ", dir='" + dir + '\'' +
                ", file='" + file + '\'' +
                ", logicalPath='" + logicalPath + '\'' +
                '}';
    }

    public EFWReportVO(String reportPath, long lastModified, String title, String dir, String file, String logicalPath) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
        this.title = title;
        this.dir = dir;
        this.file = file;
        this.logicalPath = logicalPath;
    }

    public String getLogicalPath() {
        return logicalPath;
    }

    public void setLogicalPath(String logicalPath) {
        this.logicalPath = logicalPath;
    }

    public EFWReportVO(String reportPath, long lastModified, String title, String dir, String file) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
        this.title = title;
        this.dir = dir;
        this.file = file;
    }

    public EFWReportVO(String reportPath, long lastModified, String title) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
        this.title = title;
    }

    public EFWReportVO(String reportPath, long lastModified) {
        this.reportPath = reportPath;
        this.lastModified = lastModified;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int compareTo(EFWReportVO efwReportVO) {
        long compareQuantity = efwReportVO.getLastModified();

        //ascending order
        // return this.lastModified - compareQuantity;

        //descending order
        if(compareQuantity==this.lastModified){
            return 0;
        }else if((compareQuantity -this.lastModified)>0){
            return 1;
        }else if((compareQuantity -this.lastModified)<0){
            return -1;
        };
        return 0;
    }
}