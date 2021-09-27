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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CheckingControllerTest {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Checking> checkingList;
    private List<StudentChecking> studentCheckingList;
    private List<AccountHolder> accountHolderList;
    private List<Address> addressList;
    private Money money;
    private Money money2;
    private User user;

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

        checkingList = checkingRepository.saveAll(List.of(
                new Checking(money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"345FGH", AccountStatus.ACTIVE),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.FROZEN),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE, LocalDateTime.of(2021, 7, 23,23,50,16,016)),
                new Checking(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE, LocalDateTime.of(2021, 8, 23,23,50,16,016))
        ));

        studentCheckingList = studentCheckingRepository.saveAll(List.of(
                new StudentChecking(money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"345FGH", AccountStatus.ACTIVE),
                new StudentChecking (money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE),
                new StudentChecking (money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.FROZEN)
        ));

    }

    @AfterEach
    void tearDown() {
        //checkingRepository.deleteAll();
    }

    @Test
    void get_getCheckingAccounts() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/checkingAccounts"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("username2"));
        assertAll(
                () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("USD")),
                () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("EUR"))
        );
    }

    @Test
    void get_getCheckingAccounts_sortBy() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sortBy", "Currency");
        params.add("page","0");

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/checkingAccounts").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertAll(
                () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("EUR")),
                () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("USD"))
        );
    }

    @Test
    void get_getCheckingAccountById_isOk() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/checkingAccounts/" + checkingList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Street1"));
    }

    @Test
    void get_getCheckingAccountById_isOk_null() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/checkingAccounts/9999"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertEquals(mvcResult.getResponse().getContentAsString(), "");
    }

    @Test
    void post_createCheckingAccount_isCreated() throws Exception {
        //given
        Money money3 = new Money(new BigDecimal(1500), Currency.getInstance("EUR"));
        Checking checking = new Checking(money3.getBalance(), money3.getCurrency(), accountHolderList.get(0),  accountHolderList.get(2), dataValidator.returnCurrentDate(),"678etry", AccountStatus.ACTIVE);
        String body = objectMapper.writeValueAsString(checking);

        //when
        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/checkingAccounts").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("678etry"));
    }

    @Test
    void post_createCheckingAccount_primaryOwner_null() throws Exception {
        //given
        Money money3 = new Money(new BigDecimal(1500), Currency.getInstance("EUR"));
        Checking checking = new Checking(money3.getBalance(), money3.getCurrency(), null,  accountHolderList.get(2), dataValidator.returnCurrentDate(),"678etry", AccountStatus.ACTIVE);
        String body = objectMapper.writeValueAsString(checking);

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        post("/api/v1/checkingAccounts").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("400 BAD_REQUEST \"Account must have Primary Owner\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    //does not work because of problem with authentication
    @Test
    @WithMockUser
    void patch_updateBalanceAfterPayment_negativeAmount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/makePayment")
                //.with(user("accountHolder1").password( "123456").roles("ACCOUNTHOLDER"))
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1000.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_negativeAmount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(checkingList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterPayment_notFound() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/checkingAccounts/3000/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The checking account with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterPayment_ActiveAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("-40.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_ActiveAccount_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getSourceAccountType(), ReturnType.CHECKING),
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("1040.00"))
        );
    }

    @Test
    void patch_updateBalanceAfterPayment_FrozenAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(2).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1500.00"), checkingRepository.findById(checkingList.get(2).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_FrozenAccount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(2).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(checkingList.get(2).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterDeposit_negativeAmount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1000.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_negativeAmount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(checkingList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterDeposit_notFound() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/checkingAccounts/3000/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The checking account with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterDeposit_ActiveAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1300.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_ActiveAccount_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getSourceAccountType(), ReturnType.CHECKING),
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("300.00"))
        );
    }

    @Test
    void patch_updateBalanceAfterDeposit_FrozenAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(2).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The checking account with the given id is frozen\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1500.00"), checkingRepository.findById(checkingList.get(2).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_FrozenAccount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(2).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The checking account with the given id is frozen\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(checkingList.get(2).getId()).isPresent());
    }

    @Test
    void patch_chargeMaintanenceFeeForAllOrAnyAccounts_byId() throws Exception{
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", checkingList.get(3).getId().toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/chargeFee").queryParams(params))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1476.00"), checkingRepository.findById(checkingList.get(3).getId()).get().getBalance()),
                () ->  assertEquals(new BigDecimal("1500.00"), checkingRepository.findById(checkingList.get(4).getId()).get().getBalance())
        );
    }

    @Test
    void patch_chargeMaintanenceFeeForAllOrAnyAccounts_byId_notFound() throws Exception{
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "50");

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/chargeFee").queryParams(params))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"The checking account with the given id does not exist\"", result.getResolvedException().getMessage()))
                .andReturn();
    }

    @Test
    void patch_chargeMaintanenceFeeForAllOrAnyAccounts_All() throws Exception{
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/chargeFee").queryParams(params))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1476.00"), checkingRepository.findById(checkingList.get(3).getId()).get().getBalance()),
                () ->  assertEquals(new BigDecimal("1488.00"), checkingRepository.findById(checkingList.get(4).getId()).get().getBalance())
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_negativeAmount() throws Exception {
        ///given
        Money money = new Money(new BigDecimal(-300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", studentCheckingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.STUDENT_CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1000.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance()),
                () -> assertEquals(new BigDecimal("1000.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance())
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_negativeAmount_transactionNotSaved() throws Exception {
        ///given
        Money money = new Money(new BigDecimal(-300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", studentCheckingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.STUDENT_CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(checkingList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterTransfer_ActiveAccount() throws Exception{
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", studentCheckingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.STUDENT_CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("700.00"), checkingRepository.findById(checkingList.get(0).getId()).get().getBalance());
        assertEquals(new BigDecimal("1300.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterTransfer_ActiveAccount_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", studentCheckingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.STUDENT_CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getSourceAccountType(), ReturnType.CHECKING),
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("300.00")),
                () -> assertEquals(transactionRepository.findById(checkingList.get(0).getId()).get().getTargetAccountType(), ReturnType.STUDENT_CHECKING)
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_FrozenAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", studentCheckingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.STUDENT_CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(2).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The checking account with the given id is frozen\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1500.00"), checkingRepository.findById(checkingList.get(2).getId()).get().getBalance()),
                () -> assertEquals(new BigDecimal("1000.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance())
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_FrozenAccount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", studentCheckingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.STUDENT_CHECKING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/checkingAccounts/" + checkingList.get(2).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The checking account with the given id is frozen\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(checkingList.get(2).getId()).isPresent());
    }

    @Test
    void delete_deleteCheckingAccount() throws Exception{
        //when
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/checkingAccounts/" + checkingList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();
        //then
        assertFalse(mvcResult.getResponse().getContentAsString().contains("password1"));
    }
}