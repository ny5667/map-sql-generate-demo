package com.example.demo1.vo;

public class AdminInfo {

    private String adminCompanyId;

    private String adminDepartmentId;

    private String adminPositionId;

    private String adminStaffId;

    public AdminInfo()
    {
        this.adminCompanyId = "${ADMIN_COMPANY_ID}";
        this.adminDepartmentId = "${ADMIN_DEPARTMENT_ID}";
        this.adminPositionId = "${ADMIN_POSITION_ID}";
        this.adminStaffId = "${ADMIN_STAFF_ID}";
    }

    public AdminInfo(String adminCompanyId, String adminDepartmentId, String adminPositionId, String adminStaffId) {
        this.adminCompanyId = adminCompanyId;
        this.adminDepartmentId = adminDepartmentId;
        this.adminPositionId = adminPositionId;
        this.adminStaffId = adminStaffId;
    }

    public String getAdminCompanyId() {
        return adminCompanyId;
    }

    public void setAdminCompanyId(String adminCompanyId) {
        this.adminCompanyId = adminCompanyId;
    }

    public String getAdminDepartmentId() {
        return adminDepartmentId;
    }

    public void setAdminDepartmentId(String adminDepartmentId) {
        this.adminDepartmentId = adminDepartmentId;
    }

    public String getAdminPositionId() {
        return adminPositionId;
    }

    public void setAdminPositionId(String adminPositionId) {
        this.adminPositionId = adminPositionId;
    }

    public String getAdminStaffId() {
        return adminStaffId;
    }

    public void setAdminStaffId(String adminStaffId) {
        this.adminStaffId = adminStaffId;
    }
}
