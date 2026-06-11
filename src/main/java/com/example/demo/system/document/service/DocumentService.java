package com.example.demo.system.document.service;


import com.example.demo.framework.secure.UserContext;
import com.example.demo.system.document.entity.Document;
import com.example.demo.system.document.mapper.DocumentMapper;
import com.example.demo.system.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private RagService ragService;

    private String uploadDir = System.getProperty("user.dir") + "/uploads";

    //允许的文件白名单
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "text/plain",
            "text/markdown",
            "text/html",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public Document upload(MultipartFile file) throws IOException {

        //校验文件类型
        String contentType = file.getContentType();
        if(contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("不支持的文件类型" + contentType);
        }

        //校验文件大小
        if(file.getSize() > 20*1024*1024) {
            throw new RuntimeException("文件大小不能超过20MB");
        }

        //生成唯一文件名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if(originalName != null && originalName.contains(".")){
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + extension;
        String filePath = uploadDir + File.separator +fileName;

        //保存文件到本地磁盘
        File dest = new File(filePath);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        //保存元数据到数据库
        Document doc = new Document();
        doc.setName(originalName);
        doc.setType(extension.replace(".", ""));
        doc.setSize(file.getSize());
        doc.setPath(filePath);
        doc.setStatus(1);  // 处理中
        doc.setUserId(UserContext.getUser().getId());
        doc.setTenantId(UserContext.getTenantId());
        documentMapper.insert(doc);

        // RAG 处理：解析 → 分块 → 向量化 → 存储
        try {
            ragService.ingestDocument(filePath);
            doc.setStatus(2);  // 已完成
        } catch (Exception e) {
            doc.setStatus(3);  // 失败
        }
        documentMapper.updateById(doc);

        return doc;
    }

    public List<Document> listByUser() {
        Long userId = UserContext.getUser().getId();
        return documentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Document>()
                        .eq("user_id", userId)
                        .eq("is_deleted", 0)
                        .orderByDesc("create_time")
        );
    }

    public void delete(Long id){
        Document doc = documentMapper.selectById(id);
        if(doc == null){
            throw new RuntimeException("文档不存在");
        }
        doc.setIsDeleted(1);
        documentMapper.updateById(doc);
    }
}
