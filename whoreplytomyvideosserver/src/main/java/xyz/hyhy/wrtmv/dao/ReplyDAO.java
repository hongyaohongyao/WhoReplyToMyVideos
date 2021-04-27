package xyz.hyhy.wrtmv.dao;

import xyz.hyhy.wrtmv.entity.Reply;

import java.util.List;
import java.util.Set;

public interface ReplyDAO {

    void saveReply(Reply reply);

    void removeReply(long rpid);

    Reply getReply(long rpid);

    List<Reply> getReplyByOID(long oid);

    void saveReplies(Set<Reply> replies);
}
