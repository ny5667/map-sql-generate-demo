package com.example.demo1.vo;

public class AdminInfo {

    public String ADMIN_COMPANY_ID;

    public String ADMIN_DEPARTMENT_ID;

    public String ADMIN_POSITION_ID;

    public String ADMIN_STAFF_ID;

    public AdminInfo()
    {
        this.ADMIN_COMPANY_ID = "${ADMIN_COMPANY_ID}";
        this.ADMIN_DEPARTMENT_ID = "${ADMIN_DEPARTMENT_ID}";
        this.ADMIN_POSITION_ID = "${ADMIN_POSITION_ID}";
        this.ADMIN_STAFF_ID = "${ADMIN_STAFF_ID}";
    }

    public AdminInfo(String ADMIN_COMPANY_ID, String ADMIN_DEPARTMENT_ID, String ADMIN_POSITION_ID, String ADMIN_STAFF_ID) {
        this.ADMIN_COMPANY_ID = ADMIN_COMPANY_ID;
        this.ADMIN_DEPARTMENT_ID = ADMIN_DEPARTMENT_ID;
        this.ADMIN_POSITION_ID = ADMIN_POSITION_ID;
        this.ADMIN_STAFF_ID = ADMIN_STAFF_ID;
    }

    public String getADMIN_COMPANY_ID() {
        return ADMIN_COMPANY_ID;
    }

    public void setADMIN_COMPANY_ID(String ADMIN_COMPANY_ID) {
        this.ADMIN_COMPANY_ID = ADMIN_COMPANY_ID;
    }

    public String getADMIN_DEPARTMENT_ID() {
        return ADMIN_DEPARTMENT_ID;
    }

    public void setADMIN_DEPARTMENT_ID(String ADMIN_DEPARTMENT_ID) {
        this.ADMIN_DEPARTMENT_ID = ADMIN_DEPARTMENT_ID;
    }

    public String getADMIN_POSITION_ID() {
        return ADMIN_POSITION_ID;
    }

    public void setADMIN_POSITION_ID(String ADMIN_POSITION_ID) {
        this.ADMIN_POSITION_ID = ADMIN_POSITION_ID;
    }

    public String getADMIN_STAFF_ID() {
        return ADMIN_STAFF_ID;
    }

    public void setADMIN_STAFF_ID(String ADMIN_STAFF_ID) {
        this.ADMIN_STAFF_ID = ADMIN_STAFF_ID;
    }
}
