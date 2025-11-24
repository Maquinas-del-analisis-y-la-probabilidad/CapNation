package com.machines.capnation.model.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrandIndex {
    private String brand;
    private List<Long> caps;

    private BrandIndex(String brand, List<Long> caps) {
        this.brand = brand;
        this.caps = caps;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BrandIndex that = (BrandIndex) o;
        return Objects.equals(brand, that.brand) && Objects.equals(caps, that.caps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, caps);
    }

    public List<Long> getCaps() {
        return caps;
    }


    public String getBrand() {
        return brand;
    }

    public boolean appendCap(Long id) {
        return caps.add(id);
    }


    public static class BrandIndexBuilder {
        private String brand;
        private List<Long> caps;

        public BrandIndexBuilder() {
            this.brand = null;
            this.caps = new ArrayList<>();
        }

        public BrandIndex build() {
            return new BrandIndex(brand, caps);
        }

        public BrandIndexBuilder setBrand(String brand) {

            this.brand = brand.toLowerCase();
            return this;
        }

        public BrandIndexBuilder setCaps(List<Long> caps) {
            this.caps = caps;
            return this;
        }
    }
}


