package com.sam.wiki.service;

import com.sam.wiki.domain.Demo;
import com.sam.wiki.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {
    @Autowired
    private DemoMapper temoMapper;

    public List<Demo> list(){
        return temoMapper.selectByExample(null);
    }
}
