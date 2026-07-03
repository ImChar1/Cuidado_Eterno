package com.cuidadoeterno.backend.modules.usuario.service;

import com.cuidadoeterno.backend.shared.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private final String bucketName;
    private final S3Client s3Client;

    public S3ServiceImpl(
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.s3.region}") String regionString) {
        
        this.bucketName = bucketName;
        // El cliente usa DefaultCredentialsProvider por defecto.
        // Cuando corras esto en AWS EC2 (Etapa 2), tomará las credenciales del LabRole automáticamente.
        // Si lo corres en tu PC local, asegúrate de tener tu archivo ~/.aws/credentials configurado.
        this.s3Client = S3Client.builder()
                .region(Region.of(regionString))
                .build();
    }

    @Override
    public String subirArchivo(MultipartFile archivo, String carpetaDestino) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("El archivo proporcionado está vacío", HttpStatus.BAD_REQUEST);
        }

        try {
            // 1. Generar un nombre único para evitar sobreescribir archivos
            String originalFilename = archivo.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String nombreUnico = UUID.randomUUID().toString() + extension;
            
            // 2. Definir la ruta completa en S3
            String objectKey = carpetaDestino + nombreUnico;

            // 3. Preparar la solicitud de subida
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(archivo.getContentType())
                    .build();

            // 4. Subir a S3
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize()));

            // 5. Retornar la URL pública generada
            // Estructura oficial de AWS: https://[bucket].s3.[region].amazonaws.com/[key]
            return "https://" + bucketName + ".s3.amazonaws.com/" + objectKey;

        } catch (IOException e) {
            throw new BusinessException("Error al leer el archivo para subir a S3: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new BusinessException("Error de conexión con AWS S3: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}