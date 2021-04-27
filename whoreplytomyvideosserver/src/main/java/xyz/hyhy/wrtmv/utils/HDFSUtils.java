package xyz.hyhy.wrtmv.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import xyz.hyhy.wrtmv.config.HDFSConfig;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

public class HDFSUtils {
    @Resource
    private static HDFSConfig hdfsConfig;
    @Resource
    private static Configuration hadoopConfiguration;

    private HDFSUtils() {

    }

    public static FileSystem getFileSystem() throws IOException, InterruptedException {
        String path = hdfsConfig.finalHdfsBasePath();
        FileSystem fs = FileSystem.get(URI.create(path), hadoopConfiguration, hdfsConfig.getUser());
        System.out.println(path);
        return fs;
    }

    public static FSDataOutputStream getOutputStream(FileSystem fs, String file) throws IOException {
        Path path = new Path(file);
        return fs.exists(path) ? fs.append(path) : fs.create(path);

    }

    public static void appendToFile(String file, Iterable<String> source) {
        FileSystem fs = null;
        FSDataOutputStream fdos = null;
        PrintWriter out = null;
        try {
            fs = getFileSystem();
            fdos = getOutputStream(fs, file);
            out = new PrintWriter(fdos);
            for (String data : source) {
                out.println(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeAll(out, fdos, fs);
        }

    }
}
