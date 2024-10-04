package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentModify;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    CommentResponse toCommentResponse(Comment comment);

    List<CommentResponse> toCommentResponseList(List<Comment> comments);

    Comment toComment(CommentModify commentModify);
}
