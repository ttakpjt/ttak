package com.ttak.backend.domain.observe.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/observer/application")
@Tag(name = "application API", description = "사용금지 어플리케이션 설정 관련 api")
@CrossOrigin("*")
public class AppController {


}
