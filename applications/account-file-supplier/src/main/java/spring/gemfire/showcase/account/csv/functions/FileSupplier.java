package spring.gemfire.showcase.account.csv.functions;

import io.micrometer.common.util.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Watched for files in a given directory.
 *
 * @Author Gregory Green
 */
@Component
@Slf4j
public class FileSupplier implements Supplier<String> {
    private final WatchService watcher;
    private final Path dir;
    private final WatchKey registrationKey;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private long sleepDelay = 1000;

    public FileSupplier(@Value("${file.directory}") String directory, @Value("${file.count}") int count, @Value("${file.modification.delay.ms:1000}") long sleepDelay) throws IOException {
        if (StringUtils.isNotBlank(directory)) {
            this.dir = Paths.get(directory);
        } else {
//            this.dir = Paths.get("/workspace/BOOT-INF/classes/account-files").toAbsolutePath().normalize();
            this.dir = Paths.get("/").toAbsolutePath().normalize();
        }
        log.info("Reading file from directory : " + dir);
        log.info("file.directory: {} ", dir.toFile().getAbsolutePath());
        this.watcher = FileSystems.getDefault().newWatchService();
        this.registrationKey = dir.register(watcher,
                ENTRY_CREATE,
                ENTRY_MODIFY);

        CreateFile runnable = new CreateFile(this.dir.toString(), count);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @SneakyThrows
    @Override
    public String get() {

        if (queue.isEmpty())
            fillQueue();

        return queue.poll();
    }

    private void fillQueue() throws InterruptedException, IOException {
        try {
            var watchKey = watcher.take();

            for (WatchEvent<?> event : watchKey.pollEvents()) {
                var kind = event.kind();
                var watchEvent = (WatchEvent<Path>) event;
                var filePath = watchEvent.context();
                var file = Paths.get(dir.toFile().getAbsolutePath(), filePath.toFile().getName());

                log.info("Reading file: {}", file.toFile().getAbsolutePath());

                //watch for changes
                long previousFileSize;
                long currentFileSize;
                do {
                    previousFileSize = file.toFile().length();
                    Thread.sleep(sleepDelay);
                    currentFileSize = file.toFile().length();
                } while (currentFileSize != previousFileSize);

                try (Scanner reader = new Scanner(file)) {
                    while (reader.hasNextLine()) {
                        String line = reader.nextLine();
                        log.info(line);
                        queue.add(line);
                    }
                }
            }
        } finally {
            registrationKey.reset();
        }
    }

    public class CreateFile implements Runnable {
        int count = 0;
        String dir;

        public CreateFile(String dir, int count) {
            this.count = count;
            this.dir = dir;
        }

        @SneakyThrows
        public void run() {
            int accountId = 0;
            int i = 1;
            while (i <= count) {
                PrintWriter writer = new PrintWriter(dir + "/" + i + ".txt", "UTF-8");

                int j = 1;
                while (j <= 10) {
                    writer.println(++accountId + " ," + accountId);
                    j++;
                }
                writer.close();
                i++;
                Thread.sleep(1000);
            }
        }
    }
}

