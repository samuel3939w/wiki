package com.sam.wiki.job;

import com.sam.wiki.service.DocService;
import com.sam.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Autowired
    private DocService docService;

    @Autowired
    private SnowFlake snowFlake;

    /**
     * 每30秒更新電子書信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("更新電子書下的文檔數據開始");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新電子書下的文檔數據結束，耗時：{}毫秒", System.currentTimeMillis() - start);
    }

}
