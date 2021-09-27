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
class StudentCheckingControllerTest {

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<StudentChecking> studentCheckingList;
    private List<Saving> savingList;
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

        studentCheckingList = studentCheckingRepository.saveAll(List.of(
                new StudentChecking (money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"345FGH", AccountStatus.ACTIVE),
                new StudentChecking (money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.ACTIVE),
                new StudentChecking (money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", AccountStatus.FROZEN)
        ));

        savingList = savingRepository.saveAll(List.of(
                new Saving(money.getBalance(), money.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"345FGH", LocalDateTime.of(2021, 7, 12,11,50,34,456), AccountStatus.ACTIVE),
                new Saving(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", LocalDateTime.of(2020, 6, 23,12,12,45,123), AccountStatus.ACTIVE),
                new Saving(money2.getBalance(), money2.getCurrency(), accountHolderList.get(2),  null, dataValidator.returnCurrentDate(),"765gffe", LocalDateTime.of(2020, 6, 23,12,12,45,123), AccountStatus.FROZEN)
        ));
    }

    @AfterEach
    void tearDown() {
        studentCheckingRepository.deleteAll();
    }

    @Test
    void get_getStudentCheckingAccounts() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/studentCheckingAccounts"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Maria"));
        assertAll(
                () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("USD")),
                () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("EUR"))
        );
    }

    @Test
    void get_getStudentCheckingAccounts_sortBy() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sortBy", "Currency");
        params.add("page","0");

        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/studentCheckingAccounts").queryParams(params))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertAll(
                () -> assertTrue(mvcResult.getResponse().getContentAsString().substring(0,50).contains("EUR")),
                () ->  assertFalse(mvcResult.getResponse().getContentAsString().substring(0,50).contains("USD"))
        );
    }

    @Test
    void get_getStudentCheckingAccountById_isOk() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(1).getId()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Spain"));
    }

    @Test
    void get_getStudentCheckingAccountById_isOk_null() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/studentCheckingAccounts/9999"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertEquals(mvcResult.getResponse().getContentAsString(), "");
    }

    @Test
    void post_createStudentCheckingAccount_isCreated() throws Exception {
        //given
        Money money3 = new Money(new BigDecimal(1500), Currency.getInstance("EUR"));
        StudentChecking studentChecking = new StudentChecking (money3.getBalance(), money3.getCurrency(), accountHolderList.get(0),  accountHolderList.get(1), dataValidator.returnCurrentDate(),"sdswe3443", AccountStatus.ACTIVE);
        String body = objectMapper.writeValueAsString(studentChecking);

        //when
        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/studentCheckingAccounts").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        assertTrue(mvcResult.getResponse().getContentAsString().contains("sdswe3443"));
    }

    @Test
    void post_createStudentCheckingAccount_primaryOwner_null() throws Exception {
        //given
        Money money3 = new Money(new BigDecimal(1500), Currency.getInstance("EUR"));
        Checking checking = new Checking(money3.getBalance(), money3.getCurrency(), null,  accountHolderList.get(2), dataValidator.returnCurrentDate(),"678etry", AccountStatus.ACTIVE);
        String body = objectMapper.writeValueAsString(checking);

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        post("/api/v1/studentCheckingAccounts").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("400 BAD_REQUEST \"Student checking account must have Primary Owner\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterPayment_negativeAmount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1000.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_negativeAmount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(studentCheckingList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterPayment_notFound() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/studentCheckingAccounts/3000/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The student checking account with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterPayment_ActiveAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(450));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("550.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_ActiveAccount_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(450));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getSourceAccountType(), ReturnType.STUDENT_CHECKING),
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("450.00"))
        );
    }

    @Test
    void patch_updateBalanceAfterPayment_FrozenAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(2).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1500.00"), studentCheckingRepository.findById(studentCheckingList.get(2).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterPayment_FrozenAccount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(2).getId() + "/makePayment").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(studentCheckingList.get(2).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterDeposit_negativeAmount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertEquals(new BigDecimal("1000.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_negativeAmount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(-1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(studentCheckingList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterDeposit_notFound() throws Exception {
        //given
        Money money = new Money(new BigDecimal(1000));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult =
                mockMvc.perform(
                        patch("/api/v1/studentCheckingAccounts/3000/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                        .andExpect(result -> assertEquals("404 NOT_FOUND \"The student checking account with the given id does not exist\"", result.getResolvedException().getMessage()))
                        .andReturn();
    }

    @Test
    void patch_updateBalanceAfterDeposit_ActiveAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1300.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_ActiveAccount_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getSourceAccountType(), ReturnType.STUDENT_CHECKING),
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("300.00"))
        );
    }

    @Test
    void patch_updateBalanceAfterDeposit_FrozenAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(2).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertEquals(new BigDecimal("1500.00"), studentCheckingRepository.findById(studentCheckingList.get(2).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterDeposit_FrozenAccount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(2).getId() + "/deposit").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(studentCheckingList.get(2).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterTransfer_negativeAmount() throws Exception {
        ///given
        Money money = new Money(new BigDecimal(-300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", savingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.SAVING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1000.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance()),
                () -> assertEquals(new BigDecimal("1000.00"), savingRepository.findById(savingList.get(0).getId()).get().getBalance())
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_negativeAmount_transactionNotSaved() throws Exception {
        ///given
        Money money = new Money(new BigDecimal(-300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", savingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.SAVING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The amount cannot be less then 0\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(studentCheckingList.get(0).getId()).isPresent());
    }

    @Test
    void patch_updateBalanceAfterTransfer_ActiveAccount() throws Exception{
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", savingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.SAVING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertEquals(new BigDecimal("700.00"), studentCheckingRepository.findById(studentCheckingList.get(0).getId()).get().getBalance());
        assertEquals(new BigDecimal("1300.00"), savingRepository.findById(savingList.get(0).getId()).get().getBalance());
    }

    @Test
    void patch_updateBalanceAfterTransfer_ActiveAccount_transactionSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", savingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.SAVING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getSourceAccountType(), ReturnType.STUDENT_CHECKING),
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getTransferredAmount(), new BigDecimal("300.00")),
                () -> assertEquals(transactionRepository.findById(studentCheckingList.get(0).getId()).get().getTargetAccountType(), ReturnType.SAVING)
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_FrozenAccount() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", savingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.SAVING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(2).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The student checking account with the given id is frozen\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertAll(
                () -> assertEquals(new BigDecimal("1500.00"), studentCheckingRepository.findById(studentCheckingList.get(2).getId()).get().getBalance()),
                () -> assertEquals(new BigDecimal("1000.00"), savingRepository.findById(savingList.get(0).getId()).get().getBalance())
        );
    }

    @Test
    void patch_updateBalanceAfterTransfer_FrozenAccount_transactionNotSaved() throws Exception {
        //given
        Money money = new Money(new BigDecimal(300));
        String body = objectMapper.writeValueAsString(money.getBalance());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("targetAccountId", savingList.get(0).getId().toString());
        params.add("targetAccountType", ReturnType.SAVING.toString());

        //when
        MvcResult mvcResult = mockMvc.perform(
                patch("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(2).getId() + "/transfer").queryParams(params).content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"The student checking account with the given id is frozen\"", result.getResolvedException().getMessage()))
                .andReturn();

        //then
        assertFalse(transactionRepository.findById(studentCheckingList.get(2).getId()).isPresent());
    }

    @Test
    void delete_deleteStudentCheckingAccount() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/studentCheckingAccounts/" + studentCheckingList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();
        //then
        assertFalse(mvcResult.getResponse().getContentAsString().contains("password1"));
    }
}