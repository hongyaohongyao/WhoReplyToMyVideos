package xyz.hyhy.wrtmv.dao;

import xyz.hyhy.wrtmv.entity.VideoInfo;

import java.util.List;
import java.util.Set;

public interface VideoInfoDAO {

    void saveVideo(VideoInfo v);

    void saveVideos(Set<VideoInfo> vSet);

    VideoInfo getVideoInfo(String bvid);

    List<VideoInfo> getVideosByUID(long uid);
}
