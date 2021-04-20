package com.sam.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sam.wiki.domain.User;
import com.sam.wiki.domain.UserExample;
import com.sam.wiki.mapper.UserMapper;
import com.sam.wiki.req.UserQueryReq;
import com.sam.wiki.req.UserSaveReq;
import com.sam.wiki.resp.UserQueryResp;
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
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("總行數:{}", pageInfo.getTotal());
        LOG.info("總頁數:{}", pageInfo.getPages());

//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
//            //UserResp userResp = new UserResp();
//           //BeanUtils.copyProperties(user,userResp);
//           //對象複製
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//
//            respList.add(userResp);
//        }

        //列表複製
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        } else {
            //更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
