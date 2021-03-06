package com.herokuapp.ddmura.service;

import com.herokuapp.ddmura.cache.AnonymousUser;
import com.herokuapp.ddmura.enums.CommentType;
import com.herokuapp.ddmura.dto.PaginationDTO;
import com.herokuapp.ddmura.dto.QuestionDTO;
import com.herokuapp.ddmura.exception.CustomizeException;
import com.herokuapp.ddmura.enums.ResultEnum;
import com.herokuapp.ddmura.mapper.QuestionExtMapper;
import com.herokuapp.ddmura.mapper.QuestionMapper;
import com.herokuapp.ddmura.mapper.UserMapper;
import com.herokuapp.ddmura.model.*;
import lombok.AllArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class QuestionService {

    private QuestionMapper questionMapper;
    private QuestionExtMapper questionExtMapper;
    private UserMapper userMapper;
    private AnonymousUser anonymousUser;

    private LikeService likeService;
    private UserService userService;

    public PaginationDTO<QuestionDTO> listByExample(QuestionExample example, String search, Integer page, Integer size) {
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        int totalCount;
        if (search != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : search.split(" ")) {
                if (s != null && !s.equals("")) {
                    if (sb.length() != 0) {
                        sb.append('|');
                    }
                    sb.append(s);
                }
            }
            search = sb.toString();
            totalCount = questionExtMapper.countBySearch(search);
        } else {
            totalCount = (int) questionMapper.countByExample(example);
        }
        RowBounds rowBounds = paginationDTO.setPagination(totalCount, page, size);

        example.setOrderByClause("gmt_modified desc");
        List<Question> questions;
        if (search != null) {
            questions = questionExtMapper.selectBySearchWithLimit(search, rowBounds.getOffset(), rowBounds.getLimit());
        } else {
            questions = questionMapper.selectByExampleWithRowbounds(example, rowBounds);
        }
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDto = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDto);
            User user = userService.getById(question.getCreator());
            if (user != null) {
                questionDto.setUser(user);
            } else {
                questionDto.setUser(anonymousUser);
            }
            questionDTOList.add(questionDto);
        }
        paginationDTO.setList(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO<QuestionDTO> listByExample(QuestionExample example, Integer page, Integer size) {
        return listByExample(example, null, page, size);
    }

    public PaginationDTO<QuestionDTO> list(String search, Integer page, Integer size) {
        return listByExample(new QuestionExample(), search, page, size);
    }

    public PaginationDTO<QuestionDTO> listByUser(Integer userId, Integer page, Integer size) {
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        return listByExample(example, page, size);
    }

    public QuestionDTO findById(Integer id, User sessionUser) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(ResultEnum.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDto = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDto);
        questionDto.setUser(userService.getById(questionDto.getCreator()));
        questionDto.setLikeCount(likeService.getLikeCount(id, CommentType.LIKE_QUESTION));

        // Set liked
        if (sessionUser != null && questionDto.getLikeCount() > 0 &&
                likeService.isContained(id, CommentType.LIKE_QUESTION, sessionUser.getId())) {
            questionDto.setLiked(true);
        }
        return questionDto;
    }

    public void incView(int id, int num) {
        if (questionExtMapper.incView(id, num) == 0) {
            throw new CustomizeException(ResultEnum.QUESTION_NOT_FOUND);
        }
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 新建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        } else {
            // 更新
            question.setGmtCreate(null);    // 不更新创建时间
            question.setGmtModified(System.currentTimeMillis());
            question.setCreator(null);      // 不更新创建者
            if (questionMapper.updateByPrimaryKeySelective(question) == 0) {
                throw new CustomizeException(ResultEnum.QUESTION_NOT_FOUND);
            }
        }
    }
}
