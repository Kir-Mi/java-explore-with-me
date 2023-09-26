package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.user.mapper.UserMapper;

@UtilityClass
public class CommentMapper {

    public FullCommentDto toFullCommentDto(Comment comment) {
        return FullCommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .author(UserMapper.userShortDto(comment.getAuthor()))
                .build();
    }
}