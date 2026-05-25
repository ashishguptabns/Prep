package LLD.MessageBrokerApp.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.model.Message;

public class FileMessageStore implements MessageStore {
    private static final String DELIMITER = "\t";
    private final Path filePath;

    public FileMessageStore(String fileName) {
        try {
            this.filePath = Path.of(fileName);
            if (!Files.exists(filePath)) {
                Path parent = filePath.getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to initialize message store", e);
        }
    }

    @Override
    public synchronized Message persist(Message message) {
        try {
            long offset = Files.readAllLines(filePath, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .map(this::parse)
                    .filter(existing -> existing.getTopic().equals(message.getTopic()) && existing.getPartition() == message.getPartition())
                    .mapToLong(Message::getOffset)
                    .max()
                    .orElse(-1L) + 1;
            Message persisted = new Message(message.getTopic(), message.getPartition(), offset, message.getPayload());
            String line = encode(persisted.getTopic()) + DELIMITER + persisted.getPartition() + DELIMITER + persisted.getOffset() + DELIMITER + encode(persisted.getPayload()) + System.lineSeparator();
            Files.writeString(filePath, line, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
            return persisted;
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to persist message", e);
        }
    }

    @Override
    public synchronized void remove(Message message) {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            String expected = encode(message.getTopic()) + DELIMITER + message.getPartition() + DELIMITER + message.getOffset() + DELIMITER + encode(message.getPayload());
            boolean removed = false;
            List<String> remaining = new ArrayList<>();
            for (String line : lines) {
                if (!removed && line.equals(expected)) {
                    removed = true;
                    continue;
                }
                remaining.add(line);
            }
            Files.write(filePath, remaining, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to remove persisted message", e);
        }
    }

    @Override
    public synchronized List<Message> loadAll() {
        try {
            List<Message> messages = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                messages.add(parse(line));
            }
            return messages;
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to load persisted messages", e);
        }
    }

    @Override
    public synchronized List<Message> fetch(String topic, int partition, long fromOffset) {
        try {
            List<Message> messages = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                Message message = parse(line);
                if (message.getTopic().equals(topic) && message.getPartition() == partition && message.getOffset() >= fromOffset) {
                    messages.add(message);
                }
            }
            return messages;
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to fetch persisted messages", e);
        }
    }

    private Message parse(String line) {
        String[] parts = line.split(DELIMITER, 4);
        if (parts.length != 4) {
            throw new MessageBrokerException("Corrupted message line: " + line);
        }
        return new Message(decode(parts[0]), Integer.parseInt(parts[1]), Long.parseLong(parts[2]), decode(parts[3]));
    }

    private long parseOffset(String line) {
        String[] parts = line.split(DELIMITER, 4);
        return parts.length == 4 ? Long.parseLong(parts[2]) : -1L;
    }

    private String encode(String value) {
        return value.replace("\\", "\\\\").replace(DELIMITER, "\\t");
    }

    private String decode(String value) {
        return value.replace("\\t", DELIMITER).replace("\\\\", "\\");
    }
}

