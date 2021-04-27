package xyz.hyhy.wrtmv.dao;

import xyz.hyhy.wrtmv.entity.BiliBiliUser;

import java.util.Set;

public interface BiliBiliUserDAO {

    boolean existUser(long uid);

    void saveUserAsUser(BiliBiliUser user);

    /**
     * 只保存信息
     *
     * @param user
     */
    void saveUser(BiliBiliUser user);

    BiliBiliUser getUser(long id);

    void saveUsers(Set<BiliBiliUser> users);

}
