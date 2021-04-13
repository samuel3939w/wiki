package com.sam.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sam.wiki.domain.Category;
import com.sam.wiki.domain.CategoryExample;
import com.sam.wiki.mapper.CategoryMapper;
import com.sam.wiki.req.CategoryQueryReq;
import com.sam.wiki.req.CategorySaveReq;
import com.sam.wiki.resp.CategoryQueryResp;
import com.sam.wiki.resp.PageResp;
import com.sam.wiki.utils.CopyUtil;
import com.sam.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("總行數:{}", pageInfo.getTotal());
        LOG.info("總頁數:{}", pageInfo.getPages());

//        List<CategoryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
//            //CategoryResp categoryResp = new CategoryResp();
//           //BeanUtils.copyProperties(category,categoryResp);
//           //對象複製
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
//
//            respList.add(categoryResp);
//        }

        //列表複製
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public List<CategoryQueryResp> all(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        //列表複製
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    /**
     * 保存
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id){
         categoryMapper.deleteByPrimaryKey(id);
    }
}
