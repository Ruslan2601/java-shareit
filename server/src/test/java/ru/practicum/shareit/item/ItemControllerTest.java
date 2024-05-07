package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private final User user = new User(1, "asd@asd.ru", "Ruslan");

    private final ItemResponse itemResponse = new ItemResponse(1, "table", "big table",
            true, null, null,
            null, 1, null);

    @SneakyThrows
    @Test
    void addItem_shouldAddItem() {
        ItemDto itemDto = new ItemDto(1, "table", "big table", true, 1);
        when(itemService.addItem(any(Integer.class), any()))
                .thenReturn(itemResponse);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }


    @SneakyThrows
    @Test
    void addItem_WithOutHeader() {
        ItemDto itemDto = new ItemDto(1, "table", "big table", true, 1);
        when(itemService.addItem(any(Integer.class), any()))
                .thenReturn(itemResponse);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.error").value("Required request header 'X-Sharer-User-Id' for method parameter type Integer is not present"));
    }

    @SneakyThrows
    @Test
    void getItem_shouldReturnItemById() {
        when(itemService.getItem(any(Integer.class), any(Integer.class)))
                .thenReturn(itemResponse);

        mockMvc.perform(get("/items/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getAllItems_shouldReturnListOfItems() {
        when(itemService.getAllItems(any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/items")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemResponse.getAvailable())));
    }

    @SneakyThrows
    @Test
    void updateItem_shouldUpdateItem() {
        ItemUpdate itemUpdate = new ItemUpdate(1, "table", "big table", true);
        when(itemService.updateItem(any(Integer.class), any(Integer.class), any()))
                .thenReturn(itemResponse);

        mockMvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(itemUpdate))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

    @SneakyThrows
    @Test
    void search_shouldReturnItemsList() {
        when(itemService.search(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/items/search?text=description")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemResponse.getAvailable())));
    }

    @SneakyThrows
    @Test
    void addComment_shouldAddComment() {
        CommentResponse commentResponse = new CommentResponse(1,"abcd", "Ruslan", LocalDateTime.now());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("abcd");
        when(itemService.addComment(any(Integer.class), any(CommentDto.class), any(Integer.class)))
                .thenReturn(commentResponse);

        mockMvc.perform(post("/items/1/comment")
                .content(objectMapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentResponse.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}