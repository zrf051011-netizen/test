package com.dorm.service;

import com.dorm.dao.BuildingDao;
import com.dorm.dao.UserDao;
import com.dorm.entity.Building;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.util.PageBean;

import java.util.List;

public class BuildingService {
    private final BuildingDao buildingDao = new BuildingDao();
    private final UserDao userDao = new UserDao();

    public PageBean<Building> findPage(String keyword, int page, int size) {
        return buildingDao.findPage(keyword, page, size);
    }

    public List<Building> findAllActive() {
        return buildingDao.findAllActive();
    }

    public List<Building> findByAdminId(int adminId) {
        return buildingDao.findByAdminId(adminId);
    }

    public List<Building> findStats(Integer adminId) {
        return buildingDao.findStats(adminId);
    }

    public Building findById(int id) {
        return buildingDao.findById(id);
    }

    public List<User> findBuildingAdmins() {
        return userDao.findByRole("BUILDING_ADMIN");
    }

    public void save(Building building) {
        if (building.getBuildingName() == null || building.getBuildingName().trim().isEmpty()) {
            throw new BusinessException("请输入楼栋名称");
        }
        building.setStatus(building.getStatus() == null ? 1 : building.getStatus());
        building.setFloorCount(building.getFloorCount() == null ? 6 : building.getFloorCount());
        if (building.getId() == null || building.getId() <= 0) {
            buildingDao.save(building);
        } else {
            buildingDao.updateBuilding(building);
        }
    }

    public void delete(int id) {
        Building building = buildingDao.findById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        buildingDao.deleteById(id);
    }
}
