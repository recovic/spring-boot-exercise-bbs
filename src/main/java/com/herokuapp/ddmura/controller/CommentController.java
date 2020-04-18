package com.herokuapp.ddspace.controller;

import com.herokuapp.ddspace.dto.CommentCreateDTO;
import com.herokuapp.ddspace.dto.CommentDTO;
import com.herokuapp.ddspace.enums.CommentType;
import com.herokuapp.ddspace.dto.ResultDTO;
import com.herokuapp.ddspace.enums.ResultEnum;
import com.herokuapp.ddspace.mapper.CommentMapper;
import com.herokuapp.ddspace.model.Comment;
import com.herokuapp.ddspace.model.User;
import com.herokuapp.ddspace.service.CommentService;
import com.herokuapp.ddspace.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;
    private NotificationService notificationService;
    private CommentMapper commentMapper;

    @ResponseBody
    @PostMapping("/comment")
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultEnum.NO_LOGIN;
        }
        if (commentCreateDTO == null || StringUtils.isEmpty(commentCreateDTO.getContent())) {
            return ResultEnum.EMPTY_COMMENT;
        }
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentCreateDTO, comment);
        comment.setUserId(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        ResultEnum result = commentService.insert(comment);
        if (result.getCode() != 200) {
            return result;
        }
        return notificationService.insertByComment(comment);
    }

    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Integer id) {
        List<CommentDTO> commentDTOS = commentService.findByParent(id, CommentType.COMMENT, null);
        return ResultDTO.okOf(commentDTOS);
    }

    @GetMapping("/jump/{id}")
    public String jump(@PathVariable(name = "id") Integer id, @RequestParam("type") Integer type) {
        if (type == CommentType.QUESTION || type == CommentType.LIKE_QUESTION) {
            return "redirect:/question/" + id;
        } else if (type == CommentType.COMMENT || type == CommentType.LIKE_COMMENT) {
            Comment comment = commentMapper.selectByPrimaryKey(id);
            if (comment.getType() == CommentType.QUESTION || comment.getType() == CommentType.LIKE_QUESTION) {
                return "redirect:/question/" + comment.getParentId() + "#comment_" + comment.getId();
            } else if (comment.getType() == CommentType.COMMENT || comment.getType() == CommentType.LIKE_COMMENT) {
                Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
                return "redirect:/question/" + parentComment.getParentId() + "#comment_" + parentComment.getId();
            }
        }
        return "redirect:/";
    }
}