package com.dorm.entity;

import java.util.Date;

public class Building {
    private Integer id;
    private String buildingName;
    private Integer floorCount;
    private String address;
    private Integer buildingAdminId;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String adminName;
    private Integer roomCount;
    private Integer bedCount;
    private Integer usedCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Integer getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(Integer floorCount) {
        this.floorCount = floorCount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getBuildingAdminId() {
        return buildingAdminId;
    }

    public void setBuildingAdminId(Integer buildingAdminId) {
        this.buildingAdminId = buildingAdminId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(Integer roomCount) {
        this.roomCount = roomCount;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public int getOccupancyRate() {
        if (bedCount == null || bedCount == 0 || usedCount == null) {
            return 0;
        }
        return (int) Math.round(usedCount * 100.0 / bedCount);
    }
}
