package com.rest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
class BookControllerTest {
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    Book RECORD_1 = new Book(1L, "Habits", "how to build better habits", 5);
    Book RECORD_2 = new Book(2L, "Thinking", "how to create good mental model", 4);
    Book RECORD_3 = new Book(3L, "algorithms", "learning alg in the fun way", 5);

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getAllRecords_success() throws Exception {
        List<Book> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));

        Mockito.when(bookRepository.findAll()).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].name", is("algorithms")));
    }

    @Test
    void getBookById_success() throws Exception {
        Mockito.when(bookRepository.findById(RECORD_1.getBookId()))
                .thenReturn(Optional.of(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Habits")));
    }

    @Test
    void getBookById_notFound() throws Exception {
        Mockito.when(bookRepository.findById(RECORD_1.getBookId()))
                .thenReturn(Optional.of(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(emptyOrNullString()));
    }

    @Test
    void createRecord_success() throws Exception {
        Book record = Book.builder().bookId(4L)
                .name("Introduction to C")
                .summary("concise intro to c")
                .rating(5)
                .build();

        Mockito.when(bookRepository.save(record))
                .thenReturn(record);

        String content = objectWriter.writeValueAsString(record);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content); // not support POJO, but only String

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Introduction to C")));
    }

    @Test
    void updateBookRecord_success() throws Exception {
        Book updatedRecord = Book.builder().bookId(1L)
                .name("updated Book name")
                .summary("updated summary")
                .rating(1)
                .build();

        Mockito.when(bookRepository.findById(RECORD_1.getBookId()))
                .thenReturn(Optional.of(RECORD_1));
        Mockito.when(bookRepository.save(updatedRecord))
                .thenReturn(updatedRecord);

        String updatedContent = objectWriter.writeValueAsString(updatedRecord);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(updatedContent); // not support POJO, but only String

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("updated Book name")));
    }

    @Test
    void deleteBookRecord_success() throws Exception{
        Mockito.when(bookRepository.findById(RECORD_1.getBookId()))
                .thenReturn(Optional.of(RECORD_1));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/book/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }
}