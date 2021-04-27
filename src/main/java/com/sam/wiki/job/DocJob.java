package com.sam.wiki.job;

import com.sam.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Autowired
    private DocService docService;

    /**
     * 每30秒更新電子書信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        LOG.info("更新電子書下的文檔數據開始");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新電子書下的文檔數據結束，耗時：{}毫秒", System.currentTimeMillis() - start);
    }

}
