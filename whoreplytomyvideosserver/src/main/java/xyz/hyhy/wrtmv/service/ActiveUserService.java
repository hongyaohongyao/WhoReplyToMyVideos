package xyz.hyhy.wrtmv.service;

import xyz.hyhy.wrtmv.entity.result.ActiveUser;

import java.util.List;

public interface ActiveUserService {
    /**
     * @return 新加入的用户
     */
    List<ActiveUser> getNewVisitor(long owner, long start, long end);

    /**
     * @return 最近活动的用户
     */
    List<ActiveUser> getLastActiveUser(long owner, long start, long end);

    /**
     * @return 评论最多的用户
     */
    List<ActiveUser> getMostReplies(long owner, long start, long end);

    /**
     * @return 评论视频最多的用户
     */
    List<ActiveUser> getMostVideos(long owner, long start, long end);


}
