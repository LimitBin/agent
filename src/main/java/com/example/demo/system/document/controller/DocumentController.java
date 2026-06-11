package com.example.demo.system.document.controller;


import com.example.demo.common.api.Result;
import com.example.demo.system.document.entity.Document;
import com.example.demo.system.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public Result<Document> upload(@RequestParam("file") MultipartFile file) {
        try{
            Document doc =  documentService.upload(file);
            return Result.success("上传成功",doc);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Document>> list (){
        List<Document> list = documentService.listByUser();
        return Result.success("查询成功",list);
    }

    @DeleteMapping("/{id}")
    public Result<Document> delete(@PathVariable Long id){
        try{
            documentService.delete(id);
            return Result.success("删除成功",null);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
}
