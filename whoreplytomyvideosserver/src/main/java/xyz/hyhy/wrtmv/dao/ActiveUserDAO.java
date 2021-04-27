package xyz.hyhy.wrtmv.dao;

import xyz.hyhy.wrtmv.entity.result.ActiveUser;

import java.util.List;

public interface ActiveUserDAO {
    String BY_REPLY_TIMES = "rank_r";
    String BY_REPLY_VIDEOS = "rank_v";
    String BY_FIRST_TIME = "rank_f";
    String BY_LAST_TIME = "rank_l";
    String BY_REPLY_TIMES_SEX = "rank_r_by_sex";
    String BY_REPLY_VIDEOS_SEX = "rank_v_by_sex";
    String BY_FIRST_TIME_SEX = "rank_f_by_sex";
    String BY_LAST_TIME_SEX = "rank_l_by_sex";

    void analysis(long owner);

    List<ActiveUser> getUsersByRank(long owner, long start, long end, String by);
}
