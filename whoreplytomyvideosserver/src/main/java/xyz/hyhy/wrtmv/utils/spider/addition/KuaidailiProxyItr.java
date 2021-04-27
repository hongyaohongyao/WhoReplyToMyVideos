package xyz.hyhy.wrtmv.utils.spider.addition;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.hyhy.wrtmv.utils.spider.base.AvailableProxyIterator;
import xyz.hyhy.wrtmv.utils.spider.base.ProxyIp;
import xyz.hyhy.wrtmv.utils.spider.utils.NetUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KuaidailiProxyItr implements AvailableProxyIterator {
    private final String baseUrl;
    private final Queue<ProxyIp> availableIp;
    private boolean isGetting;
    private int startPage;
    private int endPage;

    public KuaidailiProxyItr() {
        this(1, 10);
    }

    public KuaidailiProxyItr(int startPage, int endPage) {
        this(startPage, endPage, true);
    }

    public KuaidailiProxyItr(int startPage, int endPage, boolean intr) {
        baseUrl = "https://www.kuaidaili.com/free/" + (intr ? "intr/" : "inha/");
        isGetting = false;
        availableIp = new LinkedList<>();
        setPageRange(startPage, endPage);
    }

    public void setPageRange(int startPage, int endPage) {
        this.startPage = startPage;
        this.endPage = endPage;
    }

    @Override
    public boolean hasNext() {
        synchronized (availableIp) {
            if (!isGetting)
                return availableIp.peek() != null;
            while (isGetting && availableIp.peek() == null) {
                try {
                    availableIp.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return availableIp.peek() != null;
        }

    }

    @Override
    public ProxyIp next() {
        return availableIp.poll();
    }

    private void getProxyThread(ExecutorService service) {
        CountDownLatch lock = new CountDownLatch(endPage - startPage + 1);
        for (int i = startPage; i <= endPage; i++) {
            int p = i;
            service.execute(() -> {
                dealAPage(p, service);
                lock.countDown();
            });
        }
        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endGetProxy();
        service.shutdown();
    }

    @Override
    public void startGetProxy() {
        if (isGetting)
            return;
        isGetting = true;
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(() -> getProxyThread(service));
    }

    private void addProxyIp(ProxyIp proxyIp) {
        synchronized (availableIp) {
            availableIp.add(proxyIp);
            availableIp.notifyAll();
        }

    }

    private void dealAPage(int page, ExecutorService service) {
        Document doc;
        int tryTime = 3;
        while (true) {
            try {
                Thread.sleep((long) (Math.random() * 1500));
                doc = Jsoup.connect(baseUrl + page).get();
                break;
            } catch (Exception e) {
                if (tryTime-- <= 0) {
                    return;
                }
            }
        }

        Element tbody = doc.getElementsByTag("tbody").get(0);
        Elements children = tbody.children();
        CountDownLatch lock = new CountDownLatch(children.size());
        for (Element e : tbody.children()) {
            String ip = e.getElementsByAttributeValue("data-title", "IP").get(0).ownText();
            String portText = e.getElementsByAttributeValue("data-title", "PORT").get(0).ownText();
            int port = Integer.parseInt(portText);
            service.execute(() -> {
                dealIpAndPort(ip, port);
                lock.countDown();
            });
        }
        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void dealIpAndPort(String ip, int port) {
        ProxyIp proxyIp = new ProxyIp(ip, port);
        if (NetUtils.checkIp(proxyIp))
            addProxyIp(proxyIp);
    }


    private void endGetProxy() {
        isGetting = false;
        synchronized (availableIp) {
            availableIp.notifyAll();
        }
    }
}
