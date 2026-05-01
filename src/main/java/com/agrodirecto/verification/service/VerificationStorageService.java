package com.agrodirecto.verification.service;

import com.agrodirecto.common.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VerificationStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final Path root;

    public VerificationStorageService(
            @Value("${app.verification.upload-dir:storage/verification-documents}") String uploadDirectory
    ) {
        this.root = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear el directorio de documentos de verificacion: " + root, exception);
        }
    }

    public StoredFile store(MultipartFile file, Long userId) {
        validate(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "document"
                : file.getOriginalFilename());
        String extension = resolveExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;
        Path userDirectory = root.resolve(String.valueOf(userId)).normalize();
        Path target = userDirectory.resolve(storedFilename).normalize();

        if (!target.startsWith(root)) {
            throw new BadRequestException("Nombre de archivo invalido");
        }

        try {
            Files.createDirectories(userDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!Files.isRegularFile(target) || Files.size(target) == 0) {
                throw new IllegalStateException("El archivo de verificacion no fue persistido correctamente: " + target);
            }
            return new StoredFile(target.toString(), originalFilename, file.getContentType());
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo guardar el documento de verificacion", exception);
        }
    }

    public Resource load(String fileUrl) {
        try {
            Path path = Paths.get(fileUrl).toAbsolutePath().normalize();
            if (!path.startsWith(root) || !Files.exists(path)) {
                throw new BadRequestException("El archivo solicitado no existe");
            }
            return new UrlResource(path.toUri());
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer el documento de verificacion", exception);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Debe adjuntar un archivo");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("El archivo no puede superar 10 MB");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Solo se permiten archivos PDF, JPG o PNG");
        }
    }

    private String resolveExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return filename.substring(index).toLowerCase(Locale.ROOT);
    }

    public record StoredFile(String fileUrl, String originalFilename, String mimeType) {
    }
}
