package com.chen.communit.service;

import com.chen.communit.dto.PaginationDTO;
import com.chen.communit.dto.QuestionDTO;
import com.chen.communit.dto.QuestionQueryDTO;
import com.chen.communit.exception.CustomizeErrorCode;
import com.chen.communit.exception.CustomizeException;
import com.chen.communit.mapper.QuestionExtMapper;
import com.chen.communit.mapper.QuestionMapper;
import com.chen.communit.mapper.UserMapper;
import com.chen.communit.model.Question;
import com.chen.communit.model.QuestionExample;
import com.chen.communit.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public PaginationDTO list(String search,Integer page, Integer size) {


        if (StringUtils.isNotBlank(search)){
            String[] split = StringUtils.split(search, " ");
            search = Arrays.stream(split).collect(Collectors.joining("|"));
        }
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
        //查询数据总条数
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        if (totalCount==0){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
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

        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            //问题描述
            Question selectByPrimaryKey = questionMapper.selectByPrimaryKey(question.getId());
            question.setDescription(selectByPrimaryKey.getDescription());

            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    /**
     * 查询我发布的问题
     * @param userId
     * @param page
     * @param size 每页显示的数据条数
     * @return
     */
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
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
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    /**
     * 通过id查询问题
     * @param id
     * @return
     */
    public QuestionDTO getById(Long id) {
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
            //无id创建问题
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
    public void delById(long id) {
        questionMapper.deleteByPrimaryKey(id);
    }

    /**
     * 增加阅读数
     * @param id
     */
    public void inView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    /**
     * 查询左边的推荐内容
     * @param queryDTO
     * @return
     */
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        String questionDTOTag = queryDTO.getTag();
        if (StringUtils.isBlank(questionDTOTag)) {
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(questionDTOTag, ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        //questions转换为QuestionDTO
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
