package com.ironhack.midtermproject1.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject1.ApplicationTest;
import com.ironhack.midtermproject1.dao.*;
import com.ironhack.midtermproject1.enums.AccountStatus;
import com.ironhack.midtermproject1.enums.ReturnType;
import com.ironhack.midtermproject1.repository.*;
import com.ironhack.midtermproject1.utils.dataValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

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
class CreditCardControllerTest {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<CreditCard> creditCardList;
    private List<Checking> checkingList;
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

        creditCardList = creditCardRepository.saveAll(List.of(
                new CreditCard(money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),new BigDecimal("0.20"), new BigDecimal("1000.00"), LocalDateTime.of(2021, 9, 23,23,50,16,016)),
                new CreditCard(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),new BigDecimal("0.21"), new BigDecimal("1500.00"), LocalDateTime.of(2021, 7, 23,23,50,16,016))
        ));

        checkingList = checkingRepository.saveAll(List.of(
                new Checking(money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"345FGH", AccountStatus.ACTIVE),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.FROZEN),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE, LocalDateTime.of(2021, 7, 23,23,50,16,016)),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE, LocalDateTime.of(2021, 8, 23,23,50,16,016))
        ));
    }

    @AfterEach
    void tearDown() {
        //creditCardRepository.deleteAll();
    }

    @Test
    void get_getCreditCards() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/creditCards"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1@email.com"));
        assertAll(
            () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("USD")),
            () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("EUR"))
        );
    }

    @Test
    void get_getCreditCards_sortBy() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sortBy", "Currency");
        params.add("page","0");

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/creditCards").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertAll(
                () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("EUR")),
                () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("USD"))
        );
    }

    @Test
    void get_getCreditCardById_isOk() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/creditCards/" + creditCardList.get(1).getId()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("145"));
    }

    @Test
    void get_getCreditCardById_isOk_null() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/creditCards/9999"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertEquals(mvcResult.getResponse().getContentAsString(), "");
    }

    @Test
    //native query testing does not work with the embedded in-memory database :(
    void get_getCreditCardByAnyParameter() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("minCreditLimit", "1200.00");
        params.add("maxCreditLimit", "1600.00");

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/creditCards/getByAnyParams").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Smart"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("Twain"));
    }

    @Test
    void post_createCreditCard_isCreated() throws Exception {
        //given
        Money money3 = new Money(new BigDecimal(2000), Currency.getInstance("USD"));
        CreditCard creditCard = new CreditCard(money3.getBalance(), money3.getCurrency(), accountHolderList.get(2),  accountHolderList.get(1), dataValidator.returnCurrentDate(),new BigDecimal("0.15"), new BigDecimal("100.00"), LocalDateTime.of(2021, 9, 23,23,50,16,016));
        String body = objectMapper.writeValueAsString(creditCard);

        //when
        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/creditCards").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("2000"));
    }

    @Test
    void post_createCreditCard_primaryOwner_null() throws Exception {
        //given
        Money money3 = new Money(new BigDecimal(1500), Currency.getInstance("EUR"));
        Checking checking = new Checking(money3.getBalance(), money3.getCurrency(), null,  accountHolderList.get(2), dataValidator.returnCurrentDate(),"678etry", AccountStatus.ACTIVE);
        String body = objectMapper.writeValueAsString(checking);

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        post("/api/v1/creditCards").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("400 BAD_REQUEST \"Credit card must have Primary Owner\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceByAddingInterests_noContent() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(1).getId() + "/addInterest"))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1552.96"), creditCardRepository.findById(creditCardList.get(1).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceByAddingInterests_notFound() throws Exception {
        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/creditCards/3000/addInterest"))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The credit card with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterPayment_negativeAmount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1000.00"), creditCardRepository.findById(creditCardList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_negativeAmount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(creditCardList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterPayment_notFound() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/creditCards/3000/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The credit card with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterPayment() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1500));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("-500.00"), creditCardRepository.findById(creditCardList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getSourceAccountType(), ReturnType.CREDIT_CARD),
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("1000.00"))
        );
    }

    @Test
    void patch_updateBalanceAfterDeposit_negativeAmount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1000.00"), creditCardRepository.findById(creditCardList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_negativeAmount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(creditCardList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterDeposit_notFound() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/creditCards/3000/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The credit card with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterDeposit() throws Exception {
        //given
        Money money = new Money(new BigDecimal(100));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(1).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1600.00"), creditCardRepository.findById(creditCardList.get(1).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(100));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getSourceAccountType(), ReturnType.CREDIT_CARD),
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("100.00"))
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_negativeAmount() throws Exception {
        ///given
        Money money = new Money(new BigDecimal(-300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", checkingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1000.00"), creditCardRepository.findById(creditCardList.get(0).getId()).get().getBalance()),
                () -> assertEquals(new BigDecimal("1000.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance())
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_negativeAmount_transactionNotSaved() throws Exception {
        ///given
        Money money = new Money(new BigDecimal(-300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", checkingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(creditCardList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterTransfer() throws Exception{
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", checkingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("700.00"), creditCardRepository.findById(creditCardList.get(0).getId()).get().getBalance());
        assertEquals(new BigDecimal("1300.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterTransfer_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", checkingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/creditCards/" + creditCardList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getSourceAccountType(), ReturnType.CREDIT_CARD),
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("300.00")),
                () -> assertEquals(transactionRepository.findById(creditCardList.get(0).getId()).get().getTargetAccountType(), ReturnType.CHECKING)
        );
    }

    @Test
    void delete_deleteCreditCard() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/creditCards/" + creditCardList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertFalse(mvcResult.getResponse().getContentAsString().contains("Warsaw"));
    }
}