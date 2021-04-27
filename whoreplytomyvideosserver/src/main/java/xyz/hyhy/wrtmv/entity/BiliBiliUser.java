package xyz.hyhy.wrtmv.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"uid"})
public class BiliBiliUser implements Comparable<BiliBiliUser> {
    private long uid;
    private int level;
    private String name;
    private String face;
    private String sex;
    private String sign;

    @Override
    public int compareTo(BiliBiliUser o) {
        return Long.compare(uid, o.uid);
    }
}
