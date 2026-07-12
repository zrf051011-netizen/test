package com.dorm.service;

import com.dorm.dao.DormitoryDao;
import com.dorm.entity.Dormitory;
import com.dorm.exception.BusinessException;
import com.dorm.util.PageBean;

import java.util.List;

public class DormitoryService {
    private final DormitoryDao dormitoryDao = new DormitoryDao();

    public PageBean<Dormitory> findPage(String keyword, Integer buildingId, String status, Integer adminId, int page, int size) {
        return dormitoryDao.findPage(keyword, buildingId, status, adminId, page, size);
    }

    public List<Dormitory> findList(String keyword, Integer buildingId, String status, Integer adminId) {
        return dormitoryDao.findList(keyword, buildingId, status, adminId);
    }

    public List<Dormitory> findAvailable(Integer adminId) {
        return dormitoryDao.findAvailable(adminId);
    }

    public Dormitory findById(int id) {
        return dormitoryDao.findById(id);
    }

    public void save(Dormitory dormitory) {
        if (dormitory.getBuildingId() == null || dormitory.getBuildingId() <= 0) {
            throw new BusinessException("请选择楼栋");
        }
        if (dormitory.getRoomNumber() == null || dormitory.getRoomNumber().trim().isEmpty()) {
            throw new BusinessException("请输入房间号");
        }
        if (dormitory.getCapacity() == null || dormitory.getCapacity() <= 0) {
            throw new BusinessException("床位数必须大于0");
        }
        if (dormitory.getId() == null || dormitory.getId() <= 0) {
            dormitory.setCurrentCount(0);
            dormitory.setStatus(dormitory.getStatus() == null || dormitory.getStatus().isEmpty() ? "AVAILABLE" : dormitory.getStatus());
            dormitoryDao.save(dormitory);
            return;
        }
        Dormitory old = dormitoryDao.findById(dormitory.getId());
        if (old == null) {
            throw new BusinessException("宿舍不存在");
        }
        if (old.getCurrentCount() != null && old.getCurrentCount() > dormitory.getCapacity()) {
            throw new BusinessException("床位数不能小于当前入住人数");
        }
        dormitoryDao.updateDormitory(dormitory);
    }

    public void delete(int id) {
        Dormitory dormitory = dormitoryDao.findById(id);
        if (dormitory == null) {
            throw new BusinessException("宿舍不存在");
        }
        if (dormitory.getCurrentCount() != null && dormitory.getCurrentCount() > 0) {
            throw new BusinessException("宿舍已有学生入住，不能删除");
        }
        dormitoryDao.deleteById(id);
    }
}
