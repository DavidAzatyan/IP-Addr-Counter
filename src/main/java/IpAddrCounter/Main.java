package IpAddrCounter;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

public class Main {

    private static final int PARTITIONS = 1000; // Number of partitions
    private static final String PARTITION_PREFIX = "partition_";

    public static void main(String[] args) throws IOException {
        String inputFile = "ip-addr.txt"; // provide path of ip-addr.txt

        partitionFile(inputFile);
        int uniqueCount = countUniqueIPs();
        System.out.println("Unique IP Addresses: " + uniqueCount);

    }

    private static void partitionFile(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        CRC32 crc32 = new CRC32();
        Map<Integer, BufferedWriter> partitionWriters = new HashMap<>();

        String ip;
        while ((ip = reader.readLine()) != null) {
            crc32.reset();
            crc32.update(ip.getBytes());
            int partition = (int) (crc32.getValue() % PARTITIONS);

            if (!partitionWriters.containsKey(partition)) {
                partitionWriters.put(partition, new BufferedWriter(new FileWriter(PARTITION_PREFIX + partition)));
            }
            partitionWriters.get(partition).write(ip);
            partitionWriters.get(partition).newLine();
        }
        reader.close();

        for (BufferedWriter writer : partitionWriters.values()) {
            writer.close();
        }
    }

    private static int countUniqueIPs() throws IOException {
        int uniqueCount = 0;
        for (int i = 0; i < PARTITIONS; i++) {
            File partitionFile = new File(PARTITION_PREFIX + i);
            if (partitionFile.exists()) {
                BloomFilter bloomFilter = new BloomFilter(100_000_000, 0.01);
                Set<String> partitionUniqueIPs = new HashSet<>();
                BufferedReader reader = new BufferedReader(new FileReader(partitionFile));

                String ip;
                while ((ip = reader.readLine()) != null) {
                    if (!bloomFilter.mightContain(ip)) {
                        bloomFilter.add(ip);
                        partitionUniqueIPs.add(ip);
                    }
                }
                uniqueCount += partitionUniqueIPs.size();
                reader.close();
                partitionFile.delete(); // Clean up partition file
            }
        }
        return uniqueCount;
    }
}
