package xyz.hyhy.wrtmv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"bvid"})
public class VideoInfo implements Comparable<VideoInfo> {
    private String bvid;
    private long oid; //评论区的id
    private long mid;
    private String title;
    private String description;
    private long created;

    @Override
    public int compareTo(VideoInfo o) {
        return bvid.compareTo(o.bvid);
    }
}
