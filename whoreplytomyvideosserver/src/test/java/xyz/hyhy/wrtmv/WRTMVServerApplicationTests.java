package xyz.hyhy.wrtmv;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.hadoop.hive.HiveClient;
import xyz.hyhy.wrtmv.dao.BiliBiliUserDAO;
import xyz.hyhy.wrtmv.entity.BiliBiliUser;
import xyz.hyhy.wrtmv.process.BaseCrawlProcess;
import xyz.hyhy.wrtmv.process.ReplyPhaseProcess;
import xyz.hyhy.wrtmv.process.VideoInfoPhaseProcess;
import xyz.hyhy.wrtmv.utils.spider.addition.SpiderAdditionTools;

import javax.annotation.Resource;
import java.sql.*;

@SpringBootTest
class WRTMVServerApplicationTests {

    @Resource
    private HiveClient hiveClient;

    @Test
    void contextLoads() throws SQLException {
        hiveClient.executeAndfetchOne("");
        Connection conn = hiveClient.getConnection();
        Statement ps = conn.createStatement();

        ResultSet resultSet = ps.executeQuery("select title, rating_times from\n" +
                "  (select movie_id,count(1) as rating_times\n" +
                "  from ratings\n" +
                "  group by movie_id\n" +
                "  order by rating_times desc limit 10) as t\n" +
                "  join movies on movies.movie_id = t.movie_id");
        while (resultSet.next()) {
            System.out.println(resultSet.getInt("title") + " " + resultSet.getString("rating_times"));
        }
    }

    @Autowired
    private BiliBiliUserDAO biliBiliUserDAO;

    @Test
    public void testBiliBiliUserDAO() {
        BiliBiliUser user = new BiliBiliUser();
        user.setUid(1);
        user.setLevel(3);
        user.setName("unknown");
        user.setSign("hahaha");
        user.setFace("no url");
        user.setSex("保密");
        biliBiliUserDAO.saveUser(user);
    }

    @Test
    public void testReplyDAO() {
        BiliBiliUser user = new BiliBiliUser();
        user.setUid(1);
        user.setLevel(3);
        user.setName("unknown");
        user.setSign("hahaha");
        user.setFace("no url");
        user.setSex("保密");
        biliBiliUserDAO.saveUser(user);
    }

}

@SpringBootTest
class NormalTest {
    @Test
    public void testCrawl() {

    }

    @Test
    public void testConnect() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection(//
                "jdbc:hive2://hadoophost:10000/movieana",//
                "hive",
                "hongyaohongyao"//
        );
        PreparedStatement ps = conn.prepareStatement("select * from movies where movie_id<=?");
        ps.setInt(1, 10);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getInt("movie_id") + " " + resultSet.getString("genres"));
        }
    }

    @Test
    public void testReplyProcess() {
        long mid = 400468157;
        long oid = 414361845;
        ReplyPhaseProcess replyPhaseProcess = ReplyPhaseProcess.createProcess(oid);
        replyPhaseProcess.waitUntilNoTask();
    }

    @Test
    public void testVideoInfoPhaseProcess() {
        long mid = 400468157;
        long oid = 414361845;
        BaseCrawlProcess process = VideoInfoPhaseProcess.createProcess(mid);
        process.waitUntilNoTask();
    }
}

class OtherTest {
    @Test
    public void getProxyIP() {
        SpiderAdditionTools.getProxyIpFromKuaidaili("proxy_ip.ipsrc");
    }
}
