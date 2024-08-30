package com.example.blog.Controller;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.Model.AccountVo;
import com.example.blog.Service.UserProfileService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/userProfile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // 使用相對路徑，上傳目錄將位於專案的根目錄中
    private static final String UPLOAD_DIR = "D:\\Project_ex\\Blog_Project\\frontend\\public\\UserImages\\";

    // 更新用戶名
    @PutMapping("update-username/{id}")
    public ResponseEntity<String> updateUsername(@PathVariable(value = "id") Long id,
            @RequestBody Map<String, String> requestBody) {
        String newUsername = requestBody.get("username");
        boolean update = userProfileService.updateUsername(id, newUsername);
        if (update) {
            return ResponseEntity.ok("用戶名更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用戶名更新失敗");
        }
    }

    // 更新電子郵件
    @PutMapping("update-email/{id}")
    public ResponseEntity<String> updateEmail(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        System.out.println(id);
        String newEmail = requestBody.get("email");
        boolean update = userProfileService.updateEmail(id, newEmail);
        if (update) {
            return ResponseEntity.ok("用戶電子郵件更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用戶電子郵件更新失敗");
        }
    }

    // 根據用戶名稱獲取用戶資料
    @GetMapping("/{id}")
    public ResponseEntity<AccountVo> getUserById(@PathVariable(value = "id") Long id) {
        AccountVo accountVo = userProfileService.getUserById(id);
        if (accountVo != null) {
            return ResponseEntity.ok(accountVo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/upload-image/{id}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @PathVariable(value = "id") Long id) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("請選擇一個檔案來上傳。");
        }

        // 圖片大小限制在10MB以下
        final long MAX_SIZE = 10 * 1024 * 1024;
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest().body("圖片大小超過10MB限制");
        }

        try {
            // 查詢資料庫以獲取原有的圖片路徑
            AccountVo accountVo = userProfileService.getUserById(id);
            String url = accountVo.getImagelink();
            if (url != null && !url.isEmpty()) {
                try {
                    // 解析 URL 並獲取圖片的本地路徑
                    URL urlObj = new URL(url);
                    String path = urlObj.getPath();
                    String localFilePath = UPLOAD_DIR + path.replace('/', File.separatorChar);

                    Path existingFilePath = Paths.get(localFilePath);
                    System.out.println("existingFilePath is " + existingFilePath);

                    // 如果存在原有的圖片，刪除它
                    if (Files.exists(existingFilePath)) {
                        Files.delete(existingFilePath); // 确保删除原有图片
                    }
                } catch (MalformedURLException e) {
                    System.err.println("圖片 URL 格式錯誤：" + e.getMessage());
                } catch (IOException e) {
                    System.err.println("刪除原有圖片時出錯：" + e.getMessage());
                }
            }

            // 生成唯一的檔案名稱
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            System.out.println("Saving new file to: " + filePath);

            // 確保目錄存在，創建上傳目錄（包括所有父目錄）
            Files.createDirectories(filePath.getParent());

            // 儲存檔案到指定目錄
            Files.write(filePath, file.getBytes());

            // 設置基礎 URL
            String baseUrl = "http://localhost:3000/";

            // 構建相對路徑（保持斜線）
            String relativeImagePath = "UserImages/" + fileName;

            // 組合完整的 URL
            String fullImageUrl = baseUrl + relativeImagePath;

            // 更新資料庫中的圖片路徑
            userProfileService.updateUserImagePath(id, fullImageUrl);

            // 構建返回的 JSON
            String jsonResponse = String.format("{\"filePath\":\"%s\"}", fullImageUrl);

            // 返回檔案的路徑
            return ResponseEntity.ok().body(jsonResponse);
        } catch (MalformedURLException e) {
            System.err.println("URL 格式錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("無效的圖片 URL 格式。");
        } catch (IOException e) {
            e.printStackTrace(); // 打印異常堆疊以幫助調試
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上傳檔案時發生錯誤。");
        } catch (Exception e) {
            e.printStackTrace(); // 處理潛在的其他異常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("處理請求時發生錯誤。");
        }
    }

}