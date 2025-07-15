package guc.internship.modelscrapper.service.estimator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.*;

public class StlParser implements EstimatingStrategy {
    @Override
    public String getVolume(File stlFile) {
        try (FileInputStream fis = new FileInputStream(stlFile)) {
            byte[] header = new byte[80];
            fis.read(header);
            String headerStr = new String(header);
            boolean isAscii = headerStr.trim().toLowerCase().startsWith("solid");
            fis.close();

            double volume;
            if (isAscii && isAsciiStl(stlFile)) {
                volume = parseAsciiStl(stlFile);
            } else {
                volume = parseBinaryStl(stlFile);
            }

            return String.format("%.2f", Math.abs(volume));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private boolean isAsciiStl(File stlFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile))) {
            String line;
            int lines = 0;
            while ((line = reader.readLine()) != null && lines < 20) {
                if (line.trim().toLowerCase().startsWith("facet")) {
                    return true;
                }
                lines++;
            }
        }
        return false;
    }

    private double parseAsciiStl(File stlFile) throws IOException {
        Pattern vertexPattern = Pattern.compile("vertex\\s+([\\d\\.-eE]+)\\s+([\\d\\.-eE]+)\\s+([\\d\\.-eE]+)");
        double[][] vertices = new double[3][3];
        int vertexCount = 0;
        double volume = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = vertexPattern.matcher(line.trim());
                if (matcher.matches()) {
                    for (int i = 0; i < 3; i++) {
                        vertices[vertexCount][i] = Double.parseDouble(matcher.group(i + 1));
                    }
                    vertexCount++;
                    if (vertexCount == 3) {
                        volume += signedVolumeOfTriangle(vertices[0], vertices[1], vertices[2]);
                        vertexCount = 0;
                    }
                }
            }
        }
        return volume;
    }

    private double parseBinaryStl(File stlFile) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(stlFile)))) {
            byte[] header = new byte[80];
            dis.readFully(header);
            int numTriangles = Integer.reverseBytes(dis.readInt());
            double volume = 0.0;
            for (int i = 0; i < numTriangles; i++) {
                dis.skipBytes(12);
                double[][] vertices = new double[3][3];
                for (int v = 0; v < 3; v++) {
                    vertices[v][0] = readLittleEndianFloat(dis);
                    vertices[v][1] = readLittleEndianFloat(dis);
                    vertices[v][2] = readLittleEndianFloat(dis);
                }
                volume += signedVolumeOfTriangle(vertices[0], vertices[1], vertices[2]);
                dis.skipBytes(2);
            }
            return volume;
        }
    }

    private float readLittleEndianFloat(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[4];
        dis.readFully(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private double signedVolumeOfTriangle(double[] p1, double[] p2, double[] p3) {
        return (
            (p1[0] * p2[1] * p3[2] +
             p2[0] * p3[1] * p1[2] +
             p3[0] * p1[1] * p2[2] -
             p1[0] * p3[1] * p2[2] -
             p2[0] * p1[1] * p3[2] -
             p3[0] * p2[1] * p1[2]) / 6.0
        );
    }

    @Override
    public String getWeight(File stlFile, String material) {
        return "";
    }
}
