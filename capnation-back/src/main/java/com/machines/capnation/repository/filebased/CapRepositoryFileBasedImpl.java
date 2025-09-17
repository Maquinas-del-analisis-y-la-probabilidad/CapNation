package com.machines.capnation.repository.filebased;

import com.machines.capnation.exceptions.CapDatabaseException;
import com.machines.capnation.formatter.CapFormatter;
import com.machines.capnation.model.Cap;
import com.machines.capnation.repository.CapRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.util.List;


/***
 * this class will be in charge of manage the file based database
 */
public class CapRepositoryFileBasedImpl implements CapRepository {
    private static final CapFormatter formatter = new CapFormatter();
    private static final String address = "classpath:caps.txt";

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public List<Cap> findAll() {
        try {
            Resource capFile = resourceLoader.getResource(address);
            BufferedReader reader = new BufferedReader(new InputStreamReader(capFile.getInputStream()));
            return reader.lines().map(formatter::TextToCap).toList();
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }

    @Override
    public Cap save(Cap cap) {
        appendLine(formatter.capToText(cap));
        return cap;
    }

    private void appendLine(String line) {
        var resource = resourceLoader.getResource(address);
        try (FileWriter writer = new FileWriter(resource.getFile(), true)) {
            writer.append("\n").append(line);
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }
}
