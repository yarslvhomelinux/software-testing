package com.liroykaz.testing;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class PhoneNumberValidator implements Predicate<String> {

    @Override
    public boolean test(String phoneNumber) {
        if (phoneNumber.startsWith("+44") && phoneNumber.length() == 13) {
            return true;
        }
        return false;
    }
}
