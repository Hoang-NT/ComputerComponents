package fa.training.components.service.impl;

import fa.training.components.entity.Image;
import fa.training.components.repository.ImageRepository;
import fa.training.components.service.ImageService;
import fa.training.components.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Image addImage(MultipartFile file) throws IOException {
//        byte[] checkImage = getImage(file.getOriginalFilename());
//        if (checkImage != null) {
//            throw new MyException("405", "Image existed!");
//        }
        Image imageData = imageRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData != null) {
            return imageData;
        }
        return null;
    }

    public byte[] getImage(String fileName){
        Optional<Image> dbImageData = imageRepository.findByName(fileName);
        return ImageUtils.decompressImage(dbImageData.get().getImageData());
    }
}
