package com.example.demo.system.document.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("document")
public class Document {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Long size;
    private String path;
    private Integer status;
    private Long userId;
    private String tenantId;
    private Integer isDeleted;
    private String createTime;
    private String updateTime;
}
