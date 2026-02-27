package com.example.gd.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private final Path storageLocation;

    public DocumentService(@Value("${app.documents.path:documents}") String storagePath) {
        this.storageLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento", e);
        }
    }

    public String storeDocument(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String filename = timestamp + "_" + uniqueId + extension;

        Path targetLocation = this.storageLocation.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation);

        return filename;
    }

    public List<String> listDocuments() throws IOException {
        try (Stream<Path> paths = Files.list(storageLocation)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    public Resource loadDocument(String filename) throws MalformedURLException {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Nombre de archivo no válido");
        }
        Path file = storageLocation.resolve(filename).normalize();
        if (!file.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Documento no encontrado: " + filename);
        }
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new IllegalArgumentException("Documento no encontrado: " + filename);
    }
}
