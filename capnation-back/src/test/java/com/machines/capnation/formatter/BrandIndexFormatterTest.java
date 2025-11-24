package com.machines.capnation.formatter;

import com.machines.capnation.model.index.BrandIndex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BrandIndexFormatterTest {
    private BrandIndexFormatter formatter = new BrandIndexFormatter();

    @Test
    void brand_index_to_text() {
        var expected = "addidas,1,2,3,4";
        var index = new BrandIndex.BrandIndexBuilder()
                .setBrand("addidas")
                .setCaps(List.of(1L, 2L, 3L, 4L))
                .build();

        assertEquals(expected, formatter.brandIndexToText(index));
    }

    @Test
    void text_to_brand_index() {
        var string = "addidas,1,2,3,4";
        var expected = new BrandIndex.BrandIndexBuilder()
                .setBrand("addidas")
                .setCaps(List.of(1L, 2L, 3L, 4L))
                .build();

        assertEquals(expected, formatter.textToBrandIndex(string));
    }
}