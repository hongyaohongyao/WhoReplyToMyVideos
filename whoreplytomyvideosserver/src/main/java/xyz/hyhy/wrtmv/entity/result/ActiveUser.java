package xyz.hyhy.wrtmv.entity.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveUser {

    private long owner;
    private long mid;
    private String name;
    private String face;
    private String sex;
    private int level;
    private long firstTime;
    private long lastTime;
    private long timesR;
    private long timesV;
    private long rankR;
    private long rankV;
    private long rankF;
    private long rankL;
    private long rankRBySex;
    private long rankVBySex;
    private long rankFBySex;
    private long rankLBySex;

}
