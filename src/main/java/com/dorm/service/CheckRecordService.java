package com.dorm.service;

import com.dorm.dao.CheckRecordDao;
import com.dorm.entity.CheckRecord;

import java.util.List;

public class CheckRecordService {
    private final CheckRecordDao checkRecordDao = new CheckRecordDao();

    public List<CheckRecord> findByStudentId(int studentId) {
        return checkRecordDao.findByStudentId(studentId);
    }

    public List<CheckRecord> latest(Integer adminId, int size) {
        return checkRecordDao.latest(adminId, size);
    }
}
