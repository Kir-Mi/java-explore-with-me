package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.dto.LocationDtoCoordinates;
import ru.practicum.validation.EventDateValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.utils.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    @NotNull
    @EventDateValidator
    private LocalDateTime eventDate;

    @Valid
    @NotNull
    private LocationDtoCoordinates location;

    private boolean paid;

    @PositiveOrZero
    private long participantLimit;

    @Builder.Default
    private boolean requestModeration = true;

    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}