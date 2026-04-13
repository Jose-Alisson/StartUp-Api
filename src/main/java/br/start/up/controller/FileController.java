package br.start.up.controller;

import br.start.up.services.FileService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/resources")
@RestController
public class FileController {

    @Autowired
    private FileService service;

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping({"/upload", "/upload/{urlPath:.+}"})
    public ResponseEntity<?> upload(
            @PathVariable(value = "urlPath", required = false) String path,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.upload(path, file));
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @GetMapping("/files")
    public ResponseEntity<?> listFiles() {
        return ResponseEntity.ok(service.listFiles("/"));
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @GetMapping("/files/{path}")
    public ResponseEntity<?> listFiles(@PathVariable("path") String path) {
        return ResponseEntity.ok(service.listFiles(path));
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @GetMapping({"/download/{path:.+}"})
    public ResponseEntity<?> download(@PathVariable("path") String path) {
        var resource = service.download(path);
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/octet-stream")).body(resource);
    }

    @GetMapping({"/download/public/{path:.+}"})
    public ResponseEntity<?> downloadPublic(@PathVariable("path") String path) {
        var resource = service.downloadFromPublic(path);
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/octet-stream")).body(resource);
    }
}
