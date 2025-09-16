package com.machines.capnation.domain;

import org.junit.jupiter.api.Test;

public class CapTest {

    @Test
    void create_new_class() {
        Cap cap = new Cap.CapBuilder(1, "Nike", CapStyle.DAD_CAP, "Blue", 300.0, 4, CapSize.LARGE)
                .setGender(Gender.FEMALE)
                .build();
        System.out.println(cap.toString());
    }
}
