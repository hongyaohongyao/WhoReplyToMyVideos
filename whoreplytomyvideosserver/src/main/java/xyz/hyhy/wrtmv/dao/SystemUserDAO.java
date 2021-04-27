package xyz.hyhy.wrtmv.dao;

import xyz.hyhy.wrtmv.entity.SystemUser;

import java.util.List;

public interface SystemUserDAO {
    boolean exist(long uid);

    void saveSystemUser(SystemUser user);

    SystemUser getSystemUser(long uid);

    List<SystemUser> getSystemUsers();
}
