package com.machines.capnation.model;

import java.util.Objects;

public class Cap {
    private long id;
    private String brand;
    private CapStyle style;
    private String color;
    private String collaboration;
    private double price;
    private CapSize size;
    private Gender gender;
    private int stock;


    public static class CapBuilder {
        private long id;
        private String brand;
        private CapStyle style;
        private String color;
        private String collaboration;
        private double price;
        private CapSize size;
        private Gender gender;
        private int stock;

        public CapBuilder(CapStyle style, String color, String brand, double price, CapSize size, int stock) {
            this.brand = brand;
            this.style = style;
            this.color = color;
            this.price = price;
            this.stock = stock;
            this.size = size;
        }

        public Cap build() {
            return new Cap(id, brand, style, color, collaboration, price, size, gender, stock);
        }

        public CapBuilder setCollaboration(String collaboration) {
            this.collaboration = collaboration;
            return this;
        }

        public CapBuilder setGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public CapBuilder setId(Long id) {
            this.id = id;
            return this;
        }
    }

    @Override
    public String toString() {
        return "Cap{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", stile=" + style +
                ", color='" + color + '\'' +
                ", collaboration='" + collaboration + '\'' +
                ", price=" + price +
                ", size=" + size +
                ", gender=" + gender +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cap cap = (Cap) o;
        return id == cap.id && Double.compare(price, cap.price) == 0 && stock == cap.stock && Objects.equals(brand, cap.brand) && style == cap.style && Objects.equals(color, cap.color) && Objects.equals(collaboration, cap.collaboration) && size == cap.size && gender == cap.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, style, color, collaboration, price, size, gender, stock);
    }

    private Cap(long id, String brand, CapStyle style, String color, String collaboration, double price, CapSize size, Gender gender, int stock) {
        this.id = id;
        this.brand = brand;
        this.style = style;
        this.color = color;
        this.collaboration = collaboration;
        this.price = price;
        this.size = size;
        this.gender = gender;
        this.stock = stock;
    }

    public boolean similar(Cap cap) {
        return Double.compare(price, cap.price) == 0 && stock == cap.stock && Objects.equals(brand, cap.brand) && style == cap.style && Objects.equals(color, cap.color) && Objects.equals(collaboration, cap.collaboration) && size == cap.size && gender == cap.gender;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public CapStyle getStyle() {
        return style;
    }

    public void setStyle(CapStyle style) {
        this.style = style;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCollaboration() {
        return collaboration;
    }

    public void setCollaboration(String collaboration) {
        this.collaboration = collaboration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CapSize getSize() {
        return size;
    }

    public void setSize(CapSize size) {
        this.size = size;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
