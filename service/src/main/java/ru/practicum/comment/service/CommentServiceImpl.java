package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.enums.EventStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public FullCommentDto saveComment(Long userId, NewCommentDto newCommentDto, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException(String.format("Event %s isn't published", eventId), HttpStatus.CONFLICT);
        }

        Comment comment = Comment.builder()
                .content(newCommentDto.getContent())
                .created(LocalDateTime.now())
                .event(event)
                .author(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toFullCommentDto(savedComment);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        boolean isExist = commentRepository.existsById(commentId);

        if (isExist) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException(String.format("Comment %s not found", commentId), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteCommentAddedCurrentUser(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment %s not found", commentId), HttpStatus.NOT_FOUND));

        User author = userRepository.findById(authorId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", authorId), HttpStatus.NOT_FOUND));

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new ValidationException(String.format("User %s isn't author of comment %s", authorId, commentId), HttpStatus.CONFLICT);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<FullCommentDto> getCommentsByEventId(Long eventId, Pageable pageable) {
        List<Comment> comments = commentRepository.getCommentsByEventId(eventId, pageable);

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(CommentMapper::toFullCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public FullCommentDto updateCommentByAuthor(Long commentId, Long authorId, UpdateCommentDto dto) {
        Comment toUpdateComment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment %s not found", commentId), HttpStatus.NOT_FOUND));

        User author = userRepository.findById(authorId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", authorId), HttpStatus.NOT_FOUND));

        if (!toUpdateComment.getAuthor().getId().equals(author.getId())) {
            throw new ValidationException(String.format("User %s isn't author of comment %s", authorId, commentId), HttpStatus.CONFLICT);
        }

        toUpdateComment.setContent(dto.getContent());
        toUpdateComment.setUpdated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(toUpdateComment);

        return CommentMapper.toFullCommentDto(savedComment);
    }
}