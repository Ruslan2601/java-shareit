package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestRequest request = new ItemRequestRequest();
    private final User requester = new User(1, "asd@asd.ru", "Ruslan");
    private final ItemRequestResponse response = new ItemRequestResponse(1, "desc", requester, LocalDateTime.now(), null);

    @SneakyThrows
    @Test
    void addRequest_shouldAddRequest() {
        request.setDescription("desc");
        when(requestService.addRequest(any(Integer.class), any(ItemRequestRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/requests")
                .content(objectMapper.writeValueAsString(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(response.getRequester().getId()), Integer.class))
                .andExpect(jsonPath("$.requester.name", is(response.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(response.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(response.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequest_shouldReturnRequest() {
        when(requestService.getRequest(any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        mvc.perform(get("/requests/1")
                .content(objectMapper.writeValueAsString(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(response.getRequester().getId()), Integer.class))
                .andExpect(jsonPath("$.requester.name", is(response.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(response.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(response.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getAllRequestsWithPagination_shouldReturnListOfRequests() {
        when(requestService.getAllRequestsWithPagination(any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests/all")
                .content(objectMapper.writeValueAsString(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(response.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(response.getRequester().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].requester.name", is(response.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(response.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(response.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getAllRequests_shouldReturnRequestsByOwner() {
        when(requestService.getAllRequests(any(Integer.class)))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests")
                .content(objectMapper.writeValueAsString(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(response.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(response.getRequester().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].requester.name", is(response.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(response.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(response.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

}