package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentModify;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemModify;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemResponse get(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toItemResponse)
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + id + " не найден"));
    }

    @Override
    public List<ItemResponse> getAllForUser(Long id) {
        List<Item> items = itemRepository.findAllByOwnerId(id);
        return itemMapper.toItemResponseList(items);
    }

    @Override
    public List<ItemResponse> getForSearch(String text) {
        List<Item> items =  itemRepository.findAllByNameLikeIgnoreCase(text);
        return itemMapper.toItemResponseList(items);
    }

    @Override
    public CommentResponse createComment(Long itemId, Long bookerId, CommentModify commentModify) {
        Booking booking = bookingRepository.findByItem_IdAndBooker_Id(itemId, bookerId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        System.out.println(booking);
        System.out.println(booking.getStart());
        System.out.println(booking.getEnd());
        System.out.println(booking.getStatus());
        System.out.println(booking.getBookingState());
        if (booking.getBookingState().equals(BookingState.PAST)) {
            Item item = booking.getItem();
            Comment comment = commentMapper.toComment(commentModify);
            comment.setItem(item);
            comment = commentRepository.save(comment);
            return commentMapper.toCommentResponse(comment);
        } else {
            throw new NoPermissionException("Вы не можете оставить комментарий");
        }
    }

    @Override
    public ItemResponse create(Long userId, ItemModify request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        Item item = itemMapper.toItem(request);
        item.setOwner(user);
        itemRepository.save(item);
        return itemMapper.toItemResponse(item);
    }

    @Override
    public ItemResponse patch(Long id, Long userId, ItemModify request) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + id + " не найден"));

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + id + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        if (request.getName() != null)
            item.setName(request.getName());

        if (request.getDescription() != null)
            item.setDescription(request.getDescription());

        if (request.getAvailable() != null)
            item.setAvailable(request.getAvailable());

        if (request.getItemRequestModify() != null) {
            item.setItemRequest(itemRequestMapper.toItemRequest(request.getItemRequestModify()));
        }
        return itemMapper.toItemResponse(item);
    }

    @Override
    public void delete(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + id + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        itemRepository.deleteById(id);
    }

}
