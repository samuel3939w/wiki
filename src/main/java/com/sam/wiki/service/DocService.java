package com.sam.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sam.wiki.domain.Content;
import com.sam.wiki.domain.Doc;
import com.sam.wiki.domain.DocExample;
import com.sam.wiki.exception.BusinessException;
import com.sam.wiki.exception.BusinessExceptionCode;
import com.sam.wiki.mapper.ContentMapper;
import com.sam.wiki.mapper.DocMapper;
import com.sam.wiki.mapper.DocMapperCust;
import com.sam.wiki.req.DocQueryReq;
import com.sam.wiki.req.DocSaveReq;
import com.sam.wiki.resp.DocQueryResp;
import com.sam.wiki.resp.PageResp;
import com.sam.wiki.utils.CopyUtil;
import com.sam.wiki.utils.RedisUtil;
import com.sam.wiki.utils.RequestContext;
import com.sam.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private DocMapperCust docMapperCust;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public WsService wsService;

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();

        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("總行數:{}", pageInfo.getTotal());
        LOG.info("總頁數:{}", pageInfo.getPages());

//        List<DocResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
//            //DocResp docResp = new DocResp();
//           //BeanUtils.copyProperties(doc,docResp);
//           //對象複製
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//
//            respList.add(docResp);
//        }

        //列表複製
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表複製
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    /**
     * 保存
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            //更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        //文檔閱讀數+1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }

    /**
     * 點讚
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 遠程IP+doc.id作為key，24小時内不能重複
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        //推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        wsService.sendInfo("【" + docDb.getName() + "】被點讚!");
    }

    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
