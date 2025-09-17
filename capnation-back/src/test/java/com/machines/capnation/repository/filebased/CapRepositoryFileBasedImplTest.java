package com.machines.capnation.repository.filebased;

import com.machines.capnation.model.Cap;
import com.machines.capnation.model.CapSize;
import com.machines.capnation.model.CapStyle;
import com.machines.capnation.repository.CapRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapRepositoryFileBasedImplTest {
    private final CapRepository repository = new CapRepositoryFileBasedImpl();

    @Test
    void check_database() {
        repository.findAll().forEach(System.out::println);
    }

    @Test
    void check_after_append_new_cap() {
        Cap expected = new Cap.CapBuilder(CapStyle.FLAT_CAP, "Red", "nike", 40000.0, CapSize.SMALL, 3)
                .setId(1L)
                .build();
        Cap actual = repository.save(expected);
        assertEquals(expected, actual);

        repository.findAll().forEach(System.out::println);
    }

}