package fa.training.components.service;

import fa.training.components.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    Image addImage(MultipartFile multipartFile) throws IOException;

    byte[] getImage(String fileName);

}
