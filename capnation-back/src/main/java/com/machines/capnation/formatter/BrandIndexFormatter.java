package com.machines.capnation.formatter;

import com.machines.capnation.model.index.BrandIndex;

import java.util.Arrays;

public class BrandIndexFormatter {
    private static final String separator = ",";

    public String brandIndexToText(BrandIndex brandIndex) {
        var builder = new StringBuilder();
        builder.append(brandIndex.getBrand());
        builder.append(separator);

        for (int i = 0; i < brandIndex.getCaps().size(); i++) {
            builder.append(brandIndex.getCaps().get(i));

            if (i != (brandIndex.getCaps().size() - 1)) {
                builder.append(separator); // doesn't add the separator if is the last id
            }
        }
        return builder.toString();
    }


    public BrandIndex textToBrandIndex(String line) {
        var iterator = Arrays.stream(line.split(separator)).iterator();
        var brand = iterator.next();

        var brandIndex = new BrandIndex.BrandIndexBuilder()
                .setBrand(brand)
                .build();

        while (iterator.hasNext()) {
            brandIndex.appendCap(Long.parseLong(iterator.next()));
        }
        return brandIndex;
    }
}
