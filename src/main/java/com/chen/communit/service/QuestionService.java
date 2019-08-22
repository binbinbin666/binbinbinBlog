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
     *
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
            User user = userMapper.findById(question.getCreator());
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
            User user = userMapper.findById(question.getCreator());
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }
}
