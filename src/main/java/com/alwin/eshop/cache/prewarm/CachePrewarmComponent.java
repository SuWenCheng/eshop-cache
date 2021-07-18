package com.alwin.eshop.cache.prewarm;


import com.alibaba.fastjson.JSONArray;
import com.alwin.eshop.cache.model.ProductInfo;
import com.alwin.eshop.cache.service.CacheService;
import com.alwin.eshop.cache.zk.ZookeeperSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CachePrewarmComponent {

    private static final String TASK_ID_NODE_DATA_PATH = "/taskid-list";
    private static final String TASK_ID_STATUS_NODE_DATA_PATH_PREFIX = "/taskid-status";
    private static final String TASK_ID_LOCK_PATH_PREFIX = "/taskid-lock-";
    private static final String TASK_ID_STATUS_LOCK_PATH_PREFIX = "/taskid-status-lock-";
    private static final String HOT_PRODUCT_LIST_NODE_DATA_PATH_PREFIX = "/task-hot-product-list-";

    private final CacheService cacheService;

    @Async
    public void prewarmCache() {
        ZookeeperSession zkSession = ZookeeperSession.getInstance();

        String taskIdList = zkSession.getNodeData(TASK_ID_NODE_DATA_PATH);

        if (taskIdList != null && !"".equals(taskIdList)) {
            String[] taskIdArray = taskIdList.split(",");
            for (String taskId : taskIdArray) {
                String taskIdLockPath = TASK_ID_LOCK_PATH_PREFIX + taskId;

                // 如果拿不到对应taskid锁，说明已经有其他服务实例在预热了
                boolean taskLock = zkSession.acquireFastFailedDistributedLock(taskIdLockPath);
                if (!taskLock) {
                    continue;
                }

                String taskIdStatusLockPath = TASK_ID_STATUS_LOCK_PATH_PREFIX + taskId;
                zkSession.acquireDistributedLock(taskIdStatusLockPath);

                String taskIdStatus = zkSession.getNodeData(TASK_ID_STATUS_NODE_DATA_PATH_PREFIX + taskId);
                if (taskIdStatus == null || "".equals(taskIdStatus)) {
                    String productIdList = zkSession.getNodeData(HOT_PRODUCT_LIST_NODE_DATA_PATH_PREFIX + taskId);
                    JSONArray productIdJSONArray = JSONArray.parseArray(productIdList);
                    for (int i = 0; i < productIdJSONArray.size(); i++) {
                        Long productId = productIdJSONArray.getLong(i);
                        ProductInfo productInfo = cacheService.getProductInfoFromDB(productId);
                        cacheService.saveProductInfo2LocalCache(productInfo);
                        cacheService.saveProductInfo2RedisCache(productInfo);
                    }

                    zkSession.createNode(TASK_ID_STATUS_NODE_DATA_PATH_PREFIX + taskId);
                    zkSession.setNodeData(TASK_ID_STATUS_NODE_DATA_PATH_PREFIX + taskId, "success");
                }

                zkSession.releaseDistributedLock(taskIdStatusLockPath);
                zkSession.releaseDistributedLock(taskIdLockPath);
            }
        }
    }
}
