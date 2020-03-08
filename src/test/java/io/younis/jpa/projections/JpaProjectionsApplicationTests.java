package io.younis.jpa.projections;

import io.younis.jpa.projections.entity.*;
import io.younis.jpa.projections.respository.CustomerRepository;
import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Supplier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaProjectionsApplication.class)
@ActiveProfiles(profiles = "test")
class JpaProjectionsApplicationTests {

    private static final Supplier<AssertionFailedError> NO_RECORD_SUPPLIER = () -> new AssertionFailedError("there's supposed to a record");
    private CustomerRepository customerRepository;

    @Autowired
    public JpaProjectionsApplicationTests(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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

}
