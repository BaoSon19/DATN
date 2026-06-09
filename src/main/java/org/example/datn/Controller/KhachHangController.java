package org.example.datn.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.KhachHang;
import org.example.datn.Service.KhachHangService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/khachHang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService service;

    @GetMapping("/hien-thi")
    public String hienThi(Model model) {
        model.addAttribute("listKhachHang", service.findAll());
        model.addAttribute("khachHang", new KhachHang());
        return "khach_hang";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("khachHang") KhachHang kh,
                      BindingResult result,
                      Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listKhachHang", service.findAll());
            return "khach_hang";
        }

        service.them(kh);
        return "redirect:/khachHang/hien-thi";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("khachHang",
                service.findById(id).orElse(new KhachHang()));

        model.addAttribute("listKhachHang", service.findAll());
        return "khach_hang";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute KhachHang kh) {
        service.sua(kh);
        return "redirect:/khachHang/hien-thi";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.xoa(id);
        return "redirect:/khachHang/hien-thi";
    }
}