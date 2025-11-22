package com.machines.capnation.repository.filebased;

import com.machines.capnation.exceptions.CapDatabaseException;
import com.machines.capnation.formatter.BrandIndexFormatter;
import com.machines.capnation.formatter.CapFormatter;
import com.machines.capnation.model.Cap;
import com.machines.capnation.model.Index;
import com.machines.capnation.model.index.BrandIndex;
import com.machines.capnation.repository.CapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final CapFormatter CAP_FORMATTER = new CapFormatter();
    private static final BrandIndexFormatter BRAND_INDEX_FORMATTER = new BrandIndexFormatter();
    private static final Logger log = LoggerFactory.getLogger(CapRepositoryFileBasedImpl.class);

    @Value("file:${caps.file}")
    private Resource heap;

    @Value("file:${capIndex.file}")
    private Resource index;

    @Value("file:${brandIndex.file}")
    private Resource brandIndexFile;

    private List<Cap> capList = new ArrayList<>();
    private List<BrandIndex> brandList = new ArrayList<>();

    private void initialize() {
        if (capList.isEmpty()) {
            capList = readLinesCap(heap);
        }
        if (brandList.isEmpty()) {
            brandList = readLinesBrandIndex(brandIndexFile);
        }
    }

    @Override
    public List<Cap> findAll() {
        initialize();
        return capList;
    }


    private boolean existBrand(String brand) {

        for (BrandIndex index : brandList) {
            if (index.getBrand().equals(brand.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Cap save(Cap cap) {
        if (cap.getId() <= 0)
            throw new RuntimeException("The ID you've given is invalid");

        initialize();
        var exist = readLinesIndex(index).stream().anyMatch(idx -> idx.getKey() == cap.getId());
        if (!exist) {
            var index = new Index(cap.getId(), capList.size());
            boolean existBrand = this.existBrand(cap.getBrand());
            if (!existBrand) {
                var brandIndex = new BrandIndex.BrandIndexBuilder()
                        .setBrand(cap.getBrand())
                        .build();
                brandIndex.appendCap(cap.getId());

                brandList.add(brandIndex);
                appendLine(BRAND_INDEX_FORMATTER.brandIndexToText(brandIndex), brandIndexFile);
            } else {
                // add the new id to the respective brand index
                brandList.stream()
                        .filter(p -> p.getBrand().equals(cap.getBrand().toLowerCase()))
                        .findFirst()
                        .orElseThrow()
                        .appendCap(cap.getId());

                // overwrite the file of the brand index
                var lines = new StringBuilder();
                brandList.forEach(p -> {
                    lines.append(BRAND_INDEX_FORMATTER.brandIndexToText(p));
                    lines.append('\n');
                });
                overwrite(brandIndexFile, lines.toString());
            }
            capList.add(cap);
            addIndex(index);
            appendLine(CAP_FORMATTER.capToText(cap), this.heap);
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

    @Override
    public List<Cap> findByBrand(String brand) {
        initialize();

        var exist = existBrand(brand);
        if (!exist) {
            throw new RuntimeException(String.format("There is not any cap with brand: %s", brand));
        }
        var capsId = brandList.stream().filter(p -> p.getBrand().equals(brand.toLowerCase()))
                .findFirst()
                .orElseThrow()
                .getCaps();

        return capsId.stream().map(this::findById).collect(Collectors.toList());
    }

    // read all lines into a resource file.
    private List<Cap> readLinesCap(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().filter(line -> !line.isBlank()).map(CAP_FORMATTER::TextToCap).collect(Collectors.toList());
        } catch (IOException e) {
            throw new CapDatabaseException(e.getMessage());
        }
    }

    private List<BrandIndex> readLinesBrandIndex(Resource resource) {
        try (BufferedReader reader = new BufferedReader((new InputStreamReader(resource.getInputStream())))) {
            return reader.lines().filter(line -> !line.isBlank()).map(BRAND_INDEX_FORMATTER::textToBrandIndex).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
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

    // overwrite a file
    private void overwrite(Resource resource, String lines) {
        try (FileWriter writer = new FileWriter(resource.getFile(), false)) {
            writer.write(lines.toString());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
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
        overwrite(this.index, str.toString());
    }
}
