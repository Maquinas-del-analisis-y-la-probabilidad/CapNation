package com.machines.capnation.formatter;

import com.machines.capnation.domain.Cap;
import com.machines.capnation.domain.CapSize;
import com.machines.capnation.domain.CapStyle;
import com.machines.capnation.domain.Gender;

public class CapFormatter {
    private static final String separator = ",";

    /***
     * It takes an instance of a cap class and returns his representation in database so:
     * id|style|color|brand|collaboration|price|size|gender|stock
     * @param cap: Instance of a Cap
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

    /***
     * It takes a line in this format: id|style|color|brand|collaboration|price|size|gender|stock
     * and convert it to an instance of Cap class
     * @param line that represents the cap in file database
     * @return an instance of the clas Cap with the values of the line
     */
    public Cap TextToCap(String line) {
        String[] attributes = line.split(separator);
        Cap cap = new Cap.CapBuilder(
                Long.parseLong(attributes[0]),
                attributes[1],
                CapStyle.valueOf(attributes[2]),
                attributes[3], Double.parseDouble(attributes[4]),
                Integer.parseInt(attributes[5]),
                CapSize.valueOf(attributes[6])
        ).build();

        return cap

    }
}
