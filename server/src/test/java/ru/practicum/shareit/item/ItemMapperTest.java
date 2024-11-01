package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemMapperTest {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Test
    public void testItemMapper() {
        Assertions.assertNull(itemMapper.toItem(null));
        Assertions.assertNull(itemMapper.toItemResponseWithComments(null, null));
        Assertions.assertNull(itemMapper.toItemResponseList(null));
        Assertions.assertNull(itemMapper.toItem(null));
        Assertions.assertNull(itemMapper.toItemShortResponse(null));
        Assertions.assertNull(itemMapper.toItemShortResponses(null));
        Assertions.assertNull(commentMapper.toCommentResponse(null));
        Assertions.assertNull(commentMapper.toComment(null));
    }
}
