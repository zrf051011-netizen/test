package com.dorm.entity;

import java.util.Date;

public class CheckRecord {
    private Integer id;
    private Integer studentId;
    private Integer fromDormitoryId;
    private Integer toDormitoryId;
    private String type;
    private Date checkDate;
    private Integer operatorId;
    private String remark;
    private Date createTime;
    private String studentName;
    private String operatorName;
    private String fromRoom;
    private String toRoom;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getFromDormitoryId() {
        return fromDormitoryId;
    }

    public void setFromDormitoryId(Integer fromDormitoryId) {
        this.fromDormitoryId = fromDormitoryId;
    }

    public Integer getToDormitoryId() {
        return toDormitoryId;
    }

    public void setToDormitoryId(Integer toDormitoryId) {
        this.toDormitoryId = toDormitoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getFromRoom() {
        return fromRoom;
    }

    public void setFromRoom(String fromRoom) {
        this.fromRoom = fromRoom;
    }

    public String getToRoom() {
        return toRoom;
    }

    public void setToRoom(String toRoom) {
        this.toRoom = toRoom;
    }

    public String getTypeName() {
        if ("OUT".equals(type)) {
            return "退宿";
        }
        if ("CHANGE".equals(type)) {
            return "换宿";
        }
        return "入住";
    }
}
