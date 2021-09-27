package com.ironhack.midtermproject1.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject1.ApplicationTest;
import com.ironhack.midtermproject1.dao.*;
import com.ironhack.midtermproject1.enums.AccountStatus;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.enums.TransactionType;
import com.ironhack.midtermproject1.repository.*;
import com.ironhack.midtermproject1.utils.dataValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TransactionControllerTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Transaction> transactionList;
    private List<AccountHolder> accountHolderList;
    private List<Address> addressList;
    private Money money;
    private Money money2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        //module to make Jackson recognize Java 8 Date and Time API data types (JSR-310)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        money = new Money(new BigDecimal(1000), Currency.getInstance("USD"));
        money2 = new Money(new BigDecimal(1500), Currency.getInstance("EUR"));

        addressList = addressRepository.saveAll(List.of(
                new Address("Street1", "20a", "34", "Warsaw", "Poland", "34-098"),
                new Address("Street2", "145", "3", "Madrid", "Spain", "908044")
        ));

        accountHolderList = accountHolderRepository.saveAll(List.of(
                new AccountHolder("username1", "password1", true, "Marc", "Smith", LocalDate.of(2000, 9, 18), addressList.get(0), "accountHolder1@email.com"),
                new AccountHolder("username2", "password2", true, "Maria", "Twain", LocalDate.of(1989, 10, 20), addressList.get(1), "accountHolder2@email.com"),
                new AccountHolder("username3", "password3", true, "Daniel", "Smart", LocalDate.of(1994, 2, 1), addressList.get(1), "accountHolder3@email.com")
        ));

        transactionList = transactionRepository.saveAll(List.of(
                new Transaction(TransactionType.DEPOSIT, money.getBalance(), 1L,  ReturnType.CHECKING, accountHolderList.get(0), 3L, ReturnType.CREDIT_CARD, accountHolderList.get(1), "EUR"),
                new Transaction(TransactionType.TRANSFER, money.getBalance(), 2L,  ReturnType.SAVING, accountHolderList.get(1), 5L, ReturnType.SAVING, accountHolderList.get(1), "USD"),
                new Transaction(TransactionType.PAYMENT, money.getBalance(), 1L,  ReturnType.STUDENT_CHECKING, accountHolderList.get(2), 5L, ReturnType.CHECKING, accountHolderList.get(1), "EUR")
        ));

    }

    @AfterEach
    void tearDown() {
        //transactionRepository.deleteAll();
    }

    @Test
    void get_getTransactions() throws Exception{
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Street1"));
    }

    @Test
    void get_getCheckingAccounts_sortBy() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sortBy", "id");
        params.add("page","0");

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/transactions").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertAll(
                () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("1")),
                () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("3"))
        );
    }

    @Test
    void get_getTransactionById_isOk() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/transactions/" + transactionList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Street1"));
    }

    @Test
    void get_getTransactionById_isOk_null() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/transactions/9999"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertEquals(mvcResult.getResponse().getContentAsString(), "");
    }

    @Test
    void get_getTransactionBySourceAccountIdAndSourceAccountType() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "1");
        params.add("returnType",ReturnType.CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/transactions").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("Street1"));
    }

    @Test
    void get_getTransactionByAnyParameter() throws Exception{

        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sourceAccountType", ReturnType.CHECKING.toString());
        params.add("transactionType", TransactionType.DEPOSIT.toString());
        params.add("targetAccountType", ReturnType.CREDIT_CARD.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/transactions/getByAnyParams").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Smith"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("Smart"));
    }

    //fixme
    @Test
    void patch_detectFraud() throws Exception {
       Checking checking =  new Checking(money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"345FGH", AccountStatus.ACTIVE);
        checkingRepository.save(checking);
        transactionList = transactionRepository.saveAll(List.of(
                new Transaction(LocalDateTime.of(2021, 7, 23,23,50,16,016), TransactionType.DEPOSIT, money.getBalance(), checking.getId(),  ReturnType.CHECKING, accountHolderList.get(0), checking.getId(), ReturnType.CHECKING, accountHolderList.get(1), "EUR"),
                new Transaction(LocalDateTime.of(2021, 7, 23,23,50,16,017), TransactionType.DEPOSIT, money.getBalance(), checking.getId(),  ReturnType.CHECKING, accountHolderList.get(0), checking.getId(), ReturnType.CHECKING, accountHolderList.get(1), "EUR"),
                new Transaction(LocalDateTime.of(2021, 7, 23,23,50,16,020), TransactionType.DEPOSIT, money.getBalance(), checking.getId(),  ReturnType.CHECKING, accountHolderList.get(0), checking.getId(), ReturnType.CHECKING, accountHolderList.get(1), "EUR")
        ));

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/transactions/detectFraud"))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(checking.getStatus(), AccountStatus.FROZEN);
    }

    @Test
    void deleteTransaction() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                delete("/api/v1/transactions/" + transactionList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();
        //then
        assertFalse(mvcResult.getResponse().getContentAsString().contains("password1"));
    }
}