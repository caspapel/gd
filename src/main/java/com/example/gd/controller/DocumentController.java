package com.example.gd.controller;

import com.example.gd.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/addDocument")
    public String addDocumentForm(Model model) {
        return "addDocument";
    }

    @PostMapping("/addDocument")
    public String addDocument(@RequestParam("document") MultipartFile file, Model model) {
        try {
            String filename = documentService.storeDocument(file);
            model.addAttribute("success", true);
            model.addAttribute("message", "Documento guardado correctamente: " + filename);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Error al guardar: " + e.getMessage());
        }
        return "addDocument";
    }

    @GetMapping("/downloadDocument")
    public String downloadDocumentList(Model model) {
        try {
            List<String> documents = documentService.listDocuments();
            model.addAttribute("documents", documents);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "downloadDocument";
    }

    @GetMapping("/downloadDocument/file/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = documentService.loadDocument(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
