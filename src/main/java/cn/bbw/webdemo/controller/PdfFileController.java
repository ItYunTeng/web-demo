package cn.bbw.webdemo.controller;

import cn.bbw.webdemo.cache.FileCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author buliangliang
 * @version V1.0.0
 * @date 2021/8/30 9:39 下午
 * @since 1.0
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class PdfFileController {

    @PostMapping(value = "/pdfView", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> pdfView(HttpServletResponse res) {
        res.reset();
        res.setContentType("application/pdf");

        InputStream inputStream = FileCache.getInstance().get("");
        return new ResponseEntity<>("11111", HttpStatus.OK);
    }
}
