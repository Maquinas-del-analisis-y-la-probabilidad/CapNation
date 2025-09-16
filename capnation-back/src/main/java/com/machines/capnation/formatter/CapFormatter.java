package com.machines.capnation.formatter;

import com.machines.capnation.domain.Cap;


public class CapFormatter {
    private static final String separator = ",";

    /***
     * @param cap: Instance of a Cap
     *           it takes the cap and return his representation in database, it'll look like this
     *           id|style|color|brand|collaboration|price|size|gender|stock
     * @return Text representation of a Cap in the text database
     */
    public String capToText(Cap cap) {
        return new StringBuilder().append(cap.getId()).append(separator)
                .append(cap.getStyle().toString()).append(separator)
                .append(cap.getColor()).append(separator)
                .append(cap.getBrand()).append(separator)
                .append((cap.getCollaboration() != null) ? cap.getCollaboration() : "-").append(separator)
                .append(cap.getSize().toString()).append(separator)
                .append((cap.getGender() != null) ? cap.getGender().toString() : "-").append(separator) // field to gender
                .append(cap.getStock()).toString();
    }

//    public Cap TextToCap(String line) {
//        String[] attributes = line.split(separator);
//
//        switch (attributes.length) {
//            case 7 -> {
//                return new Cap.CapBuilder(
//                        Long.parseLong(attributes[0]),
//                        attributes[1],
//                        CapStyle.valueOf(attributes[2]),
//                        attributes[3], Double.parseDouble(attributes[4]),
//                        Integer.parseInt(attributes[5]),
//                        CapSize.valueOf(attributes[6])
//                ).build();
//            }
//
//            case 8 -> {
//
//            }
//        }
//    }
}
