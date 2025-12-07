package in.dinesh.controller;

import in.dinesh.dto.ImageDto;
import in.dinesh.exceptions.ResourceNotFoundException;
import in.dinesh.model.Image;
import in.dinesh.service.image.IImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IImageService imageService;

    private Image image;
    private ImageDto imageDto;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() throws Exception {
        image = new Image();
        image.setId(1L);
        image.setFileType("image/png");
        image.setFileName("test.png");
        byte[] content = "test image content".getBytes();
        Blob blob = new SerialBlob(content);
        image.setImage(blob);

        imageDto = new ImageDto();
        imageDto.setId(1L);
        imageDto.setFileName("test.png");

        file = new MockMultipartFile("file", "test.png", "image/png", "test image content".getBytes());
    }

    @Test
    void saveImages_Success() throws Exception {
        List<MockMultipartFile> files = Collections.singletonList(file);
        given(imageService.saveImages(anyLong(), any())).willReturn(Collections.singletonList(imageDto));

        mockMvc.perform(multipart("/api/v1/images/upload")
                .file("files", file.getBytes()) // The param name in controller is "files" (List)
                .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Upload success!"));
    }

    @Test
    void saveImages_Error() throws Exception {
        given(imageService.saveImages(anyLong(), any())).willThrow(new RuntimeException("Error"));

        mockMvc.perform(multipart("/api/v1/images/upload")
                .file("files", file.getBytes())
                .param("productId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Upload failed!"));
    }

    @Test
    void downloadImage_Success() throws Exception {
        given(imageService.getImageById(1L)).willReturn(image);

        mockMvc.perform(get("/api/v1/images/image/download/{imageId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.png\""));
    }

    @Test
    void updateImage_Success() throws Exception {
        given(imageService.getImageById(1L)).willReturn(image);
        doNothing().when(imageService).updateImage(any(MockMultipartFile.class), anyLong());

        MockMultipartFile updateFile = new MockMultipartFile("file", "update.png", "image/png", "update".getBytes());

        mockMvc.perform(multipart("/api/v1/images/image/{imageId}/update", 1L)
                .file(updateFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update success!"));
    }

    @Test
    void updateImage_NotFound() throws Exception {
        given(imageService.getImageById(1L)).willThrow(new ResourceNotFoundException("Image not found!"));

        MockMultipartFile updateFile = new MockMultipartFile("file", "update.png", "image/png", "update".getBytes());

        mockMvc.perform(multipart("/api/v1/images/image/{imageId}/update", 1L)
                .file(updateFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Image not found!"));
    }

    @Test
    void updateImage_Error() throws Exception {
        given(imageService.getImageById(1L)).willReturn(null); // Return null to trigger 500 path

        MockMultipartFile updateFile = new MockMultipartFile("file", "update.png", "image/png", "update".getBytes());

        mockMvc.perform(multipart("/api/v1/images/image/{imageId}/update", 1L)
                .file(updateFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Update failed!"));
    }

    @Test
    void deleteImage_Success() throws Exception {
        given(imageService.getImageById(1L)).willReturn(image);
        doNothing().when(imageService).deleteImageById(1L);

        mockMvc.perform(delete("/api/v1/images/image/{imageId}/delete", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete success!"));
    }

    @Test
    void deleteImage_NotFound() throws Exception {
        given(imageService.getImageById(1L)).willThrow(new ResourceNotFoundException("Image not found!"));

        mockMvc.perform(delete("/api/v1/images/image/{imageId}/delete", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Image not found!"));
    }

    @Test
    void deleteImage_Error() throws Exception {
        given(imageService.getImageById(1L)).willReturn(null); // Return null to trigger 500

        mockMvc.perform(delete("/api/v1/images/image/{imageId}/delete", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Delete failed!"));
    }
}
