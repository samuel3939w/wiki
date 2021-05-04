package com.sam.wiki.job;
import com.sam.wiki.service.EbookSnapshotService;
import com.sam.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EbookSnapshotJob {

    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 自定義cron表達式跑批
     * 只有等上一次執行完成，下一次才會在下一個時間點執行，錯過就錯過
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void doSnapshot() {
        // 增加日志流水號
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("生成今日電子書快照開始");
        Long start = System.currentTimeMillis();
        ebookSnapshotService.genSnapshot();
        LOG.info("生成今日電子書快照结束，耗時：{}毫秒", System.currentTimeMillis() - start);
    }

}
