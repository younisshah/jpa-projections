package io.younis.jpa.projections;

import io.younis.jpa.projections.entity.*;
import io.younis.jpa.projections.entity.join.Event;
import io.younis.jpa.projections.entity.join.EventType;
import io.younis.jpa.projections.entity.join.NotificationTemplate;
import io.younis.jpa.projections.entity.specification.UserSpecifications;
import io.younis.jpa.projections.model.CarSearchCriteria;
import io.younis.jpa.projections.respository.*;
import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaProjectionsApplication.class)
@ActiveProfiles(profiles = "test")
class JpaProjectionsApplicationTests {

    private static final Supplier<AssertionFailedError> NO_RECORD_SUPPLIER = () -> new AssertionFailedError("there's supposed to a record");
    private CustomerRepository customerRepository;
    private NotificationTemplateRepository notificationTemplateRepository;
    private EventRepository eventRepository;
    private EventTypeRepository eventTypeRepository;
    private UserRepository userRepository;
    private CarColorRepository carColorRepository;
    private CarRepository carRepository;

    @Autowired
    public JpaProjectionsApplicationTests(
            CustomerRepository customerRepository,
            NotificationTemplateRepository notificationTemplateRepository,
            EventRepository eventRepository,
            EventTypeRepository eventTypeRepository,
            UserRepository userRepository,
            CarRepository carRepository,
            CarColorRepository carColorRepository
    ) {
        this.customerRepository = customerRepository;
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.userRepository = userRepository;
        this.carColorRepository = carColorRepository;
        this.carRepository = carRepository;
    }

    @DisplayName("Test customer")
    @Test
    void customerTest() {

        //@formatter:off
        var account = Account.builder()
                .accountNumber("378")
                .build();

        var customer = Customer.builder()
                .firstName("tQNCUyqAuw")
                .lastName("VWXidPNEGb")
                .email("wkxqKPkG@mail.com")
                .account(account)
                .build();

        var creditCard = CreditCard.builder()
                .number("79240234")
                .creditCardType(CreditCard.CreditCardType.VISA)
                .build();

        var address = Address.builder()
                .city("jkfSeOdzFOmSfOK")
                .street1("VZntSVruydoffV")
                .street2("ibLCiuriTeWIBMS")
                .state("TBObktRXXeYIoS")
                .zipCode(924)
                .addressType(Address.AddressType.HOME)
                .build();

        customer.getAccount().getCards().add(creditCard);
        customer.getAccount().getAddresses().add(address);
        //@formatter:on

        customer = customerRepository.save(customer);
        var persistedCustomer = customerRepository.findById(customer.getId()).orElseThrow(NO_RECORD_SUPPLIER);

        Assertions.assertNotNull(persistedCustomer.getAccount());
        Assertions.assertNotNull(persistedCustomer.getCreatedAt());
        Assertions.assertNotNull(persistedCustomer.getLastModified());

        var nameOnly = customerRepository.findCustomerNameByEmail("wkxqKPkG@mail.com");
        var firstName = nameOnly.getFirstName();
        var lastName = nameOnly.getLastName();
        Assertions.assertEquals("tQNCUyqAuw", firstName);
        Assertions.assertEquals("VWXidPNEGb", lastName);

        var nameEmail = customerRepository.findNameEmailById(customer.getId());
        Assertions.assertEquals("tQNCUyqAuw", nameEmail.getFirstName());
        Assertions.assertEquals("wkxqKPkG@mail.com", nameEmail.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"EVT0001"})
    public void joinTest(String eventCode) {

        var event = new Event(eventCode, "SRV123", "YQbRAAlVLQCVOjFb");

        var eventType = EventType.builder()
                .eventTypeName("SMS")
                .eventCode(eventCode)
                .eventName("EpzwKTMLoh")
                .build();

        eventRepository.save(event);
        eventTypeRepository.save(eventType);

        var notificationTemplate = NotificationTemplate.builder()
                .eventTypeId(eventType.getEventTypeId())
                .enContent("Some content in English")
                .idContent("Some content in Bahasa")
                .idTitle("Some title in Bahasa")
                .enTitle("Some title in English")
                .build();

        notificationTemplateRepository.saveAndFlush(notificationTemplate);

        var smsTemplateView = notificationTemplateRepository.getSmsTemplateByEventCode(eventCode);
        Assertions.assertNotNull(smsTemplateView);
        Assertions.assertEquals("Some content in English", smsTemplateView.getEnContent());
        Assertions.assertEquals("Some content in Bahasa", smsTemplateView.getIdContent());
        Assertions.assertEquals("Some title in Bahasa", smsTemplateView.getIdTitle());
        Assertions.assertEquals("Some title in English", smsTemplateView.getEnTitle());
    }

    @Test
    public void exampleTest() {

        userRepository.saveAll(Arrays.asList(
                new User("John", "Doe", "john@doe.com"),
                new User("Jane", "Doe", "jane@doe.com")));

        String firstName = null;
        String lastName = "dOe";

        var matcher = ExampleMatcher
                .matchingAll()
                //.withIgnoreCase("lastName") // OR
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        var exampleUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();

        List<User> allUsers = userRepository.findAll(Example.of(exampleUser, matcher));
        Assertions.assertEquals(2, allUsers.size());
    }

    @ParameterizedTest
    @MethodSource("args")
    public void specificationTest(String firstName, String lastName) {

        userRepository.saveAll(Arrays.asList(
                new User("John", "Doe", "john@doe.com"),
                new User("Jane", "Doe", "jane@doe.com"),
                new User("Job", "Rogers", "bob@r.com"),
                new User("Mark", "Down", "down@txt.com")
        ));

        Specification<User> spec = Specification.where(firstName == null ? null : UserSpecifications.firstNameContains(firstName))
                .and(lastName == null ? null : UserSpecifications.lastNameContains(lastName));

        List<User> all = userRepository.findAll(spec);
        Assertions.assertEquals(2, all.size());

        all = userRepository.findAll(Specification.where(UserSpecifications.lastNameIn("Doe", "Down")));
    }

    @Test
    public void testCriteriaQuery() {
        List<CarColor> carColors = carColorRepository.saveAll(Arrays.asList(
                new CarColor("Red", "RD001"),
                new CarColor("Black", "BK001")
        ));

        var redMazda = Car.builder()
                .name("Mazda 6")
                .desc("Mazda is a very fast car")
                .manufacturer("Mazda")
                .year("2011")
                .colorCodeId(carColors.get(0).getId())
                .build();

        var blackMazda = Car.builder()
                .name("Mazda 6")
                .desc("Mazda is a very fast car")
                .manufacturer("Mazda")
                .year("2011")
                .colorCodeId(carColors.get(1).getId())
                .build();

        var bmw = Car.builder()
                .name("BMW i3")
                .desc("BMW is a very nice car")
                .manufacturer("BMW")
                .year("2013")
                .colorCodeId(carColors.get(0).getId())
                .build();

        carRepository.save(redMazda);
        carRepository.save(blackMazda);
        carRepository.save(bmw);

        var carSearch = CarSearchCriteria.builder()
                .carName("Mazda 6")
                .build();

        List<CarRow> byCarRowCommand = carRepository.findByCarSearchCommand(carSearch);
        Assertions.assertEquals(2, byCarRowCommand.size());

        List<CarRow> carRows = carRepository.findByColorCode("RD001");
        Assertions.assertEquals(2, carRows.size());

        List<CarRow> rawCarRows = carRepository.findByColorCodeRaw("RD001");
        Assertions.assertEquals(2, rawCarRows.size());

        List<CarRow> byColorCodeConstructorMapping = carRepository.findByColorCodeConstructorMapping("RD001");
        Assertions.assertEquals(2, byColorCodeConstructorMapping.size());

        List<CarRow> byColorCodeConstructorMappingXml = carRepository.findByColorCodeConstructorMappingXml("RD001");
        Assertions.assertEquals(2, byColorCodeConstructorMappingXml.size());

        var csc = CarSearchCriteria.builder()
                .colorCode("RD001")
                .pageNumber(0)
                .pageSize(10)
                .build();
        List<CarRow> byColorCodeConvenienceMap = carRepository.findByColorCodeConvenienceMap(csc);
        Assertions.assertEquals(0, byColorCodeConvenienceMap.size());
    }

    @Test
    public void str() {

        var carSearchCommand = CarSearchCriteria.builder()
                .carName("Mazda")
                .build();

        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(carSearchCommand.getCarName())) {
            params.put("carName", carSearchCommand.getCarName());
        }
        if (!StringUtils.isEmpty(carSearchCommand.getColorCode())) {
            params.put("colorCode", carSearchCommand.getColorCode());
        }
        if (!StringUtils.isEmpty(carSearchCommand.getYear())) {
            params.put("manufacture_year", carSearchCommand.getYear());
        }
        if (!StringUtils.isEmpty(carSearchCommand.getDesc())) {
            params.put("description", carSearchCommand.getDesc());
        }

        String sql = "select" +
                " c.NAME," +
                " cc.COLOR_CODE," +
                " cc.COLOR," +
                " c.DESCRIPTION," +
                " c.MANUFACTURE_YEAR" +
                " from CAR as c" +
                " INNER JOIN CAR_COLOR CC on c.COLOR_CODE_ID = CC.ID";

        StringBuilder queryBuilder = new StringBuilder(sql.length());
        queryBuilder.append(sql);
        boolean queryBuildStarted = false;

        for (String key : params.keySet()) {
            if (!queryBuildStarted) {
                queryBuilder.append(" WHERE ");
                queryBuildStarted = true;
            } else {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(key);
            queryBuilder.append(" = :");
            queryBuilder.append(key);
        }

        String finalSql = queryBuilder.toString();
        System.out.println(finalSql);
    }

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(null, "Doe"),
                Arguments.of("Jane", null)
        );
    }
}
