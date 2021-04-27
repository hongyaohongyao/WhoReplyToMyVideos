package xyz.hyhy.wrtmv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"rpid"})
public class Reply implements Comparable<Reply> {
    private long rpid;
    private long oid; //回复区号？
    private long mid; //谁回复的
    private String content;
    private long parent;
    private long ctime;

    @Override
    public int compareTo(Reply o) {
        return Long.compare(rpid, o.rpid);
    }
}
