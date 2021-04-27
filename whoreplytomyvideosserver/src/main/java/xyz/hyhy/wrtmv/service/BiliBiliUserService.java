package xyz.hyhy.wrtmv.service;


import xyz.hyhy.wrtmv.entity.BiliBiliUser;

/**
 *
 */
public interface BiliBiliUserService {

    /**
     * 根据用户id查询用户信息，没有的时候会自动爬取用户信息
     *
     * @param mid
     * @return 用户
     */
    BiliBiliUser queryUser(long mid);


}
