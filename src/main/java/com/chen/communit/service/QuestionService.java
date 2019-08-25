package com.chen.communit.service;

import com.chen.communit.dto.PaginationDTO;
import com.chen.communit.dto.QuestionDTO;
import com.chen.communit.exception.CustomizeErrorCode;
import com.chen.communit.exception.CustomizeException;
import com.chen.communit.mapper.QuestionExtMapper;
import com.chen.communit.mapper.QuestionMapper;
import com.chen.communit.mapper.UserMapper;
import com.chen.communit.model.Question;
import com.chen.communit.model.QuestionExample;
import com.chen.communit.model.User;
import org.apache.ibatis.session.RowBounds;
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

    @Autowired
    private QuestionExtMapper questionExtMapper;

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
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());

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

        for (Question question : questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size))) {
            QuestionDTO questionDTO = new QuestionDTO();
            //问题描述
            Question selectByPrimaryKey = questionMapper.selectByPrimaryKey(question.getId());
            question.setDescription(selectByPrimaryKey.getDescription());

            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    /**
     * 查询我发布的问题
     * @param userId
     * @param page
     * @param size 每页显示的数据条数
     * @return
     */
    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        //查询数据总条数
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);

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

        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        for (Question question : questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size))) {
            QuestionDTO questionDTO = new QuestionDTO();
            //问题描述
            Question selectByPrimaryKey = questionMapper.selectByPrimaryKey(question.getId());
            question.setDescription(selectByPrimaryKey.getDescription());

            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    /**
     * 通过id查询问题
     * @param id
     * @return
     */
    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        //如果id不存在,抛出异常
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        //通过创建人的id查询出创建人
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }


    /**
     * 发布或修改问题
     * @param question
     */
    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //无id新增问题
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        } else {
            //修改问题
            question.setGmtModified(System.currentTimeMillis());
            //修改的问题的内容
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            //根据Id修改
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated == 0) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }
    /**
     * 删除问题
     * @param id
     */
    public void delById(Integer id) {
        questionMapper.deleteByPrimaryKey(id);
    }

    /**
     * 增加阅读数
     * @param id
     */
    public void inView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
