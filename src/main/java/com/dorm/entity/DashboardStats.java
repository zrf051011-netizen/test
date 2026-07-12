package com.dorm.entity;

public class DashboardStats {
    private Integer userCount;
    private Integer studentCount;
    private Integer buildingCount;
    private Integer dormitoryCount;
    private Integer bedCount;
    private Integer usedBedCount;
    private Integer repairPendingCount;
    private Integer repairDoingCount;

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getBuildingCount() {
        return buildingCount;
    }

    public void setBuildingCount(Integer buildingCount) {
        this.buildingCount = buildingCount;
    }

    public Integer getDormitoryCount() {
        return dormitoryCount;
    }

    public void setDormitoryCount(Integer dormitoryCount) {
        this.dormitoryCount = dormitoryCount;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public Integer getUsedBedCount() {
        return usedBedCount;
    }

    public void setUsedBedCount(Integer usedBedCount) {
        this.usedBedCount = usedBedCount;
    }

    public Integer getRepairPendingCount() {
        return repairPendingCount;
    }

    public void setRepairPendingCount(Integer repairPendingCount) {
        this.repairPendingCount = repairPendingCount;
    }

    public Integer getRepairDoingCount() {
        return repairDoingCount;
    }

    public void setRepairDoingCount(Integer repairDoingCount) {
        this.repairDoingCount = repairDoingCount;
    }

    public int getOccupancyRate() {
        if (bedCount == null || bedCount == 0 || usedBedCount == null) {
            return 0;
        }
        return (int) Math.round(usedBedCount * 100.0 / bedCount);
    }
}
