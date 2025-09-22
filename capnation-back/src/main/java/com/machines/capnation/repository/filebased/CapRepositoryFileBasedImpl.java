package com.machines.capnation.repository.filebased;

import com.machines.capnation.exceptions.CapDatabaseException;
import com.machines.capnation.formatter.CapFormatter;
import com.machines.capnation.model.Cap;
import com.machines.capnation.repository.CapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


/***
 * this class will be in charge of manage the file based database
 * constrains:
 *  - There is no two rows with the same ID
 *  - There is no two rows with similar characteristics (different id but the same in the another parameters)
 */
@Repository
public class CapRepositoryFileBasedImpl implements CapRepository {
    private static final CapFormatter formatter = new CapFormatter();

    @Value("file:${caps.file}")
    private Resource resource;

    private List<Cap> caps;


    @Override
    public List<Cap> findAll() {
        initializeList();
        return caps;
    }

    private void initializeList() {
        if (caps == null) {
            caps = readLines();
        }
    }


    @Override
    public Cap save(Cap cap) {
        initializeList();
        var similar = caps.stream().filter(cap::similar).findAny();

        if (similar.isEmpty()) { // there is not a cap similar to the entered
            cap.setId(currentId(caps) + 1);
            appendLine(formatter.capToText(cap));
            caps.add(cap);
            return cap;
        } else {
            throw new CapDatabaseException("There is other cap with similar characteristics");
        }
    }

    private long currentId(List<Cap> list) {
        return list.stream().mapToLong(Cap::getId).max().orElse(0);
    }

    private List<Cap> readLines() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().filter(line -> !line.isBlank()).map(formatter::TextToCap).collect(Collectors.toList());
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }

    private void appendLine(String line) {
        try (FileWriter writer = new FileWriter(resource.getFile(), true)) {
            writer.append("\n").append(line);
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }
}
