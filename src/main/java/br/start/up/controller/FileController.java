package br.start.up.controller;

import br.start.up.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/resources")
@RestController
public class FileController {

    @Autowired
    private FileService service;

    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(service.upload(file));
    }

    @GetMapping("/files")
    public ResponseEntity<?> listFiles() {
        return ResponseEntity.ok(service.listFiles("/"));
    }

    @GetMapping("/files/{path}")
    public ResponseEntity<?> listFiles(@PathVariable("path") String path) {
        return ResponseEntity.ok(service.listFiles(path));
    }

    @GetMapping("/download/{path}")
    public ResponseEntity<?> download(@PathVariable("path") String path) {
        var resource = service.download(path);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
