package com.machines.capnation.domain;

import org.junit.jupiter.api.Test;

public class CapTest {

    @Test
    void create_new_class() {
        Cap cap = new Cap.CapBuilder(1, CapStyle.DAD_CAP, "Blue", "Nike", 300.0, CapSize.LARGE, 4)
                .setGender(Gender.FEMALE)
                .build();
        System.out.println(cap.toString());
    }
}
