package com.sam.wiki.controller;

import com.sam.wiki.req.EbookReq;
import com.sam.wiki.resp.CommonResp;
import com.sam.wiki.resp.EbookResp;
import com.sam.wiki.resp.PageResp;
import com.sam.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list=ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
