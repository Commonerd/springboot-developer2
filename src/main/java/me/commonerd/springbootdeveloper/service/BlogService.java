package me.commonerd.springbootdeveloper.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.VideoFile;
import me.commonerd.springbootdeveloper.dto.AddArticleRequest;
import me.commonerd.springbootdeveloper.dto.ArticleListViewResponse;
import me.commonerd.springbootdeveloper.dto.UpdateArticleRequest;
import me.commonerd.springbootdeveloper.repository.BlogRepository;
import me.commonerd.springbootdeveloper.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;


    private final ImageRepository imageRepository;

    private final LikeService likeService;

    // BlogService.java 안에서
    public Article save(AddArticleRequest request, String userName) throws Exception {
        Article article = request.toEntity(userName);
        boolean shouldLoadExistingImageFiles = false; // 이미지 파일 개수 제한을 검사하지 않음

        // 이미지 파일을 여기서 처리합니다
        if(request.getImageFiles() != null) {
            List<ImageFile> imageFiles = saveImageFiles(request.getImageFiles(), article, shouldLoadExistingImageFiles);
            article.setImageFiles(imageFiles);
        }

        return blogRepository.save(article);
    }


    private List<ImageFile> saveImageFiles(List<MultipartFile> imageFiles, Article article, boolean shouldLoadExistingImageFiles) throws Exception {
        List<ImageFile> savedImageFiles = new ArrayList<>();

        // 이미지 파일 개수 제한 로직
        int totalImageFilesCount = imageFiles.size();
        if (shouldLoadExistingImageFiles) {
            List<ImageFile> existingImageFiles = imageRepository.findByArticle(article);
            totalImageFilesCount += existingImageFiles.size();
        }

        // 이미지 파일 개수 제한  3개
        int imageFileLimit = 3;

        if (totalImageFilesCount <= imageFileLimit) {
            for (MultipartFile imageFile : imageFiles) {
                try {
                    // 파일 정보 가져오기
                    String originalFilename = imageFile.getOriginalFilename();
                    String contentType = imageFile.getContentType();
                    byte[] fileBytes = imageFile.getBytes();

                    // 파일 이름에서 확장자를 제외한 부분 추출
                    int lastDotIndex = originalFilename.lastIndexOf(".");
                    String filenameWithoutExtension = originalFilename.substring(0, lastDotIndex);


                    // 파일 확장자 가져오기
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

                    // UUID 생성
                    String uuid = UUID.randomUUID().toString();

                    // 저장할 파일명 생성 (확장자를 제외파일명 + UUID + 현재 시간 + 확장자)
                    String filename = filenameWithoutExtension + "_" + uuid + "_" + System.currentTimeMillis() + fileExtension;

                    String storagePath = "C:/Users/41m/Documents/projects/spring-developer2/src/main/resources/static/article_img/";

                    // 파일 저장 로직 구현
                    File savedFile = new File(storagePath + filename);
                    Files.write(savedFile.toPath(), fileBytes);

                    // ImageFile 엔티티 생성 및 저장
                    ImageFile image = new ImageFile();
                    image.setArticle(article);
                    image.setFilename(filename);
                    image.setContentType(contentType);
                    savedImageFiles.add(image);

                } catch (IOException e) {
                    e.printStackTrace();
                    // 파일 저장에 실패한 경우 처리할 로직
                }
            }
        } else {
                throw new Exception("Image file limit exceeded.");
            }

        return savedImageFiles;
    }


    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Page<Article> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return blogRepository.findAll(pageable);
    }


    /*public List<Article> findSearch(String title) {
        return blogRepository.findByTitleContaining(title);
    }*/

    //2023.06.06.by commonerd : 작성자,제목,내용 검색기능 추가, index-100
    //>>>>> 100
    public List<ArticleListViewResponse> searchArticles(String keyword) {
        //Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // return blogRepository.searchArticles(keyword);
        List<Article> articles = blogRepository.searchArticles(keyword);
        List<ArticleListViewResponse> responseList = new ArrayList<>();

        for (Article article : articles) {
            int likeCount = likeService.getLikeCount(article.getId());
            boolean liked = likeService.isLiked(article.getId(), article.getAuthor(), "syh712@gmail.com");
            responseList.add(new ArticleListViewResponse(article, likeCount, liked));
        }

        return responseList;
    }
    //<<<<< 100


    public List<Object[]> findArticleRanking() {
        return blogRepository.findTop5Ranking();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);

        // 이미지 파일도 함께 삭제
        List<ImageFile> imageFiles = article.getImageFiles();
        if (imageFiles != null) {
            for (ImageFile imageFile : imageFiles) {
                deleteAllImage(imageFile.getId()); // 이미지 삭제 로직을 호출
            }
        }

        blogRepository.delete(article);
    }

    public void deleteAllImage(Long id) {
        ImageFile imageFile = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + id));

        // 이미지 파일을 실제로 삭제하는 코드를 추가해야 합니다.
        // 예: File 클래스를 사용하여 파일 삭제
        File fileToDelete = new File("C:/Users/41m/Documents/projects/spring-developer2/src/main/resources/static/article_img/" + imageFile.getFilename());
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }

        imageRepository.delete(imageFile);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) throws Exception {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        boolean shouldLoadExistingImageFiles = true; // 이미지 파일 개수 제한을 검사함

        // 비디오와 이미지 파일을 여기서 처리합니다
        if(request.getImageFiles() != null) {
            List<ImageFile> imageFiles = saveImageFiles(request.getImageFiles(), article, shouldLoadExistingImageFiles);
            article.setImageFiles(imageFiles);
        }


        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }

    public void deleteImage(Long id) {
        ImageFile imageFile = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + id));

        // 이미지 파일을 실제로 삭제하는 코드를 추가해야 합니다.
        // 예: File 클래스를 사용하여 파일 삭제
        File fileToDelete = new File("C:/Users/41m/Documents/projects/spring-developer2/src/main/resources/static/article_img/" + imageFile.getFilename());
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }

        imageRepository.delete(imageFile);
    }



}