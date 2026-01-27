package br.start.up.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Value("${api.path.dir}")
    private String PATH_DIR;

    public String upload(MultipartFile file){
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is empty");
        }

        try{
            Path path = Path.of(PATH_DIR, file.getOriginalFilename()).normalize().toAbsolutePath();

            Files.createDirectories(path.getParent());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            return file.getOriginalFilename();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public Resource download(String uri){
        try {
            Path filePath = Paths.get(PATH_DIR).resolve(uri).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<String> listFiles(String uri) {
        List<String> fileList = new ArrayList<>();

        File directory = new File(Path.of(PATH_DIR, uri).normalize().toUri());

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileList.add(file.getName());
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Lista de arquivos no diretório vazia.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diretório não encontrado ou não é um diretório válido.");
        }

        return fileList;
    }
}
