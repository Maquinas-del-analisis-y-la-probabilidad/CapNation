package com.machines.capnation.formatter;

import com.machines.capnation.domain.Cap;
import com.machines.capnation.domain.CapSize;
import com.machines.capnation.domain.CapStyle;
import com.machines.capnation.domain.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CapFormatterTest {
    private CapFormatter formatter = new CapFormatter();

    @Test
    void format_cap_with_nul_gender_and_collaboration() {
        Cap cap = new Cap.CapBuilder(1, "addidas", CapStyle.BASEBALL_CAP, "Black", 45000.0, 3, CapSize.LARGE).build();
        String expected = "1,BASEBALL_CAP,Black,addidas,-,LARGE,-,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void format_cap_with_nul_gender() {
        Cap cap = new Cap.CapBuilder(1, "addidas", CapStyle.BASEBALL_CAP, "Black", 45000.0, 3, CapSize.LARGE)
                .setCollaboration("BMW").build();
        String expected = "1,BASEBALL_CAP,Black,addidas,BMW,LARGE,-,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void format_cap_with_nul_collaboration() {
        Cap cap = new Cap.CapBuilder(1, "addidas", CapStyle.BASEBALL_CAP, "Black", 45000.0, 3, CapSize.LARGE)
                .setGender(Gender.MALE).build();
        String expected = "1,BASEBALL_CAP,Black,addidas,-,LARGE,MALE,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void format_cap_with_no_null_parameters() {
        Cap cap = new Cap.CapBuilder(1, "addidas", CapStyle.BASEBALL_CAP, "Black", 45000.0, 3, CapSize.LARGE)
                .setCollaboration("BMW")
                .setGender(Gender.MALE)
                .build();

        String expected = "1,BASEBALL_CAP,Black,addidas,BMW,LARGE,MALE,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }
}

