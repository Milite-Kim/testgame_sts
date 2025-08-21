package com.milite.mapper;

import com.milite.dto.PlayerDto;

public interface CharacterStatusMapper {
	public PlayerDto getPlayerInfo(String PlayerID);
	
	public int replacePhoenixFeathers(String PlayerID);
}
