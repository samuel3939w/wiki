package com.sam.wiki.service;

import com.sam.wiki.mapper.EbookSnapshotMapperCust;
import com.sam.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {
    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    /**
     * 獲取首頁數值數據：總閱讀數、總點讚數、今日閱讀數、今日點讚數、今日預計閱讀數、今日預計閱讀增長
     */
    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }
}
