package com.milite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.milite.dto.ComStatusDto;
import com.milite.service.ComStatusService;

import lombok.*;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/CS/*")
@RestController
public class ComStatusController {
	
	@Setter(onMethod_ = @Autowired)
	private ComStatusService service;

	@RequestMapping("/idinfo")
	public ComStatusDto getInfo(Integer id) {
		System.out.println("정보 조회" + id);
		return service.getInfo(id);
	}
	
	@RequestMapping("/updateCS")
	public void updateComStatus(ComStatusDto dto) {
		service.updateStatus(dto);
	}
	
	@RequestMapping("/resetChar")
	public void resetChar(Integer id) {
		service.resetChar(id);
	}
}
