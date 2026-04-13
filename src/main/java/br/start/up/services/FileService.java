package br.start.up.services;

import br.start.up.detail.UserAuthLoader;
import br.start.up.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserAuthLoader authLoader;

    public String upload(String urlPath, MultipartFile file) {
        String filename = Paths.get(file.getOriginalFilename())
                .getFileName()
                .toString();
        return upload(urlPath, filename, file);
    }

    public  String upload(String urlPath, String filename, MultipartFile file){
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is empty");
        }

        try {
            Path path = (urlPath == null || urlPath.isBlank())
                    ? Path.of(PATH_DIR, filename)
                    : Path.of(PATH_DIR, urlPath, filename);

            path = path.normalize().toAbsolutePath();

            if (!path.startsWith(PATH_DIR)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid path"
                );
            }

            Files.createDirectories(path.getParent());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            return (urlPath == null || urlPath.isBlank())
                    ? filename
                    : urlPath + "/" + filename;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error uploading file",
                    e
            );
        }
    }

    public Resource download(String uri){
        try {
            Path filePath = Paths.get(PATH_DIR).resolve(uri).normalize();

            if (!filePath.startsWith(PATH_DIR)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid path"
                );
            }

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

    public Resource downloadFromPublic(String uri){
        return download("public/" + uri);
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
