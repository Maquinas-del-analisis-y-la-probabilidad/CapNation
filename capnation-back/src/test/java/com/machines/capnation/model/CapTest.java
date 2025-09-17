package com.machines.capnation.model;

import org.junit.jupiter.api.Test;

public class CapTest {

    @Test
    void create_new_class() {
        Cap cap = new Cap.CapBuilder(CapStyle.DAD_CAP, "Blue", "Nike", 300.0, CapSize.LARGE, 4)
                .setId(1L)
                .setGender(Gender.FEMALE)
                .build();
        System.out.println(cap.toString());
    }
}
