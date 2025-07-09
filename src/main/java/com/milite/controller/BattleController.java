package com.milite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.milite.dto.BattleResultDto;
import com.milite.service.BattleService;

import lombok.*;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/CS")
@RestController
public class BattleController {

	@Setter(onMethod_ = @Autowired)
	private BattleService service;

	@RequestMapping("/battle")
	@ResponseBody
	public BattleResultDto battle(@RequestParam("Player") Integer PlayerId, @RequestParam("Enemy") Integer EnemyId) {
		System.out.println("컨트롤러 진입 - Player: " + PlayerId + ", Enemy: " + EnemyId);
		try {
			return service.battle(PlayerId, EnemyId);
		} catch (Exception e) {
			e.printStackTrace();
			return new BattleResultDto("전투 중 오류 발생", 0, 0, false, false, false);
		}
	}
	
	@RequestMapping("/battle/turn")
	@GetMapping
	public @ResponseBody BattleResultDto battleTurnTest() {
	    // 테스트용 playerId=1, enemyId=2 고정
	    return service.battle(1, 2);
	}
}
