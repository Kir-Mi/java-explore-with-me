package ru.practicum.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.Collection;

public interface CommentService {
    FullCommentDto saveComment(Long userId, NewCommentDto newCommentDto, Long eventId);

    void deleteCommentByAdmin(Long commentId);

    void deleteCommentAddedCurrentUser(Long commentId, Long authorId);

    Collection<FullCommentDto> getCommentsByEventId(Long eventId, Pageable pageable);

    FullCommentDto updateCommentByAuthor(Long commentId, Long authorId, UpdateCommentDto dto);
}