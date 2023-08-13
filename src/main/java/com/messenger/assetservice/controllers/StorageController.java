package com.messenger.assetservice.controllers;

import com.messenger.assetservice.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/file")
@Tag(name = "Файлы", description = "Операции с загрузкой и скачиванием файлов")
public class StorageController {

    @Autowired
    private StorageService service;

    @PostMapping("/upload")
    @Operation(summary = "Загрузить файл", description = "Загружает файл на сервер")
    @ApiResponse(responseCode = "200", description = "Файл успешно загружен")
    @ApiResponse(responseCode = "400", description = "Ошибка при загрузке файла")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Файл для загрузки")
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Имя пользователя")
            @RequestParam("username") String username
    ) {
        return new ResponseEntity<>(service.uploadFile(file, username), HttpStatus.OK);
    }

    @GetMapping("/download")
    @Operation(summary = "Скачать файл", description = "Скачивает файл по его URL")
    @ApiResponse(responseCode = "200", description = "Файл успешно скачан")
    @ApiResponse(responseCode = "404", description = "Файл не найден")
    public ResponseEntity<byte[]> downloadFile(
            @Parameter(description = "URL файла для скачивания")
            @RequestParam("file-url") String fileUrl
    ) {
        byte[] data = service.downloadFile(fileUrl);
        return ResponseEntity
                .ok()
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileUrl + "\"")
                .body(data);
    }

    @DeleteMapping("/delete/{file-url}")
    @Operation(summary = "Удалить файл", description = "Удаляет файл по его URL")
    @ApiResponse(responseCode = "200", description = "Файл успешно удален")
    @ApiResponse(responseCode = "404", description = "Файл не найден")
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "URL файла для удаления")
            @PathVariable("file-url") String fileUrl
    ) {
        return new ResponseEntity<>(service.deleteFile(fileUrl), HttpStatus.OK);
    }
}
