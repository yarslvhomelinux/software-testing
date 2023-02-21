package com.liroykaz.testing.utils;

import com.liroykaz.testing.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setup() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource(
            {
                    "+447000000000, TRUE",
                    "+447000000000787, FALSE",
                    "447000000000787, FALSE"
            }
    )
    void itShouldValidatePhoneNUmber(String phoneNumber, String expectedResult) {
        //when
        boolean isValid = underTest.test(phoneNumber);

        //then
        assertThat(isValid).isEqualTo(Boolean.valueOf(expectedResult));
    }

    @Test
    @DisplayName("Should fail when length bigger than 13")
    void itShouldValidatePhoneNUmberWhenIncorrect() {
        //given
        String phoneNumber = "+447000000000787";

        //when
        boolean isValid = underTest.test(phoneNumber);

        //then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should fail when does not with +4")
    void itShouldValidatePhoneNUmberWhenDoesNotStartWithPlus() {
        //given
        String phoneNumber = "447000000000787";

        //when
        boolean isValid = underTest.test(phoneNumber);

        //then
        assertThat(isValid).isFalse();
    }
}
