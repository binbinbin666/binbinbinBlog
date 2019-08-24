package com.chen.communit.service;

import com.chen.communit.dto.PaginationDTO;
import com.chen.communit.dto.QuestionDTO;
import com.chen.communit.mapper.QuestionMapper;
import com.chen.communit.mapper.UserMapper;
import com.chen.communit.model.Question;
import com.chen.communit.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 显示全部问题
     * @param page 页码
     * @param size 每页显示的数据条数
     * @return
     */
    public PaginationDTO list(Integer page, Integer size) {

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        //查询数据总条数
        Integer totalCount = questionMapper.count();

        paginationDTO.setPagination(totalCount,page,size);
        //控制页数
        if(page<1){
            page = 1;
        }
        if(page>paginationDTO.getTotalPage()){
            page = paginationDTO.getTotalPage();
        }
        //size*(page-1)
        Integer offset = size*(page - 1);

        for (Question question : questionMapper.list(offset,size)) {
            QuestionDTO questionDTO = new QuestionDTO();
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    //查询我发布的问题
    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        //查询数据总条数
        Integer totalCount = questionMapper.countByUserId(userId);

        paginationDTO.setPagination(totalCount,page,size);
        //控制页数
        if(page<1){
            page = 1;
        }
        if(page>paginationDTO.getTotalPage()){
            page = paginationDTO.getTotalPage();
        }
        //size*(page-1)
        Integer offset = size*(page - 1);

        for (Question question : questionMapper.listByUserId(userId,offset,size)) {
            QuestionDTO questionDTO = new QuestionDTO();
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    //通过id查询问题
    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        //通过创建人的id查询出创建人
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    //创建或修改问题
    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //无id新增问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        } else {
            //修改问题
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }

    //删除问题
    public void delById(Integer id) {
        questionMapper.delById(id);
    }
}
