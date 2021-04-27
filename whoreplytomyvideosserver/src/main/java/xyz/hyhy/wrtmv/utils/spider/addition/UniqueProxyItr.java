package xyz.hyhy.wrtmv.utils.spider.addition;

import xyz.hyhy.wrtmv.utils.spider.base.AvailableProxyIterator;
import xyz.hyhy.wrtmv.utils.spider.base.ProxyIp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class UniqueProxyItr implements AvailableProxyIterator {
    private String file;
    private Iterator<ProxyIp> itr;
    private int size;

    public UniqueProxyItr(String file) {
        size = 0;
        this.file = file;
    }

    @Override
    public void startGetProxy() {
        Set<ProxyIp> set = new LinkedHashSet<>();
        try {
            Scanner sc = new Scanner(new FileInputStream(file));
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split(" ");
                if (line.length != 2)
                    continue;
                set.add(new ProxyIp(line[0], Integer.parseInt(line[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        itr = set.iterator();
    }

    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }

    @Override
    public ProxyIp next() {
        size++;
        return itr.next();
    }

    public int size() {
        return size;
    }
}
