package com.milite.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactDto {
	int ArtifactID;
	String ArtifactName;
	String ArtifactJob;
	String Session;
	String Effect;
	String Description;
}