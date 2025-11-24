package com.machines.capnation.repository.filebased;

import com.machines.capnation.exceptions.CapDatabaseException;
import com.machines.capnation.formatter.BrandIndexFormatter;
import com.machines.capnation.formatter.CapFormatter;
import com.machines.capnation.model.Cap;
import com.machines.capnation.model.DataStructures.BPlusTree;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * File-based repository implementation using B+ tree for efficient cap data management.
 * <p>
 * This class manages a file-based database with the following constraints:
 * - No two rows with the same ID
 * - No two rows with similar characteristics (different ID but same parameters)
 * <p>
 * The B+ tree is used as the primary index structure mapping cap IDs to their positions
 * in the data file, enabling O(log n) lookups.
 */
@Repository
public class CapRepositoryFileBasedImpl implements CapRepository {
    private static final CapFormatter CAP_FORMATTER = new CapFormatter();
    private static final BrandIndexFormatter BRAND_INDEX_FORMATTER = new BrandIndexFormatter();
    private static final Logger log = LoggerFactory.getLogger(CapRepositoryFileBasedImpl.class);

    @Value("file:${caps.file}")
    private Resource heap;

    @Value("file:${capIndex.file}")
    private Resource indexFile;

    @Value("file:${brandIndex.file}")
    private Resource brandIndexFile;

    private List<Cap> capList = new ArrayList<>();
    private BPlusTree<Long, Integer> capIndex = new BPlusTree<>(4); // B+ tree of order 4
    private List<BrandIndex> brandList = new ArrayList<>();

    /**
     * Initializes the repository by loading data from files if not already loaded.
     * Lazy initialization ensures data is loaded only when needed.
     */
    private void initialize() {
        if (capList.isEmpty()) {
            capList = readLinesCap(heap);
        }
        if (capIndex.isEmpty()) {
            capIndex = readLinesIndex(indexFile);
        }
        if (brandList.isEmpty()) {
            brandList = readLinesBrandIndex(brandIndexFile);
        }
    }

    @Override
    public List<Cap> findAll() {
        initialize();
        return new ArrayList<>(capList); // Return defensive copy
    }

    /**
     * Checks if a brand exists in the brand index.
     *
     * @param brand the brand name to check
     * @return true if the brand exists, false otherwise
     */
    private boolean existBrand(String brand) {
        return brandList.stream()
                .anyMatch(index -> index.getBrand().equalsIgnoreCase(brand));
    }

    @Override
    public Cap save(Cap cap) {
        if (cap.getId() <= 0) {
            throw new IllegalArgumentException("The ID must be positive");
        }

        initialize();

        // Use B+ tree to check if ID already exists - O(log n) operation
        Integer existingPosition = capIndex.search(cap.getId());

        if (existingPosition != null) {
            throw new RuntimeException(String.format("A cap with ID %d already exists", cap.getId()));
        }

        // Add cap to the end of the list
        int newPosition = capList.size();
        capList.add(cap);

        // Insert into B+ tree index - O(log n) operation
        capIndex.insert(cap.getId(), newPosition);

        // Handle brand index
        handleBrandIndex(cap);

        // Persist changes to files
        appendLine(CAP_FORMATTER.capToText(cap), heap);
        persistIndex();

        log.info("Cap with ID {} saved successfully", cap.getId());
        return cap;
    }

    /**
     * Handles brand index updates when saving a cap.
     * Creates a new brand index if the brand doesn't exist, or updates existing one.
     *
     * @param cap the cap being saved
     */
    private void handleBrandIndex(Cap cap) {
        boolean brandExists = existBrand(cap.getBrand());

        if (!brandExists) {
            // Create new brand index
            BrandIndex brandIndex = new BrandIndex.BrandIndexBuilder()
                    .setBrand(cap.getBrand().toLowerCase())
                    .build();
            brandIndex.appendCap(cap.getId());
            brandList.add(brandIndex);

            appendLine(BRAND_INDEX_FORMATTER.brandIndexToText(brandIndex), brandIndexFile);
        } else {
            // Update existing brand index
            brandList.stream()
                    .filter(b -> b.getBrand().equalsIgnoreCase(cap.getBrand()))
                    .findFirst()
                    .ifPresent(b -> b.appendCap(cap.getId()));

            // Persist all brand indices
            persistBrandIndex();
        }
    }

    @Override
    public Cap findById(Long id) {
        initialize();

        // Use B+ tree for O(log n) lookup
        Integer position = capIndex.search(id);

        if (position == null) {
            throw new RuntimeException(String.format("No cap found with ID %d", id));
        }

        if (position < 0 || position >= capList.size()) {
            log.error("Index corruption detected: position {} out of bounds for ID {}", position, id);
            throw new CapDatabaseException("Index corruption detected");
        }

        return capList.get(position);
    }

    @Override
    public List<Cap> findByBrand(String brand) {
        initialize();

        if (!existBrand(brand)) {
            throw new RuntimeException(String.format("No caps found with brand: %s", brand));
        }

        // Get all cap IDs for the brand
        List<Long> capIds = brandList.stream()
                .filter(b -> b.getBrand().equalsIgnoreCase(brand))
                .findFirst()
                .map(BrandIndex::getCaps)
                .orElseThrow(() -> new RuntimeException("Brand index not found"));

        // Use B+ tree to efficiently retrieve each cap by ID
        return capIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    /**
     * Performs a range query on caps by ID.
     * Leverages the B+ tree's efficient range query capability.
     *
     * @param startId the starting ID (inclusive)
     * @param endId   the ending ID (inclusive)
     * @return list of caps in the ID range
     */
    public List<Cap> findByIdRange(Long startId, Long endId) {
        initialize();

        // Use B+ tree's range query - efficient due to linked leaf nodes
        List<Map.Entry<Long, Integer>> rangeEntries = capIndex.rangeQuery(startId, endId);

        return rangeEntries.stream()
                .map(entry -> capList.get(entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of caps in the repository.
     *
     * @return the cap count
     */
    public int getCapCount() {
        initialize();
        return capIndex.size();
    }

    // File I/O operations

    /**
     * Reads all cap records from the data file.
     *
     * @param resource the file resource containing cap data
     * @return list of caps
     */
    private List<Cap> readLinesCap(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(CAP_FORMATTER::TextToCap)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error reading cap data file", e);
            throw new CapDatabaseException("Failed to read cap data: " + e.getMessage());
        }
    }

    /**
     * Reads all brand index records from file.
     *
     * @param resource the file resource containing brand index data
     * @return list of brand indices
     */
    private List<BrandIndex> readLinesBrandIndex(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(BRAND_INDEX_FORMATTER::textToBrandIndex)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error reading brand index file", e);
            throw new CapDatabaseException("Failed to read brand index: " + e.getMessage());
        }
    }

    /**
     * Reads the B+ tree index from file and reconstructs it.
     * Each line in the index file is in format: id,position
     *
     * @param resource the file resource containing index data
     * @return reconstructed B+ tree
     */
    private BPlusTree<Long, Integer> readLinesIndex(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            BPlusTree<Long, Integer> tree = new BPlusTree<>(4);

            reader.lines()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {
                        String[] values = line.split(",");
                        if (values.length == 2) {
                            try {
                                Long id = Long.parseLong(values[0].trim());
                                Integer position = Integer.parseInt(values[1].trim());
                                tree.insert(id, position);
                            } catch (NumberFormatException e) {
                                log.warn("Skipping malformed index line: {}", line);
                            }
                        }
                    });

            log.info("Loaded {} entries into B+ tree index", tree.size());
            return tree;
        } catch (IOException e) {
            log.error("Error reading index file", e);
            throw new CapDatabaseException("Failed to read index: " + e.getMessage());
        }
    }

    /**
     * Appends a line to a file resource.
     *
     * @param line     the line to append
     * @param resource the target file resource
     */
    private void appendLine(String line, Resource resource) {
        try (FileWriter writer = new FileWriter(resource.getFile(), true)) {
            writer.append("\n").append(line);
        } catch (IOException e) {
            log.error("Error appending to file", e);
            throw new CapDatabaseException("Failed to append data: " + e.getMessage());
        }
    }

    /**
     * Overwrites a file with new content.
     *
     * @param resource the target file resource
     * @param content  the new content
     */
    private void overwrite(Resource resource, String content) {
        try (FileWriter writer = new FileWriter(resource.getFile(), false)) {
            writer.write(content);
        } catch (IOException e) {
            log.error("Error overwriting file", e);
            throw new CapDatabaseException("Failed to overwrite file: " + e.getMessage());
        }
    }

    /**
     * Persists the entire B+ tree index to file.
     * Each entry is written in the format: id,position
     */
    private void persistIndex() {
        StringBuilder content = new StringBuilder();

        // Use B+ tree's getAll() to retrieve all entries in sorted order
        List<Map.Entry<Long, Integer>> allEntries = capIndex.getAll();

        for (Map.Entry<Long, Integer> entry : allEntries) {
            if (content.length() > 0) {
                content.append("\n");
            }
            content.append(entry.getKey()).append(",").append(entry.getValue());
        }

        overwrite(indexFile, content.toString());
        log.debug("Persisted {} index entries", allEntries.size());
    }

    /**
     * Persists all brand indices to file.
     */
    private void persistBrandIndex() {
        StringBuilder content = new StringBuilder();

        for (BrandIndex brandIndex : brandList) {
            if (content.length() > 0) {
                content.append("\n");
            }
            content.append(BRAND_INDEX_FORMATTER.brandIndexToText(brandIndex));
        }

        overwrite(brandIndexFile, content.toString());
        log.debug("Persisted {} brand indices", brandList.size());
    }
}