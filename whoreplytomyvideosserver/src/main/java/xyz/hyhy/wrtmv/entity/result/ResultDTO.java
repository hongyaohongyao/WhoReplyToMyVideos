package xyz.hyhy.wrtmv.entity.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultDTO {
    private int code;
    private String msg;
    private Object data;

    public static ResultDTO success(Object data) {
        ResultDTO result = new ResultDTO();
        result.setCode(200);
        result.setMsg("ok");
        result.setData(data);
        return result;
    }

    public static ResultDTO accept(String msg) {
        ResultDTO result = new ResultDTO();
        result.setCode(202);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public static ResultDTO noResult() {
        ResultDTO result = new ResultDTO();
        result.setCode(404);
        result.setMsg("什么都没有");
        result.setData(null);
        return result;
    }

}
