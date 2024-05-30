package dev.codescreen;

import dev.codescreen.command.api.data.UserRepository;
import dev.codescreen.command.api.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private static HttpHeaders headers;

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser_WithCorrectParams() throws Exception {
        // Prepare request body
        UserRestModel userRestModel = new UserRestModel(
                "ABC123",
                "120",
                "USD"
        );

        ResponseEntity<CreateUserResponseModel> responseEntity = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testCreditAmount_WithCorrectParams() throws Exception {
        UserRestModel userRestModel = new UserRestModel(
                "ABC123",
                "221",
                "USD"
        );

        ResponseEntity<CreateUserResponseModel> responseEntity = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);
        String userId = "";
        if (responseEntity.getBody() != null) {
            userId = responseEntity.getBody().getUserId();
        }

        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC124",
                userId,
                new TransactionAmount("223", "USD", DebitOrCredit.CREDIT)
        );

        ResponseEntity<CreditResponseModel> creditResponseEntity = restTemplate.postForEntity(createURL("/load/ABC124"), creditRestModel, CreditResponseModel.class);
        assertEquals(HttpStatus.CREATED, creditResponseEntity.getStatusCode());
    }

    @Test
    public void testDebitAmount_WithCorrectParams() throws Exception {
        UserRestModel userRestModel = new UserRestModel(
                "ABC123",
                "123",
                "USD"
        );

        ResponseEntity<CreateUserResponseModel> responseEntity = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);
        String userId = "";
        if (responseEntity.getBody() != null) {
            userId = responseEntity.getBody().getUserId();
        }

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC124",
                userId,
                new TransactionAmount("223", "USD", DebitOrCredit.DEBIT)
        );

        ResponseEntity<DebitResponseModel> debitResponseEntity = restTemplate.postForEntity(createURL("/authorization/ABC124"), debitRestModel, DebitResponseModel.class);
        assertEquals(HttpStatus.CREATED, debitResponseEntity.getStatusCode());
    }

    @Test
    public void testCreditDebit_WithWrongUserId() throws Exception {

        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC124",
                "762e81bc-cac8-4b1-b887-ccba5a6bf2c9",
                new TransactionAmount("223", "USD", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC124",
                "762e81bc-cac8-4b1-b887-ccba5a6bf2c9",
                new TransactionAmount("223", "USD", DebitOrCredit.DEBIT)
        );

        ResponseEntity<CreditResponseModel> creditResponseEntity = restTemplate.postForEntity(createURL("/load/ABC124"), creditRestModel, CreditResponseModel.class);
        ResponseEntity<DebitResponseModel> debitResponseEntity = restTemplate.postForEntity(createURL("/authorization/ABC124"), debitRestModel, DebitResponseModel.class);
        assertEquals(HttpStatus.NOT_FOUND, creditResponseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, debitResponseEntity.getStatusCode());
    }

    @Test
    public void testUserCreateCreditDebit_WithWrongMessageId() throws Exception {

        UserRestModel userRestModel = new UserRestModel(
                "ABC122",
                "123",
                "USD"
        );

        ResponseEntity<CreateUserResponseModel> userResponseEntityAdditional = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);

        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                userResponseEntityAdditional.getBody().getUserId(),
                new TransactionAmount("223", "USD", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC124",
                userResponseEntityAdditional.getBody().getUserId(),
                new TransactionAmount("223", "USD", DebitOrCredit.DEBIT)
        );

        ResponseEntity<CreateUserResponseModel> userResponseEntity = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);
        ResponseEntity<CreditResponseModel> creditResponseEntity = restTemplate.postForEntity(createURL("/load/ABC123"), creditRestModel, CreditResponseModel.class);
        ResponseEntity<DebitResponseModel> debitResponseEntity = restTemplate.postForEntity(createURL("/authorization/ABC125"), debitRestModel, DebitResponseModel.class);
        assertEquals(HttpStatus.BAD_REQUEST, userResponseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, creditResponseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, debitResponseEntity.getStatusCode());
    }

    @Test
    public void testUserCreateCreditDebit_WithInvalidAmount() throws Exception {

        UserRestModel userRestModel = new UserRestModel(
                "ABC122",
                "-123",
                "USD"
        );

        UserRestModel userRestModelAdditional = new UserRestModel(
                "ABC122",
                "225",
                "USD"
        );
        ResponseEntity<CreateUserResponseModel> userResponseEntityAdditional = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModelAdditional, CreateUserResponseModel.class);

        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC124",
                userResponseEntityAdditional.getBody().getUserId(),
                new TransactionAmount("0", "USD", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC125",
                userResponseEntityAdditional.getBody().getUserId(),
                new TransactionAmount("-223", "USD", DebitOrCredit.DEBIT)
        );

        ResponseEntity<CreateUserResponseModel> userResponseEntity = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);
        ResponseEntity<CreditResponseModel> creditResponseEntity = restTemplate.postForEntity(createURL("/load/ABC124"), creditRestModel, CreditResponseModel.class);
        ResponseEntity<DebitResponseModel> debitResponseEntity = restTemplate.postForEntity(createURL("/authorization/ABC125"), debitRestModel, DebitResponseModel.class);
        assertEquals(HttpStatus.BAD_REQUEST, userResponseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, creditResponseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, debitResponseEntity.getStatusCode());
    }

    @Test
    public void testCreditDebit_WithWrongTransactionType() throws Exception {

        UserRestModel userRestModel = new UserRestModel(
                "ABC122",
                "123",
                "USD"
        );

        ResponseEntity<CreateUserResponseModel> userResponseEntityAdditional = restTemplate.postForEntity(createURL("/create/ABC123"), userRestModel, CreateUserResponseModel.class);

        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC124",
                userResponseEntityAdditional.getBody().getUserId(),
                new TransactionAmount("12", "USD", DebitOrCredit.DEBIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC125",
                userResponseEntityAdditional.getBody().getUserId(),
                new TransactionAmount("24", "USD", DebitOrCredit.CREDIT)
        );

        ResponseEntity<CreditResponseModel> creditResponseEntity = restTemplate.postForEntity(createURL("/load/ABC124"), creditRestModel, CreditResponseModel.class);
        ResponseEntity<DebitResponseModel> debitResponseEntity = restTemplate.postForEntity(createURL("/authorization/ABC125"), debitRestModel, DebitResponseModel.class);
        assertEquals(HttpStatus.BAD_REQUEST, creditResponseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, debitResponseEntity.getStatusCode());
    }

    private String createURL(String uri) {
        return "http://localhost:" + port + uri;
    }

}
