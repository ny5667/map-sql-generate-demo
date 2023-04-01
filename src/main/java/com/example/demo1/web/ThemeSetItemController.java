//package com.example.demo1.web;
//
//import com.example.demo1.persistence.repo.ThemeSetItemRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/setItems")
//public class ThemeSetItemController {
//
//    @Autowired
//    private ThemeSetItemRepository themeSetItemRepository;
//
//    @GetMapping
//    public Iterable findAll() {
//        return themeSetItemRepository.findAll();
//    }
//
////    @GetMapping("/themeSetId/{themeSetId}")
////    public List findByThemeSetId(@PathVariable Long themeSetId) {
////        return themeSetItemRepository.findByThemeSetId(themeSetId);
////    }
//
//}