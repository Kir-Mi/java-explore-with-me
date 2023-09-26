package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullCommentDto {
    private Long id;
    private String content;
    private UserShortDto author;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime updated;
}