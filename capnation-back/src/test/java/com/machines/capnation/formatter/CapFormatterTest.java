package com.machines.capnation.formatter;

import com.machines.capnation.model.Cap;
import com.machines.capnation.model.CapSize;
import com.machines.capnation.model.CapStyle;
import com.machines.capnation.model.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CapFormatterTest {
    private CapFormatter formatter = new CapFormatter();

    @Test
    void format_cap_with_nul_gender_and_collaboration() {
        Cap cap = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 45000.0, CapSize.LARGE, 3)
                .build();
        String expected = "1,BASEBALL_CAP,Black,addidas,-,45000.0,LARGE,-,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void format_cap_with_nul_collaboration() {
        Cap cap = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 45000.0, CapSize.LARGE, 3)
                .setGender(Gender.MALE)
                .build();

        String expected = "1,BASEBALL_CAP,Black,addidas,-,45000.0,LARGE,MALE,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void format_cap_with_nul_gender() {
        Cap cap = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 45000.0, CapSize.LARGE, 3)
                .setCollaboration("BMW")
                .build();

        String expected = "1,BASEBALL_CAP,Black,addidas,BMW,45000.0,LARGE,-,3";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void format_cap_with_no_null_parameters() {
        Cap cap = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "black", "addidas", 30000.0, CapSize.LARGE, 4)
                .setCollaboration("bmw")
                .setGender(Gender.MALE)
                .build();

        String expected = "1,BASEBALL_CAP,black,addidas,bmw,30000.0,LARGE,MALE,4";
        String actual = formatter.capToText(cap);
        assertEquals(expected, actual);
    }

    @Test
    void create_line_with_no_null_values() {
        String line = "1,BASEBALL_CAP,Black,addidas,BMW,3000.0,LARGE,MALE,3";
        Cap expected = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 3000.0, CapSize.LARGE, 3)
                .setCollaboration("BMW")
                .setGender(Gender.MALE)
                .build();

        Cap actual = formatter.TextToCap(line);

        assertEquals(expected, actual);
    }

    @Test
    void create_cap_form_line_with_no_gender() {
        String line = "1,BASEBALL_CAP,Black,addidas,BMW,300.0,LARGE,-,3";
        Cap expected = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 300.0, CapSize.LARGE, 3)
                .setCollaboration("BMW")
                .build();

        Cap actual = formatter.TextToCap(line);

        assertEquals(expected, actual);
    }

    @Test
    void create_cap_from_line_with_no_collaboration() {
        String line = "1,BASEBALL_CAP,Black,addidas,-,300.0,LARGE,MALE,3";
        var expected = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 300.0, CapSize.LARGE, 3)
                .setGender(Gender.MALE)
                .build();

        var actual = formatter.TextToCap(line);

        assertEquals(expected, actual);
    }

    @Test
    void create_cap_from_line_with_no_collaboration_nor_gender() {
        String line = "1,BASEBALL_CAP,Black,addidas,-,300.0,LARGE,-,3";
        var expected = new Cap.CapBuilder(1, CapStyle.BASEBALL_CAP, "Black", "addidas", 300.0, CapSize.LARGE, 3)
                .build();

        var actual = formatter.TextToCap(line);

        assertEquals(expected, actual);
    }
}

