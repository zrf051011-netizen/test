package com.dorm.entity;

import java.util.Date;

public class Dormitory {
    private Integer id;
    private Integer buildingId;
    private String roomNumber;
    private Integer floor;
    private Integer capacity;
    private Integer currentCount;
    private String status;
    private Date createTime;
    private Date updateTime;
    private String buildingName;
    private String adminName;
    private Integer buildingAdminId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getBuildingAdminId() {
        return buildingAdminId;
    }

    public void setBuildingAdminId(Integer buildingAdminId) {
        this.buildingAdminId = buildingAdminId;
    }

    public int getVacancy() {
        int total = capacity == null ? 0 : capacity;
        int used = currentCount == null ? 0 : currentCount;
        return Math.max(total - used, 0);
    }

    public int getUseRate() {
        if (capacity == null || capacity == 0 || currentCount == null) {
            return 0;
        }
        return (int) Math.round(currentCount * 100.0 / capacity);
    }

    public String getStatusName() {
        if ("FULL".equals(status)) {
            return "已满";
        }
        if ("REPAIR".equals(status)) {
            return "维修中";
        }
        return "可入住";
    }
}
