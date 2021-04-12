package com.sam.wiki.controller;

import com.sam.wiki.req.EbookQueryReq;
import com.sam.wiki.req.EbookSaveReq;
import com.sam.wiki.resp.CommonResp;
import com.sam.wiki.resp.EbookQueryResp;
import com.sam.wiki.resp.PageResp;
import com.sam.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list=ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
