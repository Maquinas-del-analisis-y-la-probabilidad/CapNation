package com.machines.capnation.repository.filebased;

import com.machines.capnation.exceptions.CapDatabaseException;
import com.machines.capnation.formatter.CapFormatter;
import com.machines.capnation.model.Cap;
import com.machines.capnation.model.Index;
import com.machines.capnation.repository.CapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private Resource heap;

    @Value("file:${capIndex.file}")
    private Resource index;

    private List<Cap> capList = new ArrayList<>();

    private void initialize() {
        if (capList.isEmpty()) {
            capList = readLinesCap(heap);
        }
    }

    @Override
    public List<Cap> findAll() {
        initialize();
        return capList;
    }


    @Override
    public Cap save(Cap cap) {
        if (cap.getId() <= 0)
            throw new RuntimeException("The ID you've given is invalid");

        initialize();
        var exist = readLinesIndex(index).stream().anyMatch(idx -> idx.getKey() == cap.getId());
        if (!exist) {
            var index = new Index(cap.getId(), capList.size());
            capList.add(cap);
            addIndex(index);
            appendLine(formatter.capToText(cap), this.heap);
            return cap;
        }
        throw new RuntimeException(String.format("there is another cap with index %d", cap.getId()));
    }

    @Override
    public Cap findById(Long id) {
        var indexList = readLinesIndex(this.index);
        var idx = Collections.binarySearch(indexList.stream().map(Index::getKey).toList(), id);
        if (idx <= -1) throw new RuntimeException(String.format("There is not any cap with id %d", id));
        var positionInHeap = indexList.get(idx).getDirection();

        return capList.get(positionInHeap);
    }

    // read all lines into a resource file.
    private List<Cap> readLinesCap(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().filter(line -> !line.isBlank()).map(formatter::TextToCap).collect(Collectors.toList());
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }

    private List<Index> readLinesIndex(Resource resource) {
        try (BufferedReader reader = new BufferedReader((new InputStreamReader(resource.getInputStream())))) {
            return reader.lines().filter(line -> !line.isBlank()).map(Index::fromLine).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //append a line into a resource line
    private void appendLine(String line, Resource resource) {
        try (FileWriter writer = new FileWriter(resource.getFile(), true)) {
            writer.append("\n").append(line);
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }

    private void addIndex(Index index) {
        var indexList = readLinesIndex(this.index);

        int idx = Collections.binarySearch(indexList, index, Comparator.comparingLong(Index::getKey));
        if (idx < 0) {
            idx = -(idx + 1);
        }

        indexList.add(idx, index);

        var str = new StringBuilder();

        indexList.forEach(p -> str.append(String.format("%s\n", p.toLine())));

        try (FileWriter writer = new FileWriter(this.index.getFile(), false)) {
            writer.write(str.toString());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
