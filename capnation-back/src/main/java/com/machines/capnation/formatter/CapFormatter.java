package com.machines.capnation.formatter;

import com.machines.capnation.model.Cap;
import com.machines.capnation.model.CapSize;
import com.machines.capnation.model.CapStyle;
import com.machines.capnation.model.Gender;

import java.util.Objects;

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
                .append(cap.getPrice()).append(separator)
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
        String[] att = line.split(separator);
        Cap cap = new Cap.CapBuilder(
                CapStyle.valueOf(att[1]),
                att[2],
                att[3],
                Double.parseDouble(att[5]),
                CapSize.valueOf(att[6]),
                Integer.parseInt(att[8])
        ).setId(Long.parseLong(att[0]))
                .build();

        if (!Objects.equals(att[4], "-")) {
            cap.setCollaboration(att[4]);
        }
        if (!Objects.equals(att[7], "-")) {
            cap.setGender(Gender.valueOf(att[7]));
        }
        return cap;

    }
}
