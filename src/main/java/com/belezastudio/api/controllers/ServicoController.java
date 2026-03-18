package com.belezastudio.api.controllers;

import com.belezastudio.api.services.ServicoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private ServicoService servicoService;
    
}
