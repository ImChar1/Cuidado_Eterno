package com.cuidadoeterno.backend.modules.usuario.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    /**
     * Sube un archivo a Amazon S3 y retorna su URL pública.
     * @param archivo El archivo recibido desde Android.
     * @param carpetaDestino El prefijo/carpeta dentro del bucket (ej: "documentos/cuidadores/").
     */
    String subirArchivo(MultipartFile archivo, String carpetaDestino);
}