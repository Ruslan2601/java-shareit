package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemValidation;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemMapper mapper;
    private final CommentMapper commentMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public ItemResponse addItem(Integer userId, ItemDto itemDto, BindingResult bindingResult) {
        ItemValidation.validation(bindingResult);
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        Item item = mapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ObjectNotFoundException("ItemRequest с заданным id нет")));
            ItemResponse itemResponse = mapper.toItemResponse(itemRepository.save(item));
            itemResponse.setRequestId(itemDto.getRequestId());
            log.info("Добавлен товар");
            return itemResponse;
        } else {
            log.info("Добавлен товар");
            return mapper.toItemResponse(itemRepository.save(item));
        }
    }

    @Transactional
    public ItemResponse updateItem(Integer userId, Integer itemId, ItemUpdate itemUpdate) {
        Item thisItem = mapper.toItem(getItem(itemId, userId));
        if (thisItem.getOwner().getId() == userId) {
            if (itemUpdate.getName() != null) {
                thisItem.setName(itemUpdate.getName());
            }
            if (itemUpdate.getDescription() != null) {
                thisItem.setDescription(itemUpdate.getDescription());
            }
            if (itemUpdate.getAvailable() != null) {
                thisItem.setAvailable(itemUpdate.getAvailable());
            }
            itemRepository.save(thisItem);
            log.info("Обновлен товар");
            return mapper.toItemResponse(thisItem);
        } else {
            throw new ObjectNotFoundException("Данный товар не принадлежит пользователю с id = " + userId);
        }
    }

    public ItemResponse getItem(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Товара с такими id нет"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        ItemResponse itemResponse = mapper.toItemResponse(item);
        if (user.getId() == item.getOwner().getId()) {
            addLastAndNextBooking(itemResponse, userId);
        }
        List<Comment> commentList = commentRepository.findByItemId(itemId);
        itemResponse.setComments(commentList.stream().map(commentMapper::toCommentResponse).collect(Collectors.toList()));
        log.info("Отображен товар с id = {}", itemId);
        return itemResponse;
    }

    public Collection<ItemResponse> getAllItems(Integer userId, int from, int size) {
        Pageable unsortedPageable = PageRequest.of(from / size, size);
        log.info("Отображен список всех товаров пользователя");
        return itemRepository.findByOwnerId(userId, unsortedPageable).stream().map(x -> getItem(x.getId(), userId)).collect(Collectors.toList());
    }

    public Collection<ItemResponse> search(String text, int from, int size) {
        Pageable unsortedPageable = PageRequest.of(from / size, size);
        log.info("Вывод результатов поиска");
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, unsortedPageable).stream()
                .map(mapper::toItemResponse)
                .collect(Collectors.toList());
    }

    public CommentResponse addComment(Integer userId, CommentDto commentDto, Integer itemId) {
        if (commentDto.getText().isBlank() || commentDto.getText() == null) {
            throw new ValidationException("поле comment не может быть пустым");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Товара с такими id нет"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("User с заданным id = " + userId + " ещё не брал в аренду этот предмет"));
        if (item.getOwner().getId() == userId) {
            throw new ValidationException("User с заданным id = " + userId + " является владельцем");
        }
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Отзыв добавлен");
        return new CommentResponse(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    private void addLastAndNextBooking(ItemResponse item, Integer ownerId) {
        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                        item.getId(),
                        ownerId,
                        LocalDateTime.now(),
                        Status.APPROVED);
        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                        item.getId(),
                        ownerId,
                        LocalDateTime.now(),
                        Status.APPROVED);

        if (lastBooking != null) {
            item.setLastBooking(new ItemBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            item.setNextBooking(new ItemBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
    }
}
