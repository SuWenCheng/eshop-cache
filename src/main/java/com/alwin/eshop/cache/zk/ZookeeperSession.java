package com.alwin.eshop.cache.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZookeeperSession {

    private static final String ROOT_PATH = "/product-lock-";

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    private ZookeeperSession() {
        try {
            this.zooKeeper = new ZooKeeper("192.168.31.11:2181," +
                    "192.168.31.12:2181,192.168.31.13:2181", 50000, new ZookeeperWatch());
            try {
                connectedSemaphore.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("ZooKeeper session established..........");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取分布式锁
     * @param productId 商品ID
     */
    public void acquireDistributedLock(Long productId) {
        String path = ROOT_PATH + productId;
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            log.info("success to acquire lock for product[{}]", productId);
        } catch (Exception e) {
            // 如果该商品对应的锁的node已存在，即已经被别人加锁了，此时就会抛异常
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(20);
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    count++;
                    continue;
                }
                log.info("success to acquire lock for product[{}] after {} times try......", productId, count);
                break;
            }
        }
    }

    public void acquireDistributedLock(String path) {
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            log.info("success to acquire lock for {}", path);
        } catch (Exception e) {
            // 如果该商品对应的锁的node已存在，即已经被别人加锁了，此时就会抛异常
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(20);
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    count++;
                    continue;
                }
                log.info("success to acquire lock for {} after {} times try......", path, count);
                break;
            }
        }
    }

    /**
     * 快速失败分布式锁
     * @param path
     */
    public boolean acquireFastFailedDistributedLock(String path) {
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            log.info("success to acquire lock for {}", path);
            return true;
        } catch (Exception e) {
            log.info("fail to acquire lock for {}", path);
        }
        return false;
    }

    /**
     * 释放分布式锁
     * @param productId 商品ID
     */
    public void releaseDistributedLock(Long productId) {
        String path = ROOT_PATH + productId;
        try {
            zooKeeper.delete(path, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放分布式锁
     * @param path 路径
     */
    public void releaseDistributedLock(String path) {
        try {
            zooKeeper.delete(path, -1);
            log.info("release the lock for {}", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNode(String path) {
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {

        }
    }

    public String getNodeData(String path) {
        try {
            return new String(zooKeeper.getData(path, false, new Stat()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setNodeData(String path, String data) {
        try {
            zooKeeper.setData(path, data.getBytes(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ZookeeperWatch implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            log.info("Receive watched event: " + watchedEvent.getState());
            if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                connectedSemaphore.countDown();
            }
        }
    }

    private static class Singleton {
        private static ZookeeperSession instance;
        static {
            instance = new ZookeeperSession();
        }
        public static ZookeeperSession getInstance() {
            return instance;
        }
    }

    public static ZookeeperSession getInstance() {
        return Singleton.getInstance();
    }

}
